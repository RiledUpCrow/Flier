/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core.defaults;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import pl.betoncraft.flier.Flier;
import pl.betoncraft.flier.api.Bonus;
import pl.betoncraft.flier.api.Damager;
import pl.betoncraft.flier.api.Damager.Attacker;
import pl.betoncraft.flier.api.Damager.DamageResult;
import pl.betoncraft.flier.api.Game;
import pl.betoncraft.flier.api.InGamePlayer;
import pl.betoncraft.flier.exception.LoadingException;
import pl.betoncraft.flier.sidebar.Altitude;
import pl.betoncraft.flier.sidebar.Ammo;
import pl.betoncraft.flier.sidebar.Fuel;
import pl.betoncraft.flier.sidebar.Health;
import pl.betoncraft.flier.sidebar.Money;
import pl.betoncraft.flier.sidebar.Reload;
import pl.betoncraft.flier.sidebar.Speed;
import pl.betoncraft.flier.util.Utils;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * Basic rules of a game.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultGame implements Listener, Game {

	protected GameHeartBeat heartBeat;
	protected Map<UUID, InGamePlayer> dataMap = new HashMap<>();
	protected List<Bonus> bonuses = new ArrayList<>();
	
	protected int heightLimit;
	protected double heightDamage;
	protected boolean useMoney = false;
	protected int enemyKillMoney = 0;
	protected int enemyHitMoney = 0;
	protected int friendlyKillMoney = 0;
	protected int friendlyHitMoney = 0;
	protected int byEnemyDeathMoney = 0;
	protected int byEnemyHitMoney = 0;
	protected int byFriendlyDeathMoney = 0;
	protected int byFriendlyHitMoney = 0;
	protected int suicideMoney = 0;
	
	public DefaultGame(ConfigurationSection section) throws LoadingException {
		for (String bonusName : section.getStringList("bonuses")) {
			try {
				bonuses.add(Flier.getInstance().getBonus(bonusName));
			} catch (LoadingException e) {
				throw (LoadingException) new LoadingException(String.format("Error in '%s' bonus.", bonusName))
						.initCause(e);
			}
		}
		heightLimit = ValueLoader.loadInt(section, "height_limit");
		heightDamage = ValueLoader.loadNonNegativeDouble(section, "height_damage");
		useMoney = section.getBoolean("money.enabled", useMoney);
		enemyKillMoney = section.getInt("money.enemy_kill", enemyKillMoney);
		enemyHitMoney = section.getInt("money.enemy_hit", enemyHitMoney);
		friendlyKillMoney = section.getInt("money.friendly_kill", friendlyKillMoney);
		friendlyHitMoney = section.getInt("money.friendly_hit", friendlyHitMoney);
		byEnemyDeathMoney = section.getInt("money.by_enemy_death", byEnemyDeathMoney);
		byEnemyHitMoney = section.getInt("money.by_enemy_hit", byEnemyHitMoney);
		byFriendlyDeathMoney = section.getInt("money.by_friendly_death", byFriendlyDeathMoney);
		byFriendlyHitMoney = section.getInt("money.by_friendly_hit", byFriendlyHitMoney);
		suicideMoney = section.getInt("money.suicide", suicideMoney);
	}
	
	public class GameHeartBeat extends BukkitRunnable {
		
		private int i = 0;
		private DefaultGame game;
		
		public GameHeartBeat(DefaultGame game) {
			this.game = game;
			runTaskTimer(Flier.getInstance(), 1, 1);
		}

		@Override
		public void run() {
			for (Bonus bonus : bonuses) {
				bonus.update();
			}
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
			if (heightLimit > 0 && i % 20 == 0) {
				for (InGamePlayer data : getPlayers().values()) {
					if (data.getPlayer().getLocation().getY() > heightLimit) {
						data.getPlayer().damage(heightDamage);
					}
				}
			}
			i++;
			if (i > 1000) {
				i = 0;
			}
		}
	}	

	/**
	 * The game should do game-specific stuff in a fast tick here.
	 */
	public abstract void fastTick();;

	/**
	 * The game should do game-specific stuff in a slow tick here.
	 */
	public abstract void slowTick();
	
	/**
	 * Handles one player killing another one.
	 * In case of suicide killer is null.
	 * 
	 * @param killer the player who killed another
	 * @param killed the player who was killed
	 */
	public abstract void handleKill(InGamePlayer killer, InGamePlayer killed);
	
	/**
	 * Handles one player hitting another one with a Damager.
	 * 
	 * @param result result of the hit; it's unmodifiable now
	 * @param attacker the attacking player
	 * @param attacked the attacked player
	 * @param damager the Damager used in the hit
	 */
	public abstract void handleHit(DamageResult result, InGamePlayer attacker, InGamePlayer attacked, Damager damager);
	
	/**
	 * Should return the exact place to respawn the player. Use it if you want
	 * to do something special after respawning the player, or just return
	 * lobby.getSpawn().
	 * 
	 * @param player
	 *            player who is about to be respawned
	 * @return the location where the player will be respawned
	 */
	public abstract Location getRespawnLocation(InGamePlayer player);
	
	/**
	 * This method is called for the respawned player. Use it if you want to do
	 * something special after respawning the player, or just pass him to
	 * lobby.respawnPlayer().
	 * 
	 * @param player
	 *            the player who has just respawned
	 */
	public abstract void afterRespawn(InGamePlayer player);

	@Override
	public void addPlayer(InGamePlayer data) {
		UUID uuid = data.getPlayer().getUniqueId();
		if (dataMap.isEmpty()) {
			start();
		} else if (dataMap.containsKey(uuid)) {
			return;
		}
		dataMap.put(uuid, data);
		data.getLines().add(new Fuel(data));
		data.getLines().add(new Health(data));
		data.getLines().add(new Speed(data));
		data.getLines().add(new Altitude(data));
		data.getLines().add(new Ammo(data));
		data.getLines().add(new Reload(data));
		if (useMoney) {
			data.getLines().add(new Money(data));
		}
	}
	
	@Override
	public void startPlayer(InGamePlayer data) {
		new BukkitRunnable() {
			@Override
			public void run() {
				data.setPlaying(true);
			}
		}.runTaskLater(Flier.getInstance(), 20);
	}
	
	@Override
	public void removePlayer(InGamePlayer data) {
		dataMap.remove(data.getPlayer().getUniqueId());
		if (dataMap.isEmpty()) {
			stop();
		}
	}
	
	@Override
	public Map<UUID, InGamePlayer> getPlayers() {
		return Collections.unmodifiableMap(dataMap);
	}

	@Override
	public void start() {
		heartBeat = new GameHeartBeat(this);
		Bukkit.getPluginManager().registerEvents(this, Flier.getInstance());
		for (Bonus bonus : bonuses) {
			bonus.start();
		}
	}

	@Override
	public void stop() {
		HandlerList.unregisterAll(this);
		heartBeat.cancel();
		Set<InGamePlayer> copy = new HashSet<>(dataMap.values());
		for (InGamePlayer data : copy) {
			data.exitGame();
		}
		for (Bonus bonus : bonuses) {
			bonus.stop();
		}
	}
	
	@Override
	public List<Bonus> getBonuses() {
		return bonuses;
	}
	
	@Override
	public int getHeightLimit() {
		return heightLimit;
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onClick(PlayerInteractEvent event) {
		InGamePlayer data = getPlayers().get(event.getPlayer().getUniqueId());
		if (data != null) {
			switch (event.getAction()) {
			case LEFT_CLICK_AIR:
			case LEFT_CLICK_BLOCK:
				data.leftClick();
				break;
			case RIGHT_CLICK_AIR:
			case RIGHT_CLICK_BLOCK:
				data.rightClick();
				break;
			default:
				break;
			}
		}
	}
	
	@EventHandler
	public void onHit(EntityDamageByEntityEvent event) {
		// the damaged player is in game
		InGamePlayer player = getPlayers().get(event.getEntity().getUniqueId());
		if (player == null) {
			return;
		}
		event.setCancelled(true);
		event.getDamager().remove();
		// the damage was done with a Damager
		Attacker weapon = Damager.getDamager(event.getDamager());
		if (weapon == null) {
			return;
		}
		DamageResult result = player.damage(weapon.getAttacker(), weapon.getDamager());
		handleHit(result, weapon.getAttacker(), player, weapon.getDamager());
		InGamePlayer shooter = weapon.getAttacker();
		if (result != DamageResult.NOTHING && shooter != null) {
			Attitude a = getAttitude(shooter, player);
			if (a == Attitude.FRIENDLY) {
				pay(shooter, friendlyHitMoney);
				pay(player, byFriendlyHitMoney);
			} else if (a == Attitude.HOSTILE) {
				pay(shooter, enemyHitMoney);
				pay(player, byEnemyHitMoney);
			}
		}
	}
	
	private void pay(InGamePlayer player, int amount) {
		player.setMoney(player.getMoney() + amount);
	}
	
	@EventHandler
	public void onExplode(ExplosionPrimeEvent event) {
		if (event.isCancelled()) {
			return;
		}
		Attacker weapon = Damager.getDamager(event.getEntity());
		if (weapon == null) {
			return;
		}
		if (!weapon.getDamager().isExploding()) {
			event.setCancelled(true);
		}
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
			killed.getPlayer().setGlowing(false);
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
				Attitude a = getAttitude(killer, killed);
				if (a == Attitude.FRIENDLY) {
					pay(killer, friendlyKillMoney);
					pay(killed, byFriendlyDeathMoney);
				} else if (a == Attitude.HOSTILE) {
					pay(killer, enemyKillMoney);
					pay(killed, byEnemyDeathMoney);
				}
			} else {
				event.setDeathMessage(Utils.formatPlayer(killed) + " commited suicide...");
				handleKill(null, killed);
				pay(killed, suicideMoney);
			}
		}
	}
	
	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		InGamePlayer player = getPlayers().get(event.getPlayer().getUniqueId());
		if (player == null) {
			return;
		}
		event.getPlayer().setVelocity(new Vector());
		event.setRespawnLocation(getRespawnLocation(player));
		Bukkit.getScheduler().runTask(Flier.getInstance(), () -> afterRespawn(player));
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
	public void onInvInteract(InventoryClickEvent event) {
		if (getPlayers().containsKey(event.getWhoClicked().getUniqueId())) {
			event.setCancelled(true);
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
