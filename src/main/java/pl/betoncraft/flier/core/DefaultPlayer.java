/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

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

import pl.betoncraft.flier.api.content.Bonus;
import pl.betoncraft.flier.api.content.Engine;
import pl.betoncraft.flier.api.content.Lobby;
import pl.betoncraft.flier.api.content.Wings;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.PlayerClass;
import pl.betoncraft.flier.api.core.SidebarLine;
import pl.betoncraft.flier.api.core.UsableItem;
import pl.betoncraft.flier.event.FlierCollectBonusEvent;
import pl.betoncraft.flier.event.FlierEngineUseEvent;
import pl.betoncraft.flier.util.PlayerBackup;
import pl.betoncraft.flier.util.Utils;

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
	private Scoreboard oldSb;

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
		oldSb = player.getScoreboard();
		Objective stats = sb.registerNewObjective("stats", "dummy");
		stats.setDisplaySlot(DisplaySlot.SIDEBAR);
		stats.setDisplayName("Stats");
		Utils.clearPlayer(player);
		updateClass();
	}

	@Override
	public void fastTick() {
		if (isPlaying()) {
			boolean hasWings = hasWings();
			boolean wingsDead = clazz.getWings().getHealth() == 0;
			boolean wingsDisabled = clazz.getWings().areDisabled();

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
		if (!sb.equals(player.getScoreboard())) {
			player.setScoreboard(sb);
		}
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
		return item == null && stack == null || (item != null && stack != null && item.getItem().isSimilar(stack));
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
		weight += clazz.getEngine().getWeight();
		weight += clazz.getWings().getWeight();
		for (UsableItem item : clazz.getItems()) {
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
		Engine engine = clazz.getEngine();
		Wings wings = clazz.getWings();
		List<UsableItem> items = clazz.getItems();
		getPlayer().getInventory().clear();
		getPlayer().getInventory().setItemInOffHand(engine.getItem());
		getPlayer().getInventory().setChestplate(wings.getItem());
		for (UsableItem item : items) {
			int amount = item.getAmount();
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
		List<String> colors = Arrays.asList(ChatColor.values()).stream()
				.map(color -> color.name().toLowerCase()).collect(Collectors.toList());
		for (Team team : sb.getTeams()) {
			if (colors.contains(team.getName())) {
				team.unregister();
			}
		}
		for (Entry<String, ChatColor> e : map.entrySet()) {
			String player = e.getKey();
			ChatColor color = e.getValue();
			String colorName = color.name().toLowerCase();
			Team team = sb.getTeam(colorName);
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
		player.setScoreboard(oldSb);
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
		Engine engine = clazz.getEngine();
		if (engine == null) {
			return;
		}
		if (!engine.removeFuel(engine.getConsumption())) {
			return;
		}
		FlierEngineUseEvent event = new FlierEngineUseEvent(this);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return;
		}
		getPlayer().setVelocity(engine.launch(getPlayer().getVelocity(), getPlayer().getLocation().getDirection()));
		startGlowing(engine.getGlowTime());
	}
	
	private void regenerateFuel() {
		Engine engine = clazz.getEngine();
		if (engine == null) {
			return;
		}
		engine.addFuel(engine.getRegeneration());
	}
	
	private void regenerateWings() {
		Wings wings = clazz.getWings();
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
		ItemStack wings = clazz.getWings().getItem();
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
				FlierCollectBonusEvent event = new FlierCollectBonusEvent(this, bonus);
				Bukkit.getPluginManager().callEvent(event);
				if (!event.isCancelled()) {
					bonus.apply(this);
				}
			}
		}
	}

	private void createWings() {
		player.getInventory().setItem(1, clazz.getWings().getItem());
	}

	private void enableWings() {
		clazz.getWings().setDisabled(false);
	}

	private void modifyFlight() {
		Vector velocity = clazz.getWings().applyFlightModifications(this);
		if (Double.isNaN(velocity.length())) {
			velocity = new Vector();
		}
		player.setVelocity(velocity);
	}
	
	private void use() {
		if (!isPlaying()) {
			return;
		}
		// we can't remove class items while iterating over them, so this list will remember them
		// it.remove() won't work, removing class item is more complicated than that
		List<UsableItem> itemsToRemove = new ArrayList<>();
		for (Iterator<UsableItem> it = clazz.getItems().iterator(); it.hasNext();) {
			UsableItem item = it.next();
			int amount = item.getAmount();
			if (item.use(this) && item.getAmmo() == 0 && item.isConsumable()) {
				int slot = item.slot();
				ItemStack stack = player.getInventory().getItem(slot);
				// if the stack was not on the correct slot or there was another item, find the correct one
				if (stack == null || !stack.isSimilar(item.getItem())) {
					ItemStack[] inv = player.getInventory().getContents();
					for (int i = 0; i < inv.length; i++) {
						if (inv[i] != null && item.getItem().isSimilar(inv[i])) {
							stack = inv[i];
							slot = i; // remember the current slot, so we can remove it
							break;
						}
					}
				}
				amount--;
				if (amount <= 0) { // remove stack
					player.getInventory().setItem(slot, null); // here the remembered slot is used
				} else { // decrease stack
					stack.setAmount(amount);
					item.setAmmo(item.getMaxAmmo());
				}
				itemsToRemove.add(item); // remember the item to remove from the class
			}
		}
		// remove items from the class
		for (UsableItem item : itemsToRemove) {
			clazz.removeItem(item);
		}
	}

}
