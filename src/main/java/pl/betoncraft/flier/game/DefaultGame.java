/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.game;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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

import pl.betoncraft.flier.Flier;
import pl.betoncraft.flier.api.Damager;
import pl.betoncraft.flier.api.Damager.DamageResult;
import pl.betoncraft.flier.core.PlayerData;
import pl.betoncraft.flier.core.Utils;
import pl.betoncraft.flier.api.Game;

/**
 * Basic rules of a game.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultGame implements Listener, Game {
	
	public class GameHeartBeat extends BukkitRunnable {
		
		private int i = 0;
		
		public GameHeartBeat() {
			runTaskTimer(Flier.getInstance(), 1, 1);
		}

		@Override
		public void run() {
			for (PlayerData data : getPlayers().values()) {
				data.fastTick();
				if (i % 4 == 0) {
					data.slowTick();
				}
			}
			i++;
			if (i > 1000) {
				i = 0;
			}
		}
	}
	
	/**
	 * Handles one player killing another one.
	 * In case of suicide killer is null.
	 * 
	 * @param killer the player who killed another
	 * @param killed the player who was killed
	 */
	public abstract void handleKill(PlayerData killer, PlayerData killed);
	
	/**
	 * Returns a respawn location for the player.
	 * 
	 * @param respawned the player who needs respawning
	 */
	public abstract Location respawnLocation(PlayerData respawned);
	
	@EventHandler(priority=EventPriority.LOW)
	public void onClick(PlayerInteractEvent event) {
		PlayerData data = getPlayers().get(event.getPlayer().getUniqueId());
		if (data != null && data.isPlaying()) {
			data.use();
		}
	}
	
	@EventHandler
	public void onHit(EntityDamageByEntityEvent event) {
		PlayerData player = getPlayers().get(event.getEntity().getUniqueId());
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
		projectile.remove();
		// shooter was some player
		if (!(projectile.getShooter() instanceof Player)) {
			return;
		}
		Player shooterPlayer = (Player) projectile.getShooter();
		PlayerData shooter = getPlayers().get(shooterPlayer.getUniqueId());
		// was hit by someone in Game
		if (shooter == null) {
			return;
		}
		Damager weapon = Damager.getDamager(projectile);
		// was hit by himself
		if (shooterPlayer.equals(event.getEntity())) {
			// ignore if you can's commit suicide with this weapon
			if (!weapon.suicidal()) {
				return;
			}
		}
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
	public void onRespawn(PlayerRespawnEvent event) {
		PlayerData data = getPlayers().get(event.getPlayer().getUniqueId());
		if (data == null) {
			return;
		}
		data.setClazz(data.getClazz());
		event.getPlayer().setVelocity(new Vector());
		event.setRespawnLocation(respawnLocation(data));
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		PlayerData killed = getPlayers().get(event.getEntity().getUniqueId());
		if (killed != null) {
			killed.setPlaying(false);
			event.getDrops().clear();
			PlayerData lastHit = killed.getLastHit();
			PlayerData killer = lastHit == null ? null : getPlayers().get(lastHit.getPlayer().getUniqueId());
			killed.setLastHit(null);
			if (killer != null) {
				switch (event.getEntity().getLastDamageCause().getCause()) {
				case FALL:
					event.setDeathMessage(Utils.formatPlayer(killed) + " was shot down by " + Utils.formatPlayer(killer) + "!");
					break;
				default:
					event.setDeathMessage(Utils.formatPlayer(killed) + " was killed by " + Utils.formatPlayer(killer) + "!");
					break;
				}
				handleKill(killer, killed);
			} else {
				event.setDeathMessage(Utils.formatPlayer(killed) + " commited suicide...");
				handleKill(null, killed);
			}
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if (getPlayers().containsKey(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPickup(PlayerPickupItemEvent event) {
		if (getPlayers().containsKey(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onSwap(PlayerSwapHandItemsEvent event) {
		if (getPlayers().containsKey(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onInvInteract(InventoryInteractEvent event) {
		if (getPlayers().containsKey(event.getWhoClicked().getUniqueId())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		if (getPlayers().containsKey(event.getPlayer().getUniqueId())) {
			removePlayer(event.getPlayer());
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if (getPlayers().containsKey(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if (getPlayers().containsKey(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
		}
	}

}
