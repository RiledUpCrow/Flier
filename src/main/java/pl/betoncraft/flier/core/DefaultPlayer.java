/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import com.google.common.collect.Lists;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Engine;
import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.api.content.Wings;
import pl.betoncraft.flier.api.core.Damager;
import pl.betoncraft.flier.api.core.Damager.DamageResult;
import pl.betoncraft.flier.api.core.FancyStuffWrapper;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.PlayerClass;
import pl.betoncraft.flier.api.core.SidebarLine;
import pl.betoncraft.flier.api.core.UsableItem;
import pl.betoncraft.flier.api.core.Usage;
import pl.betoncraft.flier.api.core.Usage.Where;
import pl.betoncraft.flier.event.FlierEngineUseEvent;
import pl.betoncraft.flier.event.FlierPlayerHitEvent;
import pl.betoncraft.flier.util.LangManager;
import pl.betoncraft.flier.util.Position;
import pl.betoncraft.flier.util.Utils;

/**
 * Stores data about the player.
 *
 * @author Jakub Sapalski
 */
public class DefaultPlayer implements InGamePlayer {
	
	private Player player;
	private Game game;
	private PlayerClass clazz;
	private String lang;
	private Scoreboard oldSb;
	private Scoreboard sb;
	private FancyStuffWrapper fancyStuff;

	private boolean isPlaying;
	private boolean leftClicked = false;
	private boolean rightClicked = false;
	private int noDamageTicks = 0;
	private List<SidebarLine> lines = new LinkedList<>();
	private InGamePlayer lastHit = null;
	private ChatColor color = ChatColor.WHITE;
	private long glowTimer;
	private int money;
	private int tickCounter = 0;
	
	public DefaultPlayer(Player player, Game game, PlayerClass clazz) {
		this.player = player;
		this.game = game;
		this.clazz = clazz;
		lang = LangManager.getLanguage(player);
		oldSb = player.getScoreboard();
		sb = Bukkit.getScoreboardManager().getNewScoreboard();
		fancyStuff = Flier.getInstance().getFancyStuff();
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
			// if on ground glow for half a second every second
			if (tickCounter++ % 20 == 0 && Position.check(player, Where.GROUND)) {
				startGlowing(10);
			}
			displayReloadingTime();
			
			// manage UsableItems
			use();
			leftClicked = false;
			rightClicked = false;
			noDamageTicks--;
		}
	}

	@Override
	public void slowTick() {
		stopGlowing();
		updateStats();
		updateActionBar();
		if (!sb.equals(player.getScoreboard())) {
			player.setScoreboard(sb);
		}
	}
	
	private void updateActionBar() {
		if (fancyStuff.hasActionBarHandler()) {
			int slot = player.getInventory().getHeldItemSlot();
			UsableItem item = null;
			for (UsableItem i : clazz.getItems()) {
				if (i.slot() == slot) {
					item = i;
					break;
				}
			}
			if (item != null && item.getMaxAmmo() != 0) {
				int ammo = item.getAmmo();
				int maxAmmo = item.getMaxAmmo();
				String color;
				if (ammo == 0) {
					color = ChatColor.BLACK.toString();
				} else if (ammo > maxAmmo / 4.0 * 3.0) {
					color = ChatColor.GREEN.toString();
				} else if (ammo > maxAmmo / 4.0) {
					color = ChatColor.YELLOW.toString();
				} else {
					color = ChatColor.RED.toString();
				}
				String ammoChar = LangManager.getMessage(this, "ammo_char");
				String full = "";
				for (int i = 0; i < ammo; i++) {
					full += ammoChar;
				}
				String empty = "";
				for (int i = 0; i < maxAmmo - ammo; i++) {
					empty += ammoChar;
				}
				fancyStuff.sendActionBar(player,
						LangManager.getMessage(this, "actionbar_ammo", color, full, ChatColor.BLACK, empty));
			} else {
				fancyStuff.sendActionBar(player, "");
			}
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
	public List<DamageResult> damage(InGamePlayer shooter, Damager damager) {
		// empty list means no damage was dealt
		List<DamageResult> results = new ArrayList<>(3);
		// if player can't be damaged yet, return
		if (noDamageTicks > 0) {
			return results;
		}
		// player is not playing, nothing happens
		// ignore if you can's commit suicide with this weapon
		if (isPlaying() || !(shooter != null && shooter.equals(this) && !damager.suicidal())) {
			if (Position.check(getPlayer(), Usage.Where.NO_FALL)) {
				results.add(DamageResult.HIT);
				if (Position.check(getPlayer(), Usage.Where.AIR)) {
					if (damager.wingsOff()) {
						results.add(DamageResult.WINGS_OFF);
					}
					if (damager.midAirPhysicalDamage()) {
						results.add(DamageResult.REGULAR_DAMAGE);
					}
					results.add(DamageResult.WINGS_DAMAGE);
				} else if (Position.check(getPlayer(), Usage.Where.GROUND)) {
					results.add(DamageResult.REGULAR_DAMAGE);
				}
			}
		}
		// fire an event
		FlierPlayerHitEvent hitEvent = new FlierPlayerHitEvent(this, shooter, results, damager);
		Bukkit.getPluginManager().callEvent(hitEvent);
		if (hitEvent.isCancelled()) {
			results.clear();
		}
		return results;
	}
	
	@Override
	public void setNoDamageTicks(int noDamageTicks) {
		this.noDamageTicks = noDamageTicks;
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
	public UsableItem getHeldItem() {
		if (player.getInventory().getItemInMainHand() == null ||
				player.getInventory().getItemInMainHand().getType() == Material.AIR) {
			return null;
		}
		int heldSlot = player.getInventory().getHeldItemSlot();
		for (UsableItem item : clazz.getItems()) {
			if (item.slot() == heldSlot && isHolding(item)) {
				return item;
			}
		}
		return null;
	}
	
	@Override
	public boolean isHolding(UsableItem item) {
		ItemStack stack = player.getInventory().getItemInMainHand();
		return item == null && stack == null || (item != null && stack != null && item.getItem(this).isSimilar(stack));
	}
	
	@Override
	public boolean isAccelerating() {
		return player.isGliding() && player.isSneaking();
	}
	
	@Override
	public void consumeItem(UsableItem match) {
		UsableItem item = clazz.getItems().stream()
				.filter(i -> i.isSimilar(match))
				.findFirst()
				.orElse(null);
		if (item == null) {
			return;
		}
		int slot = item.slot();
		int amount = item.getAmount();
		ItemStack stack = player.getInventory().getItem(slot);
		ItemStack compare = item.getItem(this);
		// if the stack was not on the correct slot or there was another item, find the correct one
		if (stack == null || !stack.isSimilar(compare)) {
			ItemStack[] inv = player.getInventory().getContents();
			for (int i = 0; i < inv.length; i++) {
				if (inv[i] != null && compare.isSimilar(inv[i])) {
					stack = inv[i];
					slot = i; // remember the current slot, so we can remove it
					break;
				}
			}
		}
		// no such item, can't remove
		if (stack == null) {
			return;
		}
		amount--;
		if (amount <= 0) { // remove stack
			player.getInventory().setItem(slot, null); // here the remembered slot is used
		} else { // decrease stack
			stack.setAmount(amount);
			item.setAmmo(item.getMaxAmmo());
		}
		clazz.removeItem(item);
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
		weight += clazz.getEngine().getWeight();
		weight += clazz.getWings().getWeight();
		for (UsableItem item : clazz.getItems()) {
			weight += item.getWeight();
		}
		return weight;
	}

	@Override
	public String getLanguage() {
		return lang;
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
		getPlayer().getInventory().setItemInOffHand(engine.getItem(this));
		getPlayer().getInventory().setChestplate(wings.getItem(this));
		for (UsableItem item : items) {
			int amount = item.getAmount();
			int slot = item.slot();
			ItemStack itemStack = item.getItem(this);
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
		Utils.clearPlayer(player);
		player.setScoreboard(oldSb);
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
		ItemStack wings = clazz.getWings().getItem(this);
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
			String text = line.getText();
			if (text.length() > 16) {
				text = text.substring(0, 16);
			}
			setStatistic(i++, text);
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
	
	private void displayReloadingTime() {
		int slot = player.getInventory().getHeldItemSlot();
		UsableItem item = null;
		for (UsableItem i : clazz.getItems()) {
			if (i.slot() == slot) {
				item = i;
				break;
			}
		}
		if (item == null || !item.getUsages().stream().filter(usage -> usage.canUse(this)).findAny().isPresent()) {
			player.setExp(0);
			return;
		}
		int ticks = item.getCooldown();
		int max = item.getWholeCooldown();
		float amount;
		if (ticks == 0) {
			amount = (float) 0.9999; 
		} else if (max == 0) {
			amount = 0;
		} else {
			amount = (float) (max - ticks) / (float) max;
		}
		player.setExp(amount);
		
	}

	private void createWings() {
		player.getInventory().setItem(1, clazz.getWings().getItem(this));
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
		// iterate over copied list to avoid concurrent modifications
		List<UsableItem> copy = new ArrayList<>(clazz.getItems());
		for (UsableItem item : copy) {
			if (item.use(this) && item.getAmmo() == 0 && item.isConsumable()) {
				consumeItem(item);
			}
		}
	}

}
