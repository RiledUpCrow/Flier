/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.game;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.flier.Flier;
import pl.betoncraft.flier.api.Damager;
import pl.betoncraft.flier.api.Damager.DamageResult;
import pl.betoncraft.flier.api.InGamePlayer;
import pl.betoncraft.flier.api.SidebarLine;
import pl.betoncraft.flier.core.PlayerData;
import pl.betoncraft.flier.core.Utils;
import pl.betoncraft.flier.exception.LoadingException;
import pl.betoncraft.flier.sidebar.Altitude;
import pl.betoncraft.flier.sidebar.Fuel;
import pl.betoncraft.flier.sidebar.Health;
import pl.betoncraft.flier.sidebar.Money;
import pl.betoncraft.flier.sidebar.Speed;

/**
 * A simple team deathmatch game.
 *
 * @author Jakub Sapalski
 */
public class TeamDeathMatch extends DefaultGame {
	
	private Map<String, SimpleTeam> teams = new HashMap<>();
	private Map<String, TeamLine> lines = new HashMap<>();
	private Map<UUID, SimpleTeam> players = new HashMap<>();
	
	private int suicideScore = -1;
	private int friendlyKillScore = -1;
	private int enemyKillScore = 1;
	
	public TeamDeathMatch(ConfigurationSection section) throws LoadingException {
		super(section);
		ConfigurationSection teams = section.getConfigurationSection("teams");
		if (teams != null) {
			for (String t : teams.getKeys(false)) {
				SimpleTeam team = new SimpleTeam(teams.getConfigurationSection(t));
				this.teams.put(t, team);
				this.lines.put(t, new TeamLine(team));
			}
		}
		new GameHeartBeat(this);
		Bukkit.getPluginManager().registerEvents(this, Flier.getInstance());
	}
	
	private class SimpleTeam {
		
		private int score = 0;
		private String name;
		private Location spawn;
		private ChatColor color;
		
		public SimpleTeam(ConfigurationSection section) {
			spawn = Utils.parseLocation(section.getString("location"));
			color = ChatColor.valueOf(section.getString("color", "white").toUpperCase().replace(' ', '_'));
			name = ChatColor.translateAlternateColorCodes('&', section.getString("name"));
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
	public void addPlayer(Player player) {
		if (dataMap.containsKey(player.getUniqueId())) {
			return;
		}
		InGamePlayer data = new PlayerData(player, this, lobby.getDefaultClass());
		dataMap.put(player.getUniqueId(), data);
		data.getLines().add(new Fuel(data));
		data.getLines().add(new Health(data));
		data.getLines().add(new Speed(data));
		data.getLines().add(new Altitude(data));
		if (useMoney) {
			data.getLines().add(new Money(data));
		}
		data.getLines().addAll(lines.values());
		player.teleport(lobby.getSpawn());
	}
	
	@Override
	public void removePlayer(Player player) {
		InGamePlayer data = dataMap.remove(player.getUniqueId());
		if (data != null) {
			players.remove(player.getUniqueId());
			data.clear();
		}
		if (dataMap.isEmpty()) {
			for (SimpleTeam t : teams.values()) {
				t.setScore(0);
			}
		}
	}
	
	@Override
	public Map<UUID, InGamePlayer> getPlayers() {
		return dataMap;
	}
	
	@Override
	public void startPlayer(Player player) {
		InGamePlayer data = dataMap.get(player.getUniqueId());
		if (data != null) {
			if (data.getClazz() == null) {
				player.sendMessage(ChatColor.RED + "Choose your class!");
			} else {
				new BukkitRunnable() {
					@Override
					public void run() {
						data.setPlaying(true);
					}
				}.runTaskLater(Flier.getInstance(), 20);
				SimpleTeam team = getTeam(data);
				if (team == null) {
					setTeam(player, chooseTeam());
				}
				player.teleport(getTeam(data).getSpawn());
			}
		}
	}

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
	public void handleHit(DamageResult result, InGamePlayer attacker, InGamePlayer attacked, Damager damager) {}

	@Override
	public Location respawnLocation(InGamePlayer respawned) {
		return lobby.getSpawn();
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
	
	private  SimpleTeam getTeam(InGamePlayer data) {
		return players.get(data.getPlayer().getUniqueId());
	}
	
	private void setTeam(Player player, SimpleTeam team) {
		InGamePlayer data = dataMap.get(player.getUniqueId());
		if (data != null) {
			players.put(player.getUniqueId(), team);
			data.setColor(team.getColor());
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title " + player.getName()
					+ " title {\"text\":\"" + team.getColor() + Utils.capitalize(team.getName()) + "\"}");
			Map<String, ChatColor> colors = getColors();
			for (InGamePlayer g : dataMap.values()) {
				g.updateColors(colors);
			}
		}
	}
	
	private SimpleTeam chooseTeam() {
		SimpleTeam rarest = null;
		HashMap<SimpleTeam, Integer> map = new HashMap<>();
		for (SimpleTeam team : teams.values()) {
			map.put(team, 0);
		}
		for (SimpleTeam team : players.values()) {
			map.put(team, map.get(team) + 1);
		}
		Integer lowest = teams.size();
		for (Entry<SimpleTeam, Integer> e : map.entrySet()) {
			if (e.getValue() < lowest) {
				rarest = e.getKey();
				lowest = e.getValue();
			}
		}
		return rarest;
	}
	
	private void score(SimpleTeam team, int amount) {
		team.setScore(team.getScore() + amount);
	}

}
