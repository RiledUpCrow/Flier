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
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
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
import pl.betoncraft.flier.event.FlierPlayerKillEvent;
import pl.betoncraft.flier.event.FlierPlayerKillEvent.Type;
import pl.betoncraft.flier.sidebar.Altitude;
import pl.betoncraft.flier.sidebar.Ammo;
import pl.betoncraft.flier.sidebar.Fuel;
import pl.betoncraft.flier.sidebar.Health;
import pl.betoncraft.flier.sidebar.Money;
import pl.betoncraft.flier.sidebar.Reload;
import pl.betoncraft.flier.sidebar.Speed;
import pl.betoncraft.flier.util.EffectListener;
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
	
	@Override
	public void handleKill(InGamePlayer killer, InGamePlayer killed, boolean fall) {
		if (killer != null && !killer.equals(killed)) {
			if (fall) {
				notifyAllPlayers(String.format("%s was shot down by %s!",
						Utils.formatPlayer(killed), Utils.formatPlayer(killer)));
			} else {
				notifyAllPlayers(String.format("%s was killed by %s!",
						Utils.formatPlayer(killed), Utils.formatPlayer(killer)));
			}
			// fire an event
			FlierPlayerKillEvent deathEvent = new FlierPlayerKillEvent(killer, killed,
					fall ? Type.SHOT_DOWN : Type.KILLED);
			Bukkit.getPluginManager().callEvent(deathEvent);
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
			// fire an event
			FlierPlayerKillEvent deathEvent = new FlierPlayerKillEvent(killed, killed,
					fall ? Type.SHOT_DOWN : Type.KILLED);
			Bukkit.getPluginManager().callEvent(deathEvent);
			pay(killed, suicideMoney);
		}
		Utils.clearPlayer(killed.getPlayer());
		killed.setPlaying(false);
		killed.setAttacker(null);
		afterRespawn(killed);
	}
	
	@Override
	public void handleHit(InGamePlayer attacker, InGamePlayer attacked, Damager damager) {
		List<DamageResult> results = attacked.damage(attacker, damager);
		// handle a general hit
		if (results.contains(DamageResult.HIT) && attacker != null) {
			attacked.setAttacker(attacker);
			attacked.setNoDamageTicks(damager.getNoDamageTicks());
			// pay money for a hit
			Attitude a = getAttitude(attacker, attacked);
			if (a == Attitude.FRIENDLY) {
				pay(attacker, friendlyHitMoney);
				pay(attacked, byFriendlyHitMoney);
			} else if (a == Attitude.HOSTILE) {
				pay(attacker, enemyHitMoney);
				pay(attacked, byEnemyHitMoney);
			}
			// display a message about the hit and play the sound to the shooter if he exists and if he hit someone else
			if (attacker != null && !attacker.equals(this)) {
				attacker.getPlayer().sendMessage(ChatColor.YELLOW + "You managed to hit " + Utils.formatPlayer(attacked) + "!");
			}
		}
		// handle physical damage
		if (results.contains(DamageResult.REGULAR_DAMAGE)) {
			attacked.getPlayer().setNoDamageTicks(0);
			attacked.getPlayer().damage(damager.getPhysical());
		}
		// handle taking wings off
		if (results.contains(DamageResult.WINGS_OFF)) {
			attacked.takeWingsOff();
		}
		// handle wing damage
		if (results.contains(DamageResult.WINGS_DAMAGE)) {
			attacked.getClazz().getWings().removeHealth(damager.getDamage());
		}
	}
	
	@Override
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
		handleHit(weapon.getAttacker(), player, weapon.getDamager());
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
			InGamePlayer lastHit = killed.getAttacker();
			InGamePlayer killer = lastHit == null ? null : getPlayers().get(lastHit.getPlayer().getUniqueId());
			handleKill(killer, killed, event.getCause() == DamageCause.FALL);
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
