/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.game;

import org.bukkit.Location;
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
import pl.betoncraft.flier.api.Game;
import pl.betoncraft.flier.api.InGamePlayer;
import pl.betoncraft.flier.core.Utils;

/**
 * Basic rules of a game.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultGame implements Listener, Game {
	
	public class GameHeartBeat extends BukkitRunnable {
		
		private int i = 0;
		private DefaultGame game;
		
		public GameHeartBeat(DefaultGame game) {
			this.game = game;
			runTaskTimer(Flier.getInstance(), 1, 1);
		}

		@Override
		public void run() {
			game.fastTick();
			for (InGamePlayer data : getPlayers().values()) {
				data.fastTick();
			}
			if (i % 4 == 0) {
				game.slowTick();
				for (InGamePlayer data : getPlayers().values()) {
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
	public abstract void handleKill(InGamePlayer killer, InGamePlayer killed);
	
	/**
	 * The game should do game-specific stuff in a fast tick here.
	 */
	public abstract void fastTick();;

	/**
	 * The game should do game-specific stuff in a slow tick here.
	 */
	public abstract void slowTick();

	/**
	 * Returns a respawn location for the player.
	 * 
	 * @param respawned the player who needs respawning
	 */
	public abstract Location respawnLocation(InGamePlayer respawned);
	
	@EventHandler(priority=EventPriority.LOW)
	public void onClick(PlayerInteractEvent event) {
		InGamePlayer data = getPlayers().get(event.getPlayer().getUniqueId());
		if (data != null) {
			data.use();
		}
	}
	
	@EventHandler
	public void onHit(EntityDamageByEntityEvent event) {
		InGamePlayer player = getPlayers().get(event.getEntity().getUniqueId());
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
		InGamePlayer shooter = getPlayers().get(shooterPlayer.getUniqueId());
		// was hit by someone in Game
		if (shooter == null) {
			return;
		}
		Damager weapon = Damager.getDamager(projectile);
		if (weapon == null) {
			return;
		}
		player.damage(shooter, weapon);
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		InGamePlayer data = getPlayers().get(event.getPlayer().getUniqueId());
		if (data == null) {
			return;
		}
		data.setClazz(data.getClazz());
		event.getPlayer().setVelocity(new Vector());
		event.setRespawnLocation(respawnLocation(data));
	}
	
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		InGamePlayer killed = getPlayers().get(event.getEntity().getUniqueId());
		if (killed != null) {
			killed.setPlaying(false);
			event.getDrops().clear();
			InGamePlayer lastHit = killed.getAttacker();
			InGamePlayer killer = lastHit == null ? null : getPlayers().get(lastHit.getPlayer().getUniqueId());
			killed.setAttacker(null);
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
