/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import com.google.common.collect.Lists;

import pl.betoncraft.flier.api.Bonus;
import pl.betoncraft.flier.api.Damager;
import pl.betoncraft.flier.api.Damager.DamageResult;
import pl.betoncraft.flier.api.Effect;
import pl.betoncraft.flier.api.Engine;
import pl.betoncraft.flier.api.Game;
import pl.betoncraft.flier.api.InGamePlayer;
import pl.betoncraft.flier.api.Item;
import pl.betoncraft.flier.api.PlayerClass;
import pl.betoncraft.flier.api.SidebarLine;
import pl.betoncraft.flier.api.UsableItem;
import pl.betoncraft.flier.api.Wings;
import pl.betoncraft.flier.core.Utils.ImmutableVector;

/**
 * Stores data about the player.
 *
 * @author Jakub Sapalski
 */
public class PlayerData implements InGamePlayer {
	
	private Player player;
	private Game game;
	private PlayerClass clazz;
	private boolean isPlaying;

	private Location returnLoc;
	private Scoreboard sb;
	private List<SidebarLine> lines = new LinkedList<>();
	private InGamePlayer lastHit = null;
	private ChatColor color;
	private long glowTimer;
	private List<Effect> activeEffects = new LinkedList<>();

	private int money;
	
	public PlayerData(Player player, Game game, PlayerClass clazz) {
		this.player = player;
		this.game = game;
		this.clazz = clazz;
		returnLoc = player.getLocation();
		sb = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective stats = sb.registerNewObjective("stats", "dummy");
		stats.setDisplaySlot(DisplaySlot.SIDEBAR);
		stats.setDisplayName("Stats");
		player.setScoreboard(sb);
		updateClass();
	}
	
	@Override
	public void fastTick() {
		if (isPlaying()) {
			if (isFlying()) {
				player.setVelocity(clazz.getCurrentWings().applyFlightModifications(this).toVector());
			}
			if (isAccelerating()) {
				speedUp();
			} else {
				regenerateFuel();
			}
			applyEffects(true);
			regenerateWings();
			checkBonuses();
		}
	}

	private void checkBonuses() {
		List<Bonus> bonuses = game.getBonuses();
		for (Bonus bonus : bonuses) {
			if (!bonus.isAvailable()) {
				continue;
			}
			double distSqrd = bonus.distance() * bonus.distance();
			if (bonus.getLocation().distanceSquared(player.getLocation()) <= distSqrd) {
				bonus.apply(this);
			}
		}
	}

	@Override
	public void slowTick() {
		if (isPlaying()) {
			applyEffects(false);
		}
		stopGlowing();
		updateStats();
	}
	
	@Override
	public void use() {
		if (!isPlaying()) {
			return;
		}
		Item i = getHeldItem();
		if (i instanceof UsableItem) {
			UsableItem item = (UsableItem) i;
			if (item == null || (item.onlyAir() && !isFlying()) || !item.cooldown(this)) {
				return;
			}
			if (item.use(this) && item.isConsumable()) {
				// class
				clazz.getCurrentItems().put(item, clazz.getCurrentItems().get(item) - 1);
				// inventory
				ItemStack stack = getPlayer().getInventory().getItemInMainHand();
				if (stack.getAmount() == 1) {
					getPlayer().getInventory().setItemInMainHand(null); 
				} else {
					stack.setAmount(stack.getAmount() - 1);
				}
			}
		}
	}
	
	@Override
	public DamageResult damage(InGamePlayer attacker, Damager damager) {
		DamageResult result = DamageResult.NOTHING;
		if (!isPlaying()) {
			return result;
		}
		Player shooter = attacker.getPlayer();
		// was hit by himself
		if (shooter.equals(player)) {
			// ignore if you can's commit suicide with this weapon
			if (!damager.suicidal()) {
				return result;
			}
		}
		boolean notify = true;
		boolean sound = true;
		if (isFlying()) { // flying, handle air attack
			setAttacker(attacker);
			if (damager.wingsOff()) {
				takeWingsOff();
				result = DamageResult.WINGS_OFF;
			} else {
				result = DamageResult.WINGS_DAMAGE;
			}
			if (clazz.getCurrentWings().removeHealth(damager.getDamage())) {
				destroyWings();
			}
		} else if (Utils.getAltitude(getPlayer().getLocation(), 4) != 4) { // in general proximity of the ground,
			setAttacker(attacker);                                         // handle ground attack
			if (damager.killsOnGround()) {
				notify = false;
				getPlayer().damage(player.getHealth() + 1);
				result = DamageResult.INSTANT_KILL;
			} else {
				double damage = damager.getPhysical();
				if (player.getPlayer().getHealth() <= damage) {
					notify = false;
				}
				player.getPlayer().damage(damage);
				result = DamageResult.REGULAR_DAMAGE;
			}
		} else { // falling from a high place, do not attack
			notify = false;
			sound = false;
		}
		if (notify) {
			shooter.sendMessage(ChatColor.YELLOW + "You managed to hit " + Utils.formatPlayer(this) + "!");
		}
		if (sound) {
			shooter.playSound(shooter.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 1);
			player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_HURT, 1, 1);
		}
		return result;
	}

	@Override
	public Game getGame() {
		return game;
	}
	
	@Override
	public Player getPlayer() {
		return player;
	}
	
	@Override
	public double getWeight() {
		double weight = 0;
		weight += clazz.getCurrentEngine().getWeight();
		weight += clazz.getCurrentWings().getWeight();
		for (Item item : clazz.getCurrentItems().keySet()) {
			weight += item.getWeight();
		}
		return weight;
	}

	@Override
	public boolean isPlaying() {
		return isPlaying;
	}

	@Override
	public void setPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}
	
	@Override
	public PlayerClass getClazz() {
		return clazz;
	}
	
	@Override
	public void updateClass() {
		Engine engine = clazz.getCurrentEngine();
		Wings wings = clazz.getCurrentWings();
		Map<Item, Integer> items = clazz.getCurrentItems();
		getPlayer().getInventory().clear();
		getPlayer().getInventory().setItemInOffHand(engine.getItem());
		getPlayer().getInventory().setChestplate(wings.getItem());
		for (Entry<Item, Integer> e : items.entrySet()) {
			Item item = e.getKey();
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
	
	@Override
	public InGamePlayer getAttacker() {
		return lastHit;
	}
	
	@Override
	public void setAttacker(InGamePlayer player) {
		this.lastHit = player;
	}

	@Override
	public int getMoney() {
		return money;
	}

	@Override
	public void setMoney(int amount) {
		money = amount;
		if (money < 0) {
			money = 0;
		}
	}

	@Override
	public void addEffect(Effect effect) {
		activeEffects.add(effect);
	}

	@Override
	public void removeEffect(Effect effect) {
		activeEffects.remove(effect);
	}
	
	@Override
	public ChatColor getColor() {
		return color;
	}
	
	@Override
	public void setColor(ChatColor color) {
		this.color = color;
	}
	
	@Override
	public void updateColors(Map<String, ChatColor> map) {
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

	@Override
	public List<SidebarLine> getLines() {
		return lines;
	}
	
	@Override
	public void clear() {
		player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		player.getInventory().clear();
		player.setHealth(player.getMaxHealth());
		player.setFoodLevel(40);
		player.setExhaustion(0);
		player.getActivePotionEffects().clear();
		player.setGlowing(false);
		player.setVelocity(new Vector());
		player.teleport(returnLoc);
	}
	
	private boolean isAccelerating() {
		return player.isGliding() && player.isSneaking();
	}
	
	private boolean isFlying() {
		return player.isGliding();
	}
	
	private Item getHeldItem() {
		ItemStack item = player.getInventory().getItemInMainHand();
		if (item == null) {
			return null;
		}
		for (Item i : clazz.getCurrentItems().keySet()) {
			if (i.getItem().isSimilar(item)) {
				return i;
			}
		}
		return null; 
	}
	
	private void startGlowing(int ticks) {
		player.setGlowing(true);
		glowTimer = System.currentTimeMillis() + 50*ticks;
	}
	
	private void stopGlowing() {
		if (System.currentTimeMillis() >= glowTimer) {
			getPlayer().setGlowing(false);
		}
	}
	
	private void speedUp() {
		Engine engine = clazz.getCurrentEngine();
		if (engine == null) {
			return;
		}
		if (!engine.removeFuel(engine.getConsumption())) {
			return;
		}
		getPlayer().setVelocity(engine.launch(ImmutableVector.fromVector(getPlayer().getVelocity()),
				ImmutableVector.fromVector(getPlayer().getLocation().getDirection())).toVector());
		startGlowing(engine.getGlowTime());
	}
	
	private void regenerateFuel() {
		Engine engine = clazz.getCurrentEngine();
		if (engine == null) {
			return;
		}
		engine.addFuel(engine.getRegeneration());
	}
	
	private void regenerateWings() {
		Wings wings = clazz.getCurrentWings();
		if (wings == null) {
			return;
		}
		wings.addHealth(wings.getRegeneration());
	}
	
	private void takeWingsOff() {
		Player player = getPlayer();
		ItemStack elytra = player.getInventory().getChestplate();
		if (elytra != null) {
			player.getInventory().setChestplate(null);
			player.getInventory().setItem(1, elytra);
		}
	}
	
	private void destroyWings() {
		Player player = getPlayer();
		player.getInventory().setChestplate(null);
		player.getInventory().setItem(1, null);
	}
	
	private void updateStats() {
		int i = 0;
		for (SidebarLine line : Lists.reverse(lines)) {
			setStatistic(i++, line.getText());
		}
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
	
	private void applyEffects(boolean fastTick) {
		ItemStack heldItem = player.getInventory().getItemInMainHand();
		for (Item item : clazz.getCurrentItems().keySet()) {
			if (item == null) {
				continue;
			}
			if (heldItem != null && item.getItem().isSimilar(heldItem)) {
				for (Effect effect : item.getInHandEffects()) {
					applyEffect(effect, fastTick);
				}
			}
			for (Effect effect : item.getPassiveEffects()) {
				applyEffect(effect, fastTick);
			}
		}
		for (Effect effect : activeEffects) {
			applyEffect(effect, fastTick);
		}
	}
	
	private void applyEffect(Effect effect, boolean fastTick) {
		if (effect != null && effect.fast() == fastTick) {
			effect.apply(this);
		}
	}

}
