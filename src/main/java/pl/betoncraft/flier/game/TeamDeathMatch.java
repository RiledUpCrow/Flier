/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.core.Arena;
import pl.betoncraft.flier.api.core.Attacker;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.SidebarLine;
import pl.betoncraft.flier.api.core.Target;
import pl.betoncraft.flier.event.FlierPlayerSpawnEvent;
import pl.betoncraft.flier.util.LangManager;
import pl.betoncraft.flier.util.Utils;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * A simple team deathmatch game.
 *
 * @author Jakub Sapalski
 */
public class TeamDeathMatch extends DefaultGame {
	
	protected final Map<String, SimpleTeam> teams = new HashMap<>();
	protected final Map<UUID, SimpleTeam> players = new HashMap<>();
	
	protected final int suicideScore;
	protected final int friendlyKillScore;
	protected final int enemyKillScore;
	
	protected final int pointsToWin;
	
	public TeamDeathMatch(ConfigurationSection section) throws LoadingException {
		super(section);
		suicideScore = loader.loadInt("suicide_score", 0);
		friendlyKillScore = loader.loadInt("friendly_kill_score", 0);
		enemyKillScore = loader.loadInt("enemy_kill_score", 1);
		pointsToWin = loader.loadPositiveInt("points_to_win");
		ConfigurationSection teams = section.getConfigurationSection("teams");
		if (teams != null) for (String t : teams.getKeys(false)) {
			try {
				SimpleTeam team = new SimpleTeam(teams.getConfigurationSection(t));
				this.teams.put(t, team);
			} catch (LoadingException e) {
				throw (LoadingException) new LoadingException(String.format("Error in '%s' team.", t)).initCause(e);
			}
		}
		if (this.teams.isEmpty()) {
			throw new LoadingException("Teams must be defined.");
		}
	}
	
	private class SimpleTeam {
		
		private int score = 0;
		private String name;
		private List<String> spawnNames;
		private List<Location> spawns;
		private int spawnCounter = 0;
		private ChatColor color;
		
		public SimpleTeam(ConfigurationSection section) throws LoadingException {
			ValueLoader loader = new ValueLoader(section);
			spawnNames = section.getStringList("spawns");
			color = loader.loadEnum("color", ChatColor.class);
			name = ChatColor.translateAlternateColorCodes('&', loader.loadString("name"));
		}

		public int getScore() {
			return score;
		}

		public ChatColor getColor() {
			return color;
		}

		public void setScore(int score) {
			this.score = score;
		}

		public String getName() {
			return name;
		}
	}
	
	private class TeamLine implements SidebarLine {
		
		private SimpleTeam team;
		private int lastValue = 0;
		private String lastString;
		private String translated;
		
		public TeamLine(InGamePlayer player, SimpleTeam team) {
			this.team = team;
			translated = team.getName();
			if (translated.startsWith("$")) {
				translated = LangManager.getMessage(player, translated.substring(1));
			}
		}

		@Override
		public String getText() {
			int a = team.getScore();
			if (lastString == null || a != lastValue) {
				String format = team.getColor() + "%s" + ChatColor.WHITE + ": " + a;
				int left = 16 - format.length();
				String temp = translated.length() > left ? translated.substring(0, left) : translated;
				lastString = String.format(format, temp);
				lastValue = a;
			}
			return lastString;
		}
	}
	
	@Override
	public void fastTick() {}
	
	@Override
	public void slowTick() {}
	
	@Override
	public void endGame() {
		super.endGame();
		// get the winning team
		int maxPoints = players.values().stream()
				.max((teamA, teamB) -> teamA.getScore() - teamB.getScore())
				.map(team -> team.getScore())
				.orElse(0);
		List<SimpleTeam> winners = players.values().stream()
				.filter(team -> team.getScore() == maxPoints)
				.collect(Collectors.toList());
		// display message about winning
		for (Entry<UUID, SimpleTeam> entry : players.entrySet()) {
			InGamePlayer data = dataMap.get(entry.getKey());
			String word;
			if (winners.contains(entry.getValue())) {
				word = LangManager.getMessage(data, "win");
			} else {
				word = LangManager.getMessage(data, "lose");
			}
			String teamNames = String.join(", ", teams.values().stream()
					.filter(team -> team.getScore() == maxPoints)
					.map(team -> {
				return team.getName().startsWith("$") ?
						LangManager.getMessage(data, team.getName().substring(1)) :
						team.getName();
			}).collect(Collectors.toSet()));
			String win = LangManager.getMessage(data, "team_win", teamNames);
			Flier.getInstance().getFancyStuff().sendTitle(
					data.getPlayer(), win, entry.getValue().getColor() + word, 0, 0, 0);
			data.getPlayer().sendMessage(win);
		}
	}
	
	@Override
	public void removePlayer(Player player) {
		super.removePlayer(player);
		players.remove(player.getUniqueId());
		updateColors();
	}

	@Override
	public void handleKill(InGamePlayer killed, DamageCause cause) {
		super.handleKill(killed, cause);
		Attacker attacker = killed.getAttacker();
		InGamePlayer killer = attacker == null ? null : attacker.getShooter();
		if (rounds) {
			// kills in rounded games don't increase points
			// we should check if there are any opposite team players left
			// and increase points if the round is finished
			List<SimpleTeam> aliveTeams = teams.values().stream()
					// filter teams which still have alive players
					.filter(team -> players.entrySet().stream()
							// get uuids of team players and check if any is playing
							.filter(e -> e.getValue().equals(team))
							.anyMatch(e -> !e.getKey().equals(killed.getPlayer().getUniqueId()) &&
									dataMap.get(e.getKey()).isPlaying())
					)
					.collect(Collectors.toList());
			if (aliveTeams.size() == 1) {
				SimpleTeam winningTeam = aliveTeams.get(0);
				score(winningTeam, 1);
				players.entrySet().stream()
						.filter(e -> e.getValue().equals(winningTeam))
						.map(e -> dataMap.get(e.getKey()))
						.forEach(player -> moveToWaitingRoom(player));
			}
			// this is for test games where only one player is playing
			if (aliveTeams.size() <= 1) {
				roundFinished = true;
			}
		} else {
			// kills in continuous games increase points
			if (killer == null) {
				score(getTeam(killed), suicideScore);
				return;
			}
			Attitude a = getAttitude(killer, killed);
			if (a == Attitude.FRIENDLY) {
				score(getTeam(killed), friendlyKillScore);
			} else if (a == Attitude.HOSTILE) {
				score(getTeam(killer), enemyKillScore);
			}
		}
		moveToWaitingRoom(killed);
	}
	
	@Override
	public void handleRespawn(InGamePlayer player) {
		super.handleRespawn(player);
		SimpleTeam team = players.get(player.getPlayer().getUniqueId());
		if (team == null) {
			team = chooseTeam();
			setTeam(player, team);
			player.getLines().addAll(teams.values().stream()
					.map(t -> new TeamLine(player, t))
					.collect(Collectors.toList())
			);
		}
		player.getPlayer().teleport(team.spawns.get(team.spawnCounter++ % team.spawns.size()));
		FlierPlayerSpawnEvent event = new FlierPlayerSpawnEvent(player);
		Bukkit.getPluginManager().callEvent(event);
	}
	
	@Override
	public Attitude getAttitude(Target toThisOne, Target ofThisOne) {
		if (getTeam(toThisOne).equals(getTeam(ofThisOne))) {
			return Attitude.FRIENDLY;
		} else {
			return Attitude.HOSTILE;
		}
	}

	@Override
	public Map<String, ChatColor> getColors() {
		HashMap<String, ChatColor> map = new HashMap<>();
		for (Entry<UUID, InGamePlayer> e : dataMap.entrySet()) {
			SimpleTeam team = getTeam(dataMap.get(e.getKey()));
			if (team != null) {
				map.put(e.getValue().getPlayer().getName(), team.getColor());
			}
		}
		return map;
	}
	
	@Override
	public void setArena(Arena arena) throws LoadingException {
		super.setArena(arena);
		for (SimpleTeam team : teams.values()) {
			team.spawns = new ArrayList<>(team.spawnNames.size());
			for (String name : team.spawnNames) {
				team.spawns.add(arena.getLocation(name));
			}
			if (team.spawns.isEmpty()) {
				throw new LoadingException(String.format("Spawn list for team %s is empty.", team.name));
			}
		}
	}
	
	private  SimpleTeam getTeam(Target target) {
		if (target instanceof InGamePlayer) {
			return players.get(((InGamePlayer) target).getPlayer().getUniqueId());
		}
		return null; // TODO rewrite teams to include targets, not only players
	}
	
	private void setTeam(InGamePlayer data, SimpleTeam team) {
		players.put(data.getPlayer().getUniqueId(), team);
		data.setColor(team.getColor());
		String teamName = team.getName().startsWith("$") ?
				LangManager.getMessage(data, team.getName().substring(1)) :
				team.getName();
		Flier.getInstance().getFancyStuff().sendTitle(
				data.getPlayer(), team.getColor() + Utils.capitalize(teamName), null, 0, 0, 0);
		updateColors();
	}

	private void updateColors() {
		Map<String, ChatColor> colors = getColors();
		for (InGamePlayer g : dataMap.values()) {
			g.updateColors(colors);
		}
	}
	
	private SimpleTeam chooseTeam() {
		// looking for a rarest team
		SimpleTeam rarest = null;
		// prepare all teams with 0 points
		HashMap<SimpleTeam, Integer> map = new HashMap<>();
		for (SimpleTeam team : teams.values()) {
			map.put(team, 0);
		}
		// each point will mean a player in that team
		for (SimpleTeam team : players.values()) {
			if (team != null) { // team is null if the player does not have a team yet
				map.put(team, map.get(team) + 1);
			}
		}
		// maximum points for a team is the total amount of players in the game, we're looking for less
		Integer lowest = players.size();
		for (Entry<SimpleTeam, Integer> e : map.entrySet()) {
			if (e.getValue() <= lowest) { // (this is less)
				rarest = e.getKey();
				lowest = e.getValue();
			}
		}
		return rarest;
	}
	
	private void score(SimpleTeam team, int amount) {
		int newScore = team.getScore() + amount;
		team.setScore(newScore);
		if (newScore >= pointsToWin) {
			endGame();
		}
	}

}
