/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import pl.betoncraft.flier.api.Damager;
import pl.betoncraft.flier.api.Damager.DamageResult;
import pl.betoncraft.flier.api.Game;
import pl.betoncraft.flier.api.PlayerClass;
import pl.betoncraft.flier.api.Team;
import pl.betoncraft.flier.api.UsableItem;

/**
 * A simple team deathmatch game with fixed classes.
 *
 * @author Jakub Sapalski
 */
public class SimpleGame implements Game, Listener {
	
	private Map<UUID, InGamePlayer> dataMap = new HashMap<>();
	private Map<String, Team> teams = new HashMap<>();
	private SimpleLobby lobby;
	
	public SimpleGame(ConfigurationSection section) {
		ConfigurationSection teams = section.getConfigurationSection("teams");
		if (teams != null) {
			int i = 0;
			for (String team : teams.getKeys(false)) {
				this.teams.put(team, new DefaultTeam(teams.getConfigurationSection(team), i++));
			}
		}
		ConfigurationSection lobby = section.getConfigurationSection("lobby");
		if (lobby != null) {
			this.lobby = new SimpleLobby(lobby, this);
		}
		new BukkitRunnable() {
			int i = 0;
			@Override
			public void run() {
				for (InGamePlayer inGame : dataMap.values()) {
					if (!inGame.isPlaying) {
						continue;
					}
					PlayerData data = inGame.getData();
					if (data.isFlying()) {
						data.applyFlightModifications();
					}
					if (data.isAccelerating()) {
						data.speedUp();
					} else {
						data.regenerateFuel();
					}
					data.regenerateWings();
					data.cooldown();
					if (i % 4 == 0) {
						data.stopGlowing();
						data.updateStats();
					}
				}
				i++;
				if (i > 1000) {
					i = 0;
				}
			}
		}.runTaskTimer(Flier.getInstance(), 1, 1);
		Bukkit.getPluginManager().registerEvents(this, Flier.getInstance());
	}
	
	@Override
	public void addPlayer(Player player) {
		if (dataMap.containsKey(player.getUniqueId())) {
			return;
		}
		PlayerData data = new PlayerData(player);
		InGamePlayer inGame = new InGamePlayer(data);
		dataMap.put(player.getUniqueId(), inGame);
		player.teleport(lobby.getSpawn());
	}
	
	public void setTeam(Player player, Team team) {
		InGamePlayer inGame = dataMap.get(player.getUniqueId());
		if (inGame != null) {
			inGame.setTeam(team);
			inGame.getData().clearStats();
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title " + player.getName()
					+ " title {\"text\":\"" + team.getColor() + Utils.capitalize(team.getName()) + "\"}");
			for (int i = 0; i < teams.size(); i++) {
				inGame.getData().addStatistic("");
			}
			for (Entry<String, Team> e : teams.entrySet()) {
				inGame.getData().updateStatistic(e.getValue().getIndex(), e.getValue().getName() + ChatColor.WHITE + ": " +
						e.getValue().getScore());
			}
			updateColors();
		}
	}
	
	public void setClass(Player player, PlayerClass clazz) {
		InGamePlayer inGame = dataMap.get(player.getUniqueId());
		if (inGame != null) {
			inGame.setClass(clazz);
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title " + player.getName()
					+ " title {\"text\":\"" + ChatColor.AQUA + clazz.getName() + "\"}");
			giveClassItems(clazz, inGame.getData());
		}
	}
	
	public void startPlayer(Player player) {
		InGamePlayer inGame = dataMap.get(player.getUniqueId());
		if (inGame != null) {
			if (inGame.getTeam() == null || inGame.getClazz() == null) {
				player.sendMessage(ChatColor.RED + "Choose your class and team!");
			} else {
				player.teleport(inGame.getTeam().getSpawn());
				inGame.setPlaying(true);
			}
		}
	}
	
	public Map<UUID, Player> getPlayers() {
		HashMap<UUID, Player> players = new HashMap<>();
		for (Entry<UUID, InGamePlayer> e : dataMap.entrySet()) {
			players.put(e.getKey(), e.getValue().getData().getPlayer());
		}
		return players;
	}
	
	@Override
	public void removePlayer(Player player) {
		InGamePlayer data = dataMap.remove(player.getUniqueId());
		if (data != null) {
			player.setVelocity(new Vector());
			data.getData().clear();
			player.teleport(data.getData().getReturnLocation());
		}
	}

	@Override
	public Team getTeam(String name) {
		return teams.get(name);
	}

	@Override
	public Map<String, Team> getTeams() {
		return teams;
	}

	@Override
	public void stop() {
		HandlerList.unregisterAll(lobby);
		HandlerList.unregisterAll(this);
		Set<InGamePlayer> copy = new HashSet<>(dataMap.values());
		for (InGamePlayer data : copy) {
			removePlayer(data.getData().getPlayer());
		}
	}

	private void updateColors() {
		Map<String, ChatColor> colors = getColors();
		for (InGamePlayer g : dataMap.values()) {
			g.getData().setTeamColors(colors);
		}
	}
	
	private Map<String, ChatColor> getColors() {
		HashMap<String, ChatColor> map = new HashMap<>();
		for (Entry<UUID, InGamePlayer> e : dataMap.entrySet()) {
			map.put(e.getValue().getData().getPlayer().getName(), dataMap.get(e.getKey()).getTeam().getColor());
		}
		return map;
	}

	private void giveClassItems(PlayerClass clazz, PlayerData data) {
		data.getPlayer().getInventory().clear();
		data.setEngine(clazz.getEngine());
		for (Entry<UsableItem, Integer> e : clazz.getItems().entrySet()) {
			data.addItem(e.getKey(), e.getValue());
		}
		data.setWings(clazz.getWings());
	}
	
	private void score(Team team, int amount) {
		team.setScore(team.getScore() + amount);
		for (InGamePlayer data : dataMap.values()) {
			data.getData().updateStatistic(team.getIndex(), team.getName() + ChatColor.WHITE + ": " + team.getScore());
		}
	}
	
	@EventHandler
	public void onClic(PlayerInteractEvent event) {
		InGamePlayer player = dataMap.get(event.getPlayer().getUniqueId());
		if (player != null) {
			player.getData().use();
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		InGamePlayer player = dataMap.get(event.getPlayer().getUniqueId());
		if (player != null) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		InGamePlayer player = dataMap.get(event.getPlayer().getUniqueId());
		if (player != null) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onHit(EntityDamageByEntityEvent event) {
		InGamePlayer player = dataMap.get(event.getEntity().getUniqueId());
		// hit player in Game
		if (player == null) {
			return;
		}
		event.setCancelled(true);
		// hit by a projectile
		if (!(event.getDamager() instanceof Projectile)) {
			return;
		}
		Projectile projectile = (Projectile) event.getDamager();
		// shooter was some player
		if (!(projectile.getShooter() instanceof Player)) {
			return;
		}
		Player shooterPlayer = (Player) projectile.getShooter();
		// was not hit by himself
		if (shooterPlayer.equals(event.getEntity())) {
			return;
		}
		InGamePlayer shooter = dataMap.get(shooterPlayer.getUniqueId());
		// was hit by someone in Game
		if (shooter == null) {
			return;
		}
		Damager weapon = Damager.getDamager(projectile);
		// was hit by a Weapon
		if (weapon == null) {
			return;
		}
		PlayerData playerData = player.getData();
		PlayerData shooterData = shooter.getData();
		DamageResult result = playerData.damage(weapon);
		boolean sound = true;
		switch (result) {
		case INSTANT_KILL:
			playerData.setLastHit(shooterData);
			playerData.getPlayer().damage(playerData.getPlayer().getHealth() + 1, shooterData.getPlayer());
			break;
		case WINGS_OFF:
			playerData.setLastHit(shooterData);
			playerData.takeWingsOff();
			// no break, we want to damage wings too
		case WINGS_DAMAGE:
			playerData.setLastHit(shooterData);
			playerData.removeHealth(weapon.getDamage());
			break;
		case REGULAR_DAMAGE:
			playerData.setLastHit(shooterData);
			playerData.getPlayer().damage(weapon.getPhysical(), shooterData.getPlayer());
			break;
		case NOTHING:
			sound = false;
			break;
		}
		if (sound) {
			shooterData.getPlayer().playSound(shooterData.getPlayer().getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 1);
			playerData.getPlayer().playSound(playerData.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_HURT, 1, 1);
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if (dataMap.containsKey(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPickup(PlayerPickupItemEvent event) {
		if (dataMap.containsKey(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onSwap(PlayerSwapHandItemsEvent event) {
		if (dataMap.containsKey(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInvInteract(InventoryInteractEvent event) {
		if (dataMap.containsKey(event.getWhoClicked().getUniqueId())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		InGamePlayer killed = dataMap.get(event.getEntity().getUniqueId());
		if (killed != null) {
			Team killedTeam = killed.getTeam();
			PlayerClass killedClass = killed.getClazz();
			event.getDrops().clear();
			PlayerData lastHit = killed.getData().getLastHit();
			InGamePlayer killer = lastHit == null ? null : dataMap.get(lastHit.getPlayer().getUniqueId());
			killed.getData().setLastHit(null);
			if (killer != null) {
				Team killerTeam = killer.getTeam();
				PlayerClass killerClass = killer.getClazz();
				switch (event.getEntity().getLastDamageCause().getCause()) {
				case FALL:
					event.setDeathMessage(formatPlayer(killedTeam, killedClass, killed.getData().getPlayer().getName())
							+ " was shot down by "
							+ formatPlayer(killerTeam, killerClass, killer.getData().getPlayer().getName()));
					break;
				default:
					event.setDeathMessage(formatPlayer(killedTeam, killedClass, killed.getData().getPlayer().getName())
							+ " was killed by "
							+ formatPlayer(killerTeam, killerClass, killer.getData().getPlayer().getName()));
					break;
				}
				score(killerTeam, 1);
			} else {
				event.setDeathMessage(formatPlayer(killedTeam, killedClass, killed.getData().getPlayer().getName())
						+ " commited suicide");
				score(killedTeam, -1);
			}
		}
	}

	private String formatPlayer(Team team, PlayerClass clazz, String name) {
		return team.getColor() + name + ChatColor.WHITE + " (" + ChatColor.AQUA + clazz.getName() + ChatColor.WHITE + ")";
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		if (dataMap.containsKey(event.getPlayer().getUniqueId())) {
			removePlayer(event.getPlayer());
		}
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		InGamePlayer data = dataMap.get(event.getPlayer().getUniqueId());
		if (data == null) {
			return;
		}
		PlayerClass c = data.getClazz();
		giveClassItems(c, data.getData());
		event.getPlayer().setVelocity(new Vector());
		event.setRespawnLocation(lobby.getSpawn());
	}
	
	public class InGamePlayer {
		
		private PlayerData data;
		private PlayerClass clazz;
		private Team team;
		private boolean isPlaying;
		
		public InGamePlayer(PlayerData data) {
			this.data = data;
		}

		public PlayerData getData() {
			return data;
		}

		public PlayerClass getClazz() {
			return clazz;
		}

		public void setClass(PlayerClass clazz) {
			this.clazz = clazz;
		}

		public Team getTeam() {
			return team;
		}

		public void setTeam(Team team) {
			this.team = team;
		}

		public boolean isPlaying() {
			return isPlaying;
		}

		public void setPlaying(boolean isPlaying) {
			this.isPlaying = isPlaying;
		}
	}


}
