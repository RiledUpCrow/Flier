/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import com.google.common.collect.Lists;

import pl.betoncraft.flier.api.Bonus;
import pl.betoncraft.flier.api.Engine;
import pl.betoncraft.flier.api.InGamePlayer;
import pl.betoncraft.flier.api.Item;
import pl.betoncraft.flier.api.Lobby;
import pl.betoncraft.flier.api.PlayerClass;
import pl.betoncraft.flier.api.SidebarLine;
import pl.betoncraft.flier.api.UsableItem;
import pl.betoncraft.flier.api.Wings;
import pl.betoncraft.flier.util.PlayerBackup;
import pl.betoncraft.flier.util.Utils;
import pl.betoncraft.flier.util.Utils.ImmutableVector;

/**
 * Stores data about the player.
 *
 * @author Jakub Sapalski
 */
public class DefaultPlayer implements InGamePlayer {
	
	private Player player;
	private Lobby lobby;
	private PlayerClass clazz;
	private PlayerBackup backup;
	private Scoreboard sb;

	private boolean isPlaying;
	private boolean leftClicked = false;
	private boolean rightClicked = false;
	private List<SidebarLine> lines = new LinkedList<>();
	private InGamePlayer lastHit = null;
	private ChatColor color;
	private long glowTimer;
	private int money;
	
	public DefaultPlayer(Player player, Lobby lobby, PlayerClass clazz) {
		this.player = player;
		this.lobby = lobby;
		this.clazz = clazz;
		backup = new PlayerBackup(player);
		backup.save();
		sb = Bukkit.getScoreboardManager().getNewScoreboard();
		Objective stats = sb.registerNewObjective("stats", "dummy");
		stats.setDisplaySlot(DisplaySlot.SIDEBAR);
		stats.setDisplayName("Stats");
		player.setScoreboard(sb);
		Utils.clearPlayer(player);
		updateClass();
	}

	@Override
	public void fastTick() {
		if (isPlaying()) {
			boolean hasWings = hasWings();
			boolean wingsDead = clazz.getCurrentWings().getHealth() == 0;
			boolean wingsDisabled = clazz.getCurrentWings().areDisabled();

			if (hasWings) { // has wings
				if (wingsDead) { // wings should be dead, destroying
					destroyWings();
				} else { // wings are alive
					regenerateWings();
					if (wingsDisabled) { // wings are disabled, take off
						takeWingsOff();
						enableWings();
					} else { // wings are not disabled
						if (player.isGliding()) { // the player is flying
							modifyFlight();
							if (isAccelerating()) { // the player is accelerating
								speedUp();
							}
						}
					}
				}
			} else { // has no wings
				if (!wingsDead) { // wings should be alive, create them
					createWings();
				}
			}
			if (!isAccelerating()) { // is not accelerating
				regenerateFuel();
			}
			checkBonuses();
			
			// manage UsableItems
			use();
			leftClicked = false;
			rightClicked = false;
			
		}
	}

	@Override
	public void slowTick() {
		stopGlowing();
		updateStats();
	}
	
	@Override
	public void leftClick() {
		if (isPlaying()) {
			leftClicked = true;
		}
	}
	
	@Override
	public void rightClick() {
		if (isPlaying()) {
			rightClicked = true;
		}
	}
	
	@Override
	public boolean didLeftClick() {
		return leftClicked;
	}
	
	@Override
	public boolean didRightClick() {
		return rightClicked;
	}
	
	@Override
	public boolean isHolding(UsableItem item) {
		ItemStack stack = player.getInventory().getItemInMainHand();
		return item == null && stack == null || item != null && stack != null && item.getItem().isSimilar(stack);
	}

	@Override
	public Lobby getLobby() {
		return lobby;
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
		Map<UsableItem, Integer> items = clazz.getCurrentItems();
		getPlayer().getInventory().clear();
		getPlayer().getInventory().setItemInOffHand(engine.getItem());
		getPlayer().getInventory().setChestplate(wings.getItem());
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
	public void exitGame() {
		lobby.getGame().removePlayer(this);
		isPlaying = false;
		lines.clear();
		lastHit = null;
		color = null;
		glowTimer = 0;
		money = 0;
		clazz.reset();
		Utils.clearPlayer(player);
		updateClass();
		getPlayer().teleport(lobby.getSpawn());
	}
	
	@Override
	public void exitLobby() {
		exitGame();
		player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
		player.getInventory().clear();
		backup.load();
	}
	
	private boolean isAccelerating() {
		return player.isGliding() && player.isSneaking();
	}
	
	private void startGlowing(int ticks) {
		player.setGlowing(true);
		glowTimer = System.currentTimeMillis() + 50*ticks;
	}
	
	private void stopGlowing() {
		if (System.currentTimeMillis() >= glowTimer) {
			player.setGlowing(false);
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
	
	@Override
	public void takeWingsOff() {
		ItemStack elytra = player.getInventory().getChestplate();
		if (elytra != null) {
			player.getInventory().setChestplate(null);
			player.getInventory().setItem(1, elytra);
		}
	}
	
	private void destroyWings() {
		player.getInventory().setChestplate(null);
		player.getInventory().setItem(1, null);
	}
	
	private boolean hasWings() {
		ItemStack wings = clazz.getCurrentWings().getItem();
		ItemStack chestPlate = player.getInventory().getChestplate();
		if (chestPlate != null && chestPlate.isSimilar(wings)) {
			return true;
		}
		ItemStack slot = player.getInventory().getItem(1);
		if (slot != null && slot.isSimilar(wings)) {
			return true;
		}
		return false;
	}
	
	private void updateStats() {
		int i = 0;
		for (SidebarLine line : Lists.reverse(lines)) {
			setStatistic(i++, line.getText());
		}
		while (i < ChatColor.values().length) {
			setStatistic(i++, null);
		}
	}
	
	private void setStatistic(int index, String string) {
		if (index >= ChatColor.values().length) {
			return;
		}
		String name = ChatColor.values()[index].toString();
		if (string != null) {
			Score score = sb.getObjective(DisplaySlot.SIDEBAR).getScore(name);
			score.setScore(index);
			Team team = sb.getEntryTeam(name);
			if (team == null) {
				team = sb.registerNewTeam(name);
				team.addEntry(name);
			}
			team.setPrefix(string);
		} else {
			sb.resetScores(name);
		}
	}

	private void checkBonuses() {
		List<Bonus> bonuses = lobby.getGame().getBonuses();
		for (Bonus bonus : bonuses) {
			if (!bonus.isAvailable()) {
				continue;
			}
			double distSqrd = bonus.getDistance() * bonus.getDistance();
			if (bonus.getLocation().distanceSquared(player.getLocation()) <= distSqrd) {
				bonus.apply(this);
			}
		}
	}

	private void createWings() {
		player.getInventory().setItem(1, clazz.getCurrentWings().getItem());
	}

	private void enableWings() {
		clazz.getCurrentWings().setDisabled(false);
	}

	private void modifyFlight() {
		Vector velocity = clazz.getCurrentWings().applyFlightModifications(this).toVector();
		if (Double.isNaN(velocity.length())) {
			velocity = new Vector();
		}
		player.setVelocity(velocity);
	}
	
	private void use() {
		if (!isPlaying()) {
			return;
		}
		for (Iterator<Entry<UsableItem, Integer>> it = clazz.getCurrentItems().entrySet().iterator(); it.hasNext();) {
			Entry<UsableItem, Integer> entry = it.next();
			UsableItem item = entry.getKey();
			int amount = entry.getValue();
			if (item.use(this) && item.getAmmo() == 0 && item.isConsumable()) {
				amount--;
				ItemStack stack = getPlayer().getInventory().getItemInMainHand();
				if (amount <= 0) { // remove stack
					it.remove();
					getPlayer().getInventory().setItemInMainHand(null);
				} else { // decrease stack
					entry.setValue(amount);
					stack.setAmount(amount);
					item.setAmmo(item.getMaxAmmo());
				}
			}
		}
	}

}
