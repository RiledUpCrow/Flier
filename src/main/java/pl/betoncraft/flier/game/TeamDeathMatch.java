/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.api.content.Lobby;
import pl.betoncraft.flier.api.core.Damager;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.SidebarLine;
import pl.betoncraft.flier.api.core.Damager.DamageResult;
import pl.betoncraft.flier.core.defaults.DefaultGame;
import pl.betoncraft.flier.util.Utils;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * A simple team deathmatch game.
 *
 * @author Jakub Sapalski
 */
public class TeamDeathMatch extends DefaultGame {
	
	protected final Map<String, SimpleTeam> teams = new HashMap<>();
	protected final Map<String, TeamLine> lines = new HashMap<>();
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
				this.lines.put(t, new TeamLine(team));
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
		private Location spawn;
		private ChatColor color;
		
		public SimpleTeam(ConfigurationSection section) throws LoadingException {
			ValueLoader loader = new ValueLoader(section);
			spawn = loader.loadLocation("location");
			color = loader.loadEnum("color", ChatColor.class);
			name = ChatColor.translateAlternateColorCodes('&', loader.loadString("name"));
		}

		public int getScore() {
			return score;
		}

		public Location getSpawn() {
			return spawn;
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
		
		public TeamLine(SimpleTeam team) {
			this.team = team;
		}

		@Override
		public String getText() {
			int a = team.getScore();
			if (lastString == null || a != lastValue) {
				lastString = team.getColor() + team.getName() + ChatColor.WHITE + ": " + a;
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
	public void handleKill(InGamePlayer killer, InGamePlayer killed) {
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

	@Override
	public void handleHit(List<DamageResult> result, InGamePlayer attacker, InGamePlayer attacked, Damager damager) {}
	
	@Override
	public void afterRespawn(InGamePlayer player) {
		player.getLobby().respawnPlayer(player);
	}
	
	@Override
	public void addPlayer(InGamePlayer data) {
		super.addPlayer(data);
		data.getLines().addAll(lines.values());
	}
	
	@Override
	public void startPlayer(InGamePlayer data) {
		super.startPlayer(data);
		SimpleTeam team = getTeam(data);
		if (team == null) {
			team = chooseTeam();
			setTeam(data, team);
		}
		data.getPlayer().teleport(team.getSpawn());
	}
	
	@Override
	public void removePlayer(InGamePlayer data) {
		super.removePlayer(data);
		players.remove(data.getPlayer().getUniqueId());
		updateColors();
	}
	
	@Override
	public void stop() {
		super.stop();
		for (SimpleTeam team : teams.values()) {
			team.setScore(0);
		}
	}
	
	@Override
	public Attitude getAttitude(InGamePlayer toThisOne, InGamePlayer ofThisOne) {
		if (!toThisOne.isPlaying()) {
			return Attitude.NEUTRAL;
		}
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
	
	private  SimpleTeam getTeam(InGamePlayer data) {
		return players.get(data.getPlayer().getUniqueId());
	}
	
	private void setTeam(InGamePlayer data, SimpleTeam team) {
		players.put(data.getPlayer().getUniqueId(), team);
		data.setColor(team.getColor());
		String title = String.format("title %s title {\"text\":\"%s%s\"}",
				data.getPlayer().getName(), team.getColor(), Utils.capitalize(team.getName()));
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), title);
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
			// display message about winning
			for (Entry<UUID, SimpleTeam> entry : players.entrySet()) {
				String name = Bukkit.getPlayer(entry.getKey()).getName();
				String word;
				if (entry.getValue().equals(team)) {
					word = "You win!";
				} else {
					word = "You lose!";
				}
				String title = String.format("title %s title {\"text\":\"%sTeam %s wins!\"}",
						name, team.getColor(), Utils.capitalize(team.getName()));
				String subTitle = String.format("title %s subtitle {\"text\":\"%s%s\"}",
						name, entry.getValue().getColor(), word);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), title);
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), subTitle);
			}
			// start next game
			Lobby lobby = dataMap.values().iterator().next().getLobby();
			Game[] games = new Game[lobby.getGames().size()];
			games = lobby.getGames().values().toArray(games);
			Game game = null;
			for (int i = 0; i < games.length; i++) {
				if (games[i].equals(this)) {
					game = games[(i+1) % games.length];
					break;
				}
			}
			lobby.setGame(game);
		}
	}

}
