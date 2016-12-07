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
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
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
	
	private Map<UUID, PlayerData> dataMap = new HashMap<>();
	
	private Map<String, Team> teams = new HashMap<>();
	
	private Map<UUID, Team> players = new HashMap<>();
	private Map<UUID, PlayerClass> classes = new HashMap<>();
	
	public SimpleGame(ConfigurationSection section) {
		ConfigurationSection teams = section.getConfigurationSection("teams");
		if (teams != null) {
			int i = 0;
			for (String team : teams.getKeys(false)) {
				this.teams.put(team, new DefaultTeam(teams.getConfigurationSection(team), i++));
			}
		}
		new BukkitRunnable() {
			int i = 0;
			@Override
			public void run() {
				for (PlayerData data : dataMap.values()) {
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
	public void addPlayer(Player player, Team team, PlayerClass clazz) {
		PlayerData data = new PlayerData(player);
		dataMap.put(player.getUniqueId(), data);
		players.put(player.getUniqueId(), team);
		classes.put(player.getUniqueId(), clazz);
		giveClassItems(clazz, data);
		player.teleport(getSpawnLocation(player));
		for (int i = 0; i < teams.size(); i++) {
			data.addStatistic("");
		}
		for (Entry<String, Team> e : SimpleGame.this.teams.entrySet()) {
			data.updateStatistic(e.getValue().getIndex(), e.getValue().getName() + ChatColor.WHITE + ": " +
					e.getValue().getScore());
		}
		updateColors();
	}
	
	@Override
	public void removePlayer(Player player) {
		PlayerData data = dataMap.remove(player.getUniqueId());
		if (data != null) {
			data.clear();
		}
		player.teleport(player.getLocation().getWorld().getSpawnLocation());
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
		Set<PlayerData> copy = new HashSet<>(dataMap.values());
		for (PlayerData data : copy) {
			removePlayer(data.getPlayer());
		}
	}

	private void updateColors() {
		Map<String, ChatColor> colors = getColors();
		for (PlayerData d : dataMap.values()) {
			d.setTeamColors(colors);
		}
	}
	
	private Map<String, ChatColor> getColors() {
		HashMap<String, ChatColor> map = new HashMap<>();
		for (Entry<UUID, PlayerData> e : dataMap.entrySet()) {
			map.put(e.getValue().getPlayer().getName(), players.get(e.getKey()).getColor());
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
	
	private Location getSpawnLocation(Player player) {
		return players.get(player.getUniqueId()).getSpawn();
	}
	
	private void score(Team team, int amount) {
		team.setScore(team.getScore() + amount);
		for (PlayerData data : dataMap.values()) {
			data.updateStatistic(team.getIndex(), team.getName() + ChatColor.WHITE + ": " + team.getScore());
		}
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		PlayerData data = dataMap.get(event.getPlayer().getUniqueId());
		if (data != null) {
			switch (event.getAction()) {
			case LEFT_CLICK_AIR:
			case LEFT_CLICK_BLOCK:
				event.setCancelled(true);
				data.use();
				break;
			default:
				break;
			}
		}
	}
	
	@EventHandler
	public void onHit(EntityDamageByEntityEvent event) {
		PlayerData player = dataMap.get(event.getEntity().getUniqueId());
		// hit player in Game
		if (player == null) {
			return;
		}
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
			event.setCancelled(true);
			return;
		}
		PlayerData shooter = dataMap.get(shooterPlayer.getUniqueId());
		// was hit by someone in Game
		if (shooter == null) {
			event.setCancelled(true);
			return;
		}
		Damager weapon = Damager.getDamager(projectile);
		// was hit by a Weapon
		if (weapon == null) {
			event.setCancelled(true);
			return;
		}
		DamageResult result = player.damage(weapon);
		boolean sound = true;
		switch (result) {
		case INSTANT_KILL:
			player.setLastHit(shooter);
			player.getPlayer().damage(player.getPlayer().getHealth() + 1, shooter.getPlayer());
			break;
		case WINGS_OFF:
			player.setLastHit(shooter);
			player.takeWingsOff();
			// no break, we want to damage wings too
		case WINGS_DAMAGE:
			player.setLastHit(shooter);
			player.removeHealth(weapon.getDamage());
			break;
		case REGULAR_DAMAGE:
			player.setLastHit(shooter);
			player.getPlayer().damage(weapon.getPhysical(), shooter.getPlayer());
			break;
		case NOTHING:
			sound = false;
			break;
		}
		if (sound) {
			shooter.getPlayer().playSound(shooter.getPlayer().getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 1);
			player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_HURT, 1, 1);
		}
		event.setCancelled(true);
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
	public void onInvOpen(InventoryInteractEvent event) {
		if (dataMap.containsKey(event.getWhoClicked().getUniqueId())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		PlayerData killed = dataMap.get(event.getEntity().getUniqueId());
		if (killed != null) {
			Team killedTeam = players.get(killed.getPlayer().getUniqueId());
			PlayerClass killedClass = classes.get(killed.getPlayer().getUniqueId());
			event.getDrops().clear();
			PlayerData killer = killed.getLastHit();
			killed.setLastHit(null);
			if (killer != null) {
				Team killerTeam = players.get(killer.getPlayer().getUniqueId());
				PlayerClass killerClass = classes.get(killer.getPlayer().getUniqueId());
				switch (event.getEntity().getLastDamageCause().getCause()) {
				case FALL:
					event.setDeathMessage(formatPlayer(killedTeam, killedClass, killed.getPlayer().getName())
							+ " was shot down by "
							+ formatPlayer(killerTeam, killerClass, killer.getPlayer().getName()));
					break;
				default:
					event.setDeathMessage(formatPlayer(killedTeam, killedClass, killed.getPlayer().getName())
							+ " was killed by "
							+ formatPlayer(killerTeam, killerClass, killer.getPlayer().getName()));
					break;
				}
				score(killerTeam, 1);
			} else {
				event.setDeathMessage(formatPlayer(killedTeam, killedClass, killed.getPlayer().getName())
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
		PlayerData data = dataMap.get(event.getPlayer().getUniqueId());
		if (data == null) {
			return;
		}
		PlayerClass c = classes.get(event.getPlayer().getUniqueId());
		giveClassItems(c, data);
		event.setRespawnLocation(getSpawnLocation(event.getPlayer()));
	}

}
