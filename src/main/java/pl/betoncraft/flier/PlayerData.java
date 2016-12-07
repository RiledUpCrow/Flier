/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import pl.betoncraft.flier.api.Damager;
import pl.betoncraft.flier.api.Damager.DamageResult;
import pl.betoncraft.flier.api.Engine;
import pl.betoncraft.flier.api.UsableItem;
import pl.betoncraft.flier.api.Wings;

/**
 * Stores data about the player.
 *
 * @author Jakub Sapalski
 */
public class PlayerData {
	
	private Player player;
	private Scoreboard sb;
	private int customIndex = 0;
	
	private long glowTimer;
	
	private double fuel;
	private double health;

	private Engine engine;
	private Map<UsableItem, Integer> items = new HashMap<>();
	private Wings wings;
	
	public PlayerData(Player player) {
		this.player = player;
		sb = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective stats = sb.registerNewObjective("stats", "dummy");
		stats.setDisplaySlot(DisplaySlot.SIDEBAR);
		stats.setDisplayName("Stats");
		player.setScoreboard(sb);
	}
	
	public Engine getEngine() {
		return engine;
	}
	
	public void setEngine(Engine engine) {
		this.engine = engine;
		getPlayer().getInventory().setItemInOffHand(engine.getItem());
		setFuel(engine.getMaxFuel());
	}
	
	public Map<UsableItem, Integer> getItems() {
		return items;
	}
	
	public void addItem(UsableItem item, int amount) {
		int slot = item.slot();
		getItems().put(item, amount);
		ItemStack itemStack = item.getItem();
		itemStack.setAmount(amount);
		if (slot >= 0) {
			player.getInventory().setItem(slot, itemStack);
		} else {
			getPlayer().getInventory().addItem(itemStack);
		}
	}
	
	public Wings getWings() {
		return wings;
	}
	
	public void setWings(Wings wings) {
		this.wings = wings;
		getPlayer().getInventory().setChestplate(wings.getItem());
		setHealth(wings.getHealth());
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
	
	public void stopGlowing() {
		if (System.currentTimeMillis() >= glowTimer) {
			getPlayer().setGlowing(false);
		}
	}
	
	public void startGlowing(int ticks) {
		getPlayer().setGlowing(true);
		glowTimer = System.currentTimeMillis() + 50*ticks;
	}
	
	public void setTeamColors(Map<String, ChatColor> map) {
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
	
	public void applyFlightModifications() {
		Vector velocity = getPlayer().getVelocity().clone();
		double horizontalSpeed = Math.sqrt((velocity.getX() * velocity.getX()) + (velocity.getZ() * velocity.getZ()));
		double liftingForce = getWings().getLiftingForce() * horizontalSpeed;
		double weight = getWeight();
		velocity.add(new Vector(0, 1, 0).multiply(liftingForce - weight));
		double aerodynamics = getWings().getAerodynamics();
		Vector airResistance = velocity.clone().multiply(aerodynamics);
		velocity.add(airResistance);
		if (!velocity.equals(getPlayer().getVelocity())) {
			getPlayer().setVelocity(velocity);
		}
	}
	
	public void launch(double minSpeed, double acceleration, double maxSpeed) {
		Player player = getPlayer();
		Vector velocity = player.getVelocity();
		double speed = velocity.length();
		
		if (speed > maxSpeed) {
			speed = 0;
		} else if (speed < minSpeed) {
			speed = minSpeed;
		}
		Vector direction = player.getLocation().getDirection();
		velocity.add(direction.multiply(speed * getEngine().getAcceleration()));

//		// different algorithm
//		if (speed > maxSpeed) {
//			return;
//		}
//		if (speed < minSpeed) {
//			Vector direction = player.getLocation().getDirection();
//			velocity.add(direction.multiply(acceleration));
//		} else {
//			velocity.multiply(acceleration + 1);
//		}
//		speed = velocity.length();
//		if (speed > maxSpeed) {
//			velocity.multiply(maxSpeed / speed);
//		}
		
		player.setVelocity(velocity);
	}
	
	public void speedUp() {
		Engine engine = getEngine();
		if (engine == null) {
			return;
		}
		if (!removeFuel(engine.getConsumption())) {
			return;
		}
		launch(engine.getMinSpeed(), engine.getAcceleration(), engine.getMaxSpeed());
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
		if (item == null) {
			return;
		}
		int amount = getItems().get(item);
		if (amount == 0) {
			return;
		}
		if (item.isConsumable()) {
			amount--;
			getItems().put(item, amount);
		}
		item.use(this);
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
