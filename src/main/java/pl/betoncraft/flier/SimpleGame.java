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
import org.bukkit.event.EventPriority;
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

/**
 * A simple team deathmatch game with fixed classes.
 *
 * @author Jakub Sapalski
 */
public class SimpleGame implements Game, Listener {
	
	private Map<UUID, PlayerData> dataMap = new HashMap<>();
	private Map<String, Team> teams = new HashMap<>();
	private SimpleLobby lobby;
	
	public class GameHeartBeat extends BukkitRunnable {
		
		private int i = 0;
		private SimpleGame game;
		
		public GameHeartBeat(SimpleGame game) {
			this.game = game;
			runTaskTimer(Flier.getInstance(), 1, 1);
		}

		@Override
		public void run() {
			game.fastTick();
			if (i % 4 == 0) {
				game.slowTick();
			}
			i++;
			if (i > 1000) {
				i = 0;
			}
		}

	}
	
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
		new GameHeartBeat(this);
		Bukkit.getPluginManager().registerEvents(this, Flier.getInstance());
	}
	
	@Override
	public void addPlayer(Player player) {
		if (dataMap.containsKey(player.getUniqueId())) {
			return;
		}
		PlayerData data = new PlayerData(player, this);
		dataMap.put(player.getUniqueId(), data);
		player.teleport(lobby.getSpawn());
	}
	
	public void setTeam(Player player, Team team) {
		PlayerData data = dataMap.get(player.getUniqueId());
		if (data != null) {
			data.setTeam(team);
			data.clearStats();
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title " + player.getName()
					+ " title {\"text\":\"" + team.getColor() + Utils.capitalize(team.getName()) + "\"}");
			for (int i = 0; i < teams.size(); i++) {
				data.addStatistic("");
			}
			for (Entry<String, Team> e : teams.entrySet()) {
				data.updateStatistic(e.getValue().getIndex(), e.getValue().getName() + ChatColor.WHITE + ": " +
						e.getValue().getScore());
			}
			updateColors();
		}
	}
	
	public void setClass(Player player, PlayerClass clazz) {
		PlayerData data = dataMap.get(player.getUniqueId());
		if (data != null) {
			data.setClazz(clazz);
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title " + player.getName()
					+ " title {\"text\":\"" + ChatColor.AQUA + clazz.getName() + "\"}");
		}
	}
	
	public void startPlayer(Player player) {
		PlayerData data = dataMap.get(player.getUniqueId());
		if (data != null) {
			if (data.getTeam() == null || data.getClazz() == null) {
				player.sendMessage(ChatColor.RED + "Choose your class and team!");
			} else {
				new BukkitRunnable() {
					@Override
					public void run() {
						data.setPlaying(true);
					}
				}.runTaskLater(Flier.getInstance(), 20);
				player.teleport(data.getTeam().getSpawn());
			}
		}
	}
	
	@Override
	public Map<UUID, PlayerData> getPlayers() {
		return dataMap;
	}
	
	@Override
	public void removePlayer(Player player) {
		PlayerData data = dataMap.remove(player.getUniqueId());
		if (data != null) {
			player.setVelocity(new Vector());
			data.clear();
			player.teleport(data.getReturnLocation());
		}
		if (dataMap.isEmpty()) {
			for (Team t : teams.values()) {
				t.setScore(0);
			}
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
		Set<PlayerData> copy = new HashSet<>(dataMap.values());
		for (PlayerData data : copy) {
			removePlayer(data.getPlayer());
		}
	}
	
	public void fastTick() {
		for (PlayerData data : dataMap.values()) {
			if (!data.isPlaying()) {
				continue;
			}
			Player player = data.getPlayer();
			if (data.isFlying()) {
				player.setVelocity(data.getWings().applyFlightModifications(player.getVelocity()));
			}
			if (data.isAccelerating()) {
				data.speedUp();
			} else {
				data.regenerateFuel();
			}
			data.regenerateWings();
			data.cooldown();
		}
	}
	
	public void slowTick() {
		for (PlayerData data : dataMap.values()) {
			if (!data.isPlaying()) {
				continue;
			}
			data.stopGlowing();
			data.updateStats();
		}
	}

	private void updateColors() {
		Map<String, ChatColor> colors = getColors();
		for (PlayerData g : dataMap.values()) {
			g.setTeamColors(colors);
		}
	}
	
	private Map<String, ChatColor> getColors() {
		HashMap<String, ChatColor> map = new HashMap<>();
		for (Entry<UUID, PlayerData> e : dataMap.entrySet()) {
			map.put(e.getValue().getPlayer().getName(), dataMap.get(e.getKey()).getTeam().getColor());
		}
		return map;
	}
	
	private void score(Team team, int amount) {
		team.setScore(team.getScore() + amount);
		for (PlayerData data : dataMap.values()) {
			data.updateStatistic(team.getIndex(), team.getName() + ChatColor.WHITE + ": " + team.getScore());
		}
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onClick(PlayerInteractEvent event) {
		PlayerData data = dataMap.get(event.getPlayer().getUniqueId());
		if (data != null && data.isPlaying()) {
			data.use();
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if (dataMap.containsKey(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if (dataMap.containsKey(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onHit(EntityDamageByEntityEvent event) {
		PlayerData player = dataMap.get(event.getEntity().getUniqueId());
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
		PlayerData shooter = dataMap.get(shooterPlayer.getUniqueId());
		// was hit by someone in Game
		if (shooter == null) {
			return;
		}
		Damager weapon = Damager.getDamager(projectile);
		// was hit by a Weapon
		if (weapon == null) {
			return;
		}
		DamageResult result = player.damage(weapon);
		boolean notify = true;
		boolean sound = true;
		switch (result) {
		case INSTANT_KILL:
			player.setLastHit(shooter);
			notify = false;
			player.getPlayer().damage(player.getPlayer().getHealth() + 1);
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
			double damage = weapon.getPhysical();
			if (player.getPlayer().getHealth() <= damage) {
				notify = false;
			}
			player.getPlayer().damage(damage);
			break;
		case NOTHING:
			notify = false;
			sound = false;
			break;
		}
		if (notify) {
			shooter.getPlayer().sendMessage(ChatColor.YELLOW + "You managed to hit " + Utils.formatPlayer(player) + "!");
		}
		if (sound) {
			shooter.getPlayer().playSound(shooter.getPlayer().getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 1);
			player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_HURT, 1, 1);
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
		PlayerData killed = dataMap.get(event.getEntity().getUniqueId());
		if (killed != null) {
			killed.setPlaying(false);
			Team killedTeam = killed.getTeam();
			event.getDrops().clear();
			PlayerData lastHit = killed.getLastHit();
			PlayerData killer = lastHit == null ? null : dataMap.get(lastHit.getPlayer().getUniqueId());
			killed.setLastHit(null);
			if (killer != null) {
				Team killerTeam = killer.getTeam();
				switch (event.getEntity().getLastDamageCause().getCause()) {
				case FALL:
					event.setDeathMessage(Utils.formatPlayer(killed) + " was shot down by " + Utils.formatPlayer(killer) + "!");
					break;
				default:
					event.setDeathMessage(Utils.formatPlayer(killed) + " was killed by " + Utils.formatPlayer(killer) + "!");
					break;
				}
				score(killerTeam, 1);
			} else {
				event.setDeathMessage(Utils.formatPlayer(killed) + " commited suicide...");
				score(killedTeam, -1);
			}
		}
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
		data.setClazz(data.getClazz());
		event.getPlayer().setVelocity(new Vector());
		event.setRespawnLocation(lobby.getSpawn());
	}

}
