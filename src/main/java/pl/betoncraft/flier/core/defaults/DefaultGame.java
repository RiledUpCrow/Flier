/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core.defaults;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Bonus;
import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.api.content.Wings;
import pl.betoncraft.flier.api.core.Damager;
import pl.betoncraft.flier.api.core.Damager.DamageResult;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.Usage;
import pl.betoncraft.flier.event.FlierHitPlayerEvent;
import pl.betoncraft.flier.sidebar.Altitude;
import pl.betoncraft.flier.sidebar.Ammo;
import pl.betoncraft.flier.sidebar.Fuel;
import pl.betoncraft.flier.sidebar.Health;
import pl.betoncraft.flier.sidebar.Money;
import pl.betoncraft.flier.sidebar.Reload;
import pl.betoncraft.flier.sidebar.Speed;
import pl.betoncraft.flier.util.EffectListener;
import pl.betoncraft.flier.util.Position;
import pl.betoncraft.flier.util.Utils;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * Basic rules of a game.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultGame implements Listener, Game {

	protected final ValueLoader loader;

	protected GameHeartBeat heartBeat;
	protected Map<UUID, InGamePlayer> dataMap = new HashMap<>();
	protected List<Bonus> bonuses = new ArrayList<>();
	protected EffectListener listener;
	
	protected int heightLimit;
	protected double heightDamage;
	protected Location center;
	protected int radius;
	protected boolean useMoney;
	protected int enemyKillMoney;
	protected int enemyHitMoney;
	protected int friendlyKillMoney;
	protected int friendlyHitMoney;
	protected int byEnemyDeathMoney;
	protected int byEnemyHitMoney;
	protected int byFriendlyDeathMoney;
	protected int byFriendlyHitMoney;
	protected int suicideMoney;
	
	private int minX, minZ, maxX, maxZ;
	
	public DefaultGame(ConfigurationSection section) throws LoadingException {
		loader = new ValueLoader(section);
		Flier flier = Flier.getInstance();
		for (String bonusName : section.getStringList("bonuses")) {
			bonuses.add(flier.getBonus(bonusName));
		}
		listener = new EffectListener(section.getStringList("effects"), this);
		heightLimit = loader.loadInt("height_limit", 512);
		heightDamage = loader.loadNonNegativeDouble("height_damage", 0.5);
		center = loader.loadLocation("center");
		radius = loader.loadPositiveInt("radius");
		minX = center.getBlockX() - radius;
		maxX = center.getBlockX() + radius;
		minZ = center.getBlockZ() - radius;
		maxZ = center.getBlockZ() + radius;
		useMoney = loader.loadBoolean("money.enabled", false);
		enemyKillMoney = loader.loadInt("money.enemy_kill", 0);
		enemyHitMoney = loader.loadInt("money.enemy_hit", 0);
		friendlyKillMoney = loader.loadInt("money.friendly_kill", 0);
		friendlyHitMoney = loader.loadInt("money.friendly_hit", 0);
		byEnemyDeathMoney = loader.loadInt("money.by_enemy_death", 0);
		byEnemyHitMoney = loader.loadInt("money.by_enemy_hit", 0);
		byFriendlyDeathMoney = loader.loadInt("money.by_friendly_death", 0);
		byFriendlyHitMoney = loader.loadInt("money.by_friendly_hit", 0);
		suicideMoney = loader.loadInt("money.suicide", 0);
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
				Location loc = data.getPlayer().getLocation();
				if (loc.getBlockX() < minX || loc.getBlockX() > maxX ||
						loc.getBlockZ() < minZ || loc.getBlockZ() > maxZ) {
					data.getPlayer().damage(data.getPlayer().getHealth() + 1);
				}
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
	public abstract void handleHit(List<DamageResult> result, InGamePlayer attacker, InGamePlayer attacked, Damager damager);
	
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
		data.getPlayer().getInventory().setHeldItemSlot(0);
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
		Collection<InGamePlayer> copy = new ArrayList<>(dataMap.values());
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
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onClick(PlayerInteractEvent event) {
		InGamePlayer data = getPlayers().get(event.getPlayer().getUniqueId());
		if (data != null) {
			event.setCancelled(true);
			ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
			Wings wings = data.getClazz().getWings();
			if (item != null && wings != null && item.isSimilar(wings.getItem())) {
				// handle wearing wings
				event.getPlayer().getInventory().setChestplate(item);
				event.getPlayer().getInventory().setItemInMainHand(null);
				event.getPlayer().getWorld().playSound(
						event.getPlayer().getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1, 1);
			} else {
				// handle a click
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
	}
	
	@EventHandler(priority=EventPriority.LOW)
	public void onHit(EntityDamageByEntityEvent event) {
		// if the damager is another player, cancel the event - melee not allowed
		if (event.getDamager() instanceof Player && getPlayers().containsKey(event.getDamager().getUniqueId())) {
			event.setCancelled(true);
			return;
		}
		// get stuff involved in the attack
		InGamePlayer player = getPlayers().get(event.getEntity().getUniqueId());
		Utils.Attacker weapon = Utils.getDamager(event.getDamager());
		// it's a weapon, so remove the attacking entity
		if (weapon != null) {
			event.getDamager().remove();
		}
		// cancel event if it's involved in the game
		if (weapon != null || player != null) {
			event.setCancelled(true);
		}
		// stop if the weapon was not used or in-game player was not attacked
		if (weapon == null || player == null) {
			return;
		}
		// weapon was used on in-game player, process the attack
		List<DamageResult> result = damage(player, weapon.getAttacker(), weapon.getDamager());
		InGamePlayer shooter = weapon.getAttacker();
		// fire an event
		FlierHitPlayerEvent hitEvent = new FlierHitPlayerEvent(shooter, player, result, weapon.getDamager());
		Bukkit.getPluginManager().callEvent(hitEvent);
		if (hitEvent.isCancelled()) {
			return;
		}
		// handle the hit
		handleHit(result, weapon.getAttacker(), player, weapon.getDamager());
		// handle a general hit
		if (result.contains(DamageResult.HIT) && shooter != null) {
			// pay money for a hit
			Attitude a = getAttitude(shooter, player);
			if (a == Attitude.FRIENDLY) {
				pay(shooter, friendlyHitMoney);
				pay(player, byFriendlyHitMoney);
			} else if (a == Attitude.HOSTILE) {
				pay(shooter, enemyHitMoney);
				pay(player, byEnemyHitMoney);
			}
			// display a message about the hit and play the sound to the shooter if he exists and if he hit someone else
			if (shooter != null && !shooter.equals(player)) {
				shooter.getPlayer().sendMessage(ChatColor.YELLOW + "You managed to hit " + Utils.formatPlayer(player) + "!");
			}
		}
		// handle physical damage
		if (result.contains(DamageResult.REGULAR_DAMAGE)) {
			event.setCancelled(false);
			event.setDamage(weapon.getDamager().getPhysical());
		}
		// handle taking wings off
		if (result.contains(DamageResult.WINGS_OFF)) {
			player.takeWingsOff();
		}
		// handle wing damage
		if (result.contains(DamageResult.WINGS_DAMAGE)) {
			player.getClazz().getWings().removeHealth(weapon.getDamager().getDamage());
		}
	}

	public List<DamageResult> damage(InGamePlayer attacked, InGamePlayer attacker, Damager damager) {
		List<DamageResult> list = new ArrayList<>(3);
		// player is not playing, nothing happens
		if (!attacked.isPlaying()) {
			return list;
		}
		Player shooter = attacker == null ? null : attacker.getPlayer();
		// ignore if you can's commit suicide with this weapon
		if (shooter != null && shooter.equals(attacked) && !damager.suicidal()) {
			return list;
		}
		// flying, handle air attack
		if (Position.check(attacked.getPlayer(), Usage.Where.NO_FALL)) {
			attacked.setAttacker(attacker);
			list.add(DamageResult.HIT);
			if (Position.check(attacked.getPlayer(), Usage.Where.AIR)) {
				if (damager.wingsOff()) {
					list.add(DamageResult.WINGS_OFF);
				}
				if (damager.midAirPhysicalDamage()) {
					list.add(DamageResult.REGULAR_DAMAGE);
				}
				list.add(DamageResult.WINGS_DAMAGE);
			} else if (Position.check(attacked.getPlayer(), Usage.Where.GROUND)) {
				list.add(DamageResult.REGULAR_DAMAGE);
			}
		}
		return list;
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onDamage(EntityDamageEvent event) {
		if (event.isCancelled()) {
			return;
		}
		// handle death
		InGamePlayer killed = getPlayers().get(event.getEntity().getUniqueId());
		if (killed != null && killed.getPlayer().getHealth() - event.getFinalDamage() <= 0) {
			event.setCancelled(true);
			Utils.clearPlayer(killed.getPlayer());
			killed.setPlaying(false);
			InGamePlayer lastHit = killed.getAttacker();
			InGamePlayer killer = lastHit == null ? null : getPlayers().get(lastHit.getPlayer().getUniqueId());
			killed.setAttacker(null);
			killed.getPlayer().setGlowing(false);
			if (killer != null && !killer.equals(killed)) {
				switch (event.getCause()) {
				case FALL:
					notifyAllPlayers(String.format("%s was shot down by %s!",
							Utils.formatPlayer(killed), Utils.formatPlayer(killer)));
					break;
				default:
					notifyAllPlayers(String.format("%s was killed by %s!",
							Utils.formatPlayer(killed), Utils.formatPlayer(killer)));
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
				notifyAllPlayers(String.format("%s commited suicide...", Utils.formatPlayer(killed)));
				handleKill(null, killed);
				pay(killed, suicideMoney);
			}
			afterRespawn(killed);
		}
	}
	
	private void notifyAllPlayers(String message) {
		for (InGamePlayer player : dataMap.values()) {
			player.getPlayer().sendMessage(message);
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
		Utils.Attacker weapon = Utils.getDamager(event.getEntity());
		if (weapon == null) {
			return;
		}
		if (!weapon.getDamager().isExploding()) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBlockExplode(EntityExplodeEvent event) {
		if (Utils.getDamager(event.getEntity()) != null) {
			event.blockList().clear();
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
