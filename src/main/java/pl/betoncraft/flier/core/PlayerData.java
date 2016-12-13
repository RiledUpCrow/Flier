/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import pl.betoncraft.flier.api.Damager;
import pl.betoncraft.flier.api.Damager.DamageResult;
import pl.betoncraft.flier.api.Engine;
import pl.betoncraft.flier.api.Game;
import pl.betoncraft.flier.api.PlayerClass;
import pl.betoncraft.flier.api.UsableItem;
import pl.betoncraft.flier.api.Wings;
import pl.betoncraft.flier.core.Utils.ImmutableVector;

/**
 * Stores data about the player.
 *
 * @author Jakub Sapalski
 */
public class PlayerData {
	
	private Player player;
	private Game game;
	private PlayerClass clazz;
	private boolean isPlaying;

	private Location returnLoc;
	private Scoreboard sb;
	private int customIndex = 0;
	private PlayerData lastHit = null;
	private ChatColor color;
	
	private long glowTimer;
	
	private double fuel;
	private double health;
	
	public PlayerData(Player player, Game game) {
		this.player = player;
		this.game = game;
		returnLoc = player.getLocation();
		sb = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective stats = sb.registerNewObjective("stats", "dummy");
		stats.setDisplaySlot(DisplaySlot.SIDEBAR);
		stats.setDisplayName("Stats");
		player.setScoreboard(sb);
	}
	
	public void fastTick() {
		if (!isPlaying()) {
			return;
		}
		Player player = getPlayer();
		if (isFlying()) {
			player.setVelocity(getWings().applyFlightModifications(this).toVector());
		}
		if (isAccelerating()) {
			speedUp();
		} else {
			regenerateFuel();
		}
		regenerateWings();
		cooldown();
	}
	
	public void slowTick() {
		if (!isPlaying()) {
			return;
		}
		stopGlowing();
		updateStats();
	}

	public Game getGame() {
		return game;
	}
	
	public PlayerClass getClazz() {
		return clazz;
	}
	
	public void setClazz(PlayerClass clazz) {
		this.clazz = clazz;
		Engine engine = clazz.getEngine();
		Wings wings = clazz.getWings();
		Map<UsableItem, Integer> items = clazz.getItems();
		getPlayer().getInventory().clear();
		getPlayer().getInventory().setItemInOffHand(engine.getItem());
		setFuel(engine.getMaxFuel());
		getPlayer().getInventory().setChestplate(wings.getItem());
		setHealth(wings.getHealth());
		for (Entry<UsableItem, Integer> e : items.entrySet()) {
			UsableItem item = e.getKey();
			int amount = e.getValue();
			int slot = item.slot();
			ItemStack itemStack = item.getItem();
			itemStack.setAmount(amount);
			if (slot >= 0) {
				player.getInventory().setItem(slot, itemStack);
			} else {
				getPlayer().getInventory().addItem(itemStack);
			}
		}
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}
	
	public Engine getEngine() {
		return clazz.getEngine();
	}
	
	public Map<UsableItem, Integer> getItems() {
		return clazz.getItems();
	}
	
	public Wings getWings() {
		return clazz.getWings();
	}
	
	public Scoreboard getScoreboard() {
		return sb;
	}
	
	public boolean isAccelerating() {
		Player player = getPlayer();
		return player.isGliding() && player.isSneaking();
	}
	
	public boolean isFlying() {
		return getPlayer().isGliding();
	}
	
	public boolean isOnGround() {
		return ((Entity) getPlayer()).isOnGround();
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public void setLastHit(PlayerData player) {
		this.lastHit = player;
	}
	
	public PlayerData getLastHit() {
		return lastHit;
	}

	public Location getReturnLocation() {
		return returnLoc;
	}
	
	public UsableItem getHeldItem() {
		ItemStack item = getPlayer().getInventory().getItemInMainHand();
		if (item == null) {
			return null;
		}
		for (UsableItem i : getItems().keySet()) {
			if (i.getItem().isSimilar(item)) {
				return i;
			}
		}
		return null; 
	}
	
	public double getFuel() {
		return fuel;
	}
	
	public void setFuel(double fuel) {
		this.fuel = fuel;
	}
	
	public boolean addFuel(double amount, double max) {
		double fuel = getFuel();
		if (fuel >= max) {
			return false;
		}
		if (fuel + amount > max) {
			setFuel(max);
		} else {
			setFuel(fuel + amount);
		}
		return true;
	}
	
	public boolean removeFuel(double amount) {
		double fuel = getFuel();
		if (fuel <= 0) {
			return false;
		}
		if (fuel < amount) {
			setFuel(0);
		} else {
			setFuel(fuel - amount);
		}
		return true;
	}
	
	public double getHealth() {
		return health;
	}
	
	public void setHealth(double health) {
		this.health = health;
	}
	
	public boolean addHealth(double amount, double max) {
		double health = getHealth();
		if (health >= max) {
			return false;
		}
		if (health + amount > max) {
			setHealth(max);
		} else {
			setHealth(health + amount);
		}
		return true;
	}
	
	public boolean removeHealth(double amount) {
		double health = getHealth();
		if (health <= 0) {
			return false;
		}
		if (health < amount) {
			setHealth(0);
			destroyWings();
		} else {
			setHealth(health - amount);
		}
		return true;
	}
	
	public void startGlowing(int ticks) {
		getPlayer().setGlowing(true);
		glowTimer = System.currentTimeMillis() + 50*ticks;
	}
	
	public void stopGlowing() {
		if (System.currentTimeMillis() >= glowTimer) {
			getPlayer().setGlowing(false);
		}
	}
	
	public void updateColors() {
		Map<String, ChatColor> map = game.getColors();
		for (Entry<String, ChatColor> e : map.entrySet()) {
			String player = e.getKey();
			ChatColor color = e.getValue();
			String colorName = color.name().toLowerCase();
			org.bukkit.scoreboard.Team team = sb.getTeam(colorName);
			if (team == null) {
				team = sb.registerNewTeam(colorName);
			}
			team.addEntry(player.toString());
			team.setPrefix(ChatColor.COLOR_CHAR + "" + color.getChar());
		}
	}
	
	public ChatColor getColor() {
		return color;
	}
	
	public void setColor(ChatColor color) {
		this.color = color;
	}
	
	public void updateStats() {
		double f = getEngine() == null ? 0 : 100 * fuel / getEngine().getMaxFuel();
		double h = getWings() == null ? 0 : 100 * health / getWings().getHealth();
		double s = getPlayer().getVelocity().length() * 10;
		if (s < 1) {
			s = 0;
		}
		double a = getPlayer().getLocation().getY() - 64;
		setStatistic(customIndex + 4, String.format("F: %.1f%%", f));
		setStatistic(customIndex + 3, String.format("H: %.1f%%", h));
		setStatistic(customIndex + 2, String.format("S: %.1f~", s));
		setStatistic(customIndex + 1, String.format("A: %.1fm", a));
	}
	
	private void setStatistic(int index, String string) {
		for (String entry : sb.getEntries()) {
			Set<Score> scores = sb.getScores(entry);
			for (Score score : scores) {
				if (score.getScore() == index) {
					Objective objective = score.getObjective();
					sb.resetScores(entry);
					Score updatedScore = objective.getScore(string);
					updatedScore.setScore(index);
					return;
				}
			}
		}
		Score newScore = sb.getObjective(DisplaySlot.SIDEBAR).getScore(string);
		newScore.setScore(index);
	}
	
	public void addStatistic(String string) {
		customIndex++;
		setStatistic(customIndex, string);
		updateStats();
	}
	
	public void updateStatistic(int index, String string) {
		if (index >= customIndex) {
			throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + customIndex);
		}
		setStatistic(index + 1, string);
	}
	
	public double getWeight() {
		double weight = 0;
		weight += getEngine().getWeight();
		weight += getWings().getWeight();
		for (UsableItem item : getItems().keySet()) {
			weight += item.getWeight();
		}
		return weight;
	}
	
	public void speedUp() {
		Engine engine = getEngine();
		if (engine == null) {
			return;
		}
		if (!removeFuel(engine.getConsumption())) {
			return;
		}
		getPlayer().setVelocity(engine.launch(ImmutableVector.fromVector(getPlayer().getVelocity()),
				ImmutableVector.fromVector(getPlayer().getLocation().getDirection())).toVector());
		startGlowing(engine.getGlowTime());
	}
	
	public void regenerateFuel() {
		Engine engine = getEngine();
		if (engine == null) {
			return;
		}
		addFuel(engine.getRegeneration(), engine.getMaxFuel());
	}
	
	public void use() {
		UsableItem item = getHeldItem();
		if (item == null || (item.onlyAir() && !isFlying())) {
			return;
		}
		if (item.use(this) && item.isConsumable()) {
			ItemStack stack = getPlayer().getInventory().getItemInMainHand();
			if (stack.getAmount() == 1) {
				getPlayer().getInventory().setItemInMainHand(null); 
			} else {
				stack.setAmount(stack.getAmount() - 1);
			}
		}
	}
	
	public void cooldown() {
		for (UsableItem item : getItems().keySet()) {
			item.cooldown(this);
		}
	}
	
	public DamageResult damage(Damager damager) {
		if (isFlying()) {
			if (damager.wingsOff()) {
				return DamageResult.WINGS_OFF;
			} else {
				return DamageResult.WINGS_DAMAGE;
			}
		} else if (Utils.getAltitude(getPlayer().getLocation(), 4) != 4) {
			if (damager.killsOnGround()) {
				return DamageResult.INSTANT_KILL;
			} else {
				return DamageResult.REGULAR_DAMAGE;
			}
		} else {
			return DamageResult.NOTHING;
		}
	}
	
	public void takeWingsOff() {
		Player player = getPlayer();
		ItemStack elytra = player.getInventory().getChestplate();
		if (elytra != null) {
			player.getInventory().setChestplate(null);
			player.getInventory().setItem(1, elytra);
		}
	}
	
	public void destroyWings() {
		Player player = getPlayer();
		player.getInventory().setChestplate(null);
		player.getInventory().setItem(1, null);
	}
	
	public void regenerateWings() {
		Wings wings = getWings();
		if (wings == null) {
			return;
		}
		addHealth(wings.getRegeneration(), wings.getHealth());
	}

	public void clearStats() {
		customIndex = 0;
	}
	
	public void clear() {
		getPlayer().setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		getPlayer().getInventory().clear();
		getPlayer().setHealth(getPlayer().getMaxHealth());
		getPlayer().setFoodLevel(20);
		getPlayer().setExhaustion(20);
		getPlayer().getActivePotionEffects().clear();
		getPlayer().getVelocity().zero();
		getPlayer().setGlowing(false);
	}

}
