/**
 * Copyright (c) 2017 Jakub Sapalski
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
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
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.util.Vector;

import com.google.common.collect.Lists;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Action;
import pl.betoncraft.flier.api.content.Activator;
import pl.betoncraft.flier.api.content.Engine;
import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.api.content.Game.Attitude;
import pl.betoncraft.flier.api.content.Wings;
import pl.betoncraft.flier.api.core.Attacker;
import pl.betoncraft.flier.api.core.Damager;
import pl.betoncraft.flier.api.core.FancyStuffWrapper;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.Kit;
import pl.betoncraft.flier.api.core.SidebarLine;
import pl.betoncraft.flier.api.core.UsableItem;
import pl.betoncraft.flier.api.core.Usage;
import pl.betoncraft.flier.event.FlierEngineUseEvent;
import pl.betoncraft.flier.event.FlierPlayerHitEvent;
import pl.betoncraft.flier.util.LangManager;
import pl.betoncraft.flier.util.Utils;

/**
 * Stores data about the player.
 *
 * @author Jakub Sapalski
 */
public class DefaultPlayer implements InGamePlayer {
	
	private Player player;
	private Game game;
	private Kit kit;
	private String lang;
	private Scoreboard oldSb;
	private Scoreboard sb;
	private FancyStuffWrapper fancyStuff;
	private BukkitRunnable ticker;
	private int tickCounter = 0;

	private boolean isPlaying;
	private List<String> triggers = new ArrayList<>();
	private int noDamageTicks = 0;
	private List<SidebarLine> lines = new LinkedList<>();
	private Attacker lastHit = null;
	private ChatColor color = ChatColor.WHITE;
	private int money;
	
	public DefaultPlayer(Player player, Game game, Kit kit) {
		Flier flier = Flier.getInstance();
		this.player = player;
		this.game = game;
		this.kit = kit.replicate(this);
		lang = LangManager.getLanguage(player);
		oldSb = player.getScoreboard();
		sb = Bukkit.getScoreboardManager().getNewScoreboard();
		fancyStuff = flier.getFancyStuff();
		Objective stats = sb.registerNewObjective("stats", "dummy");
		stats.setDisplaySlot(DisplaySlot.SIDEBAR);
		stats.setDisplayName("Stats");
		Utils.clearPlayer(player);
		updateKit();
		ticker = new BukkitRunnable() {
			@Override
			public void run() {
				fastTick();
				if (tickCounter % 4 == 0) {
					slowTick();
				}
				tickCounter++;
			}
		};
		ticker.runTaskTimer(flier, 1, 1);
	}

	public void fastTick() {
		if (isPlaying()) {
			boolean hasWings = hasWings();
			boolean wingsDead = kit.getWings().getHealth() == 0;
			boolean wingsDisabled = kit.getWings().areDisabled();

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
			
			// manage UsableItems
			use();
			triggers.clear();
			noDamageTicks--;
		}
		displayReloadingTime();
	}

	public void slowTick() {
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
			for (UsableItem i : kit.getItems()) {
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
						LangManager.getMessage(this, "actionbar_ammo", color + full + ChatColor.BLACK + empty));
			} else {
				fancyStuff.sendActionBar(player, "");
			}
		}
	}

	@Override
	public String getName() {
		return player.getName();
	}
	
	@Override
	public void addTrigger(String name) {
		if (isPlaying()) {
			triggers.add(name);
		}
	}
	
	@Override
	public List<String> getTriggers() {
		return triggers;
	}
	
	@Override
	public boolean handleHit(Attacker attacker) {
		Damager damager = attacker.getDamager();
		InGamePlayer source = attacker.getSource();
		InGamePlayer creator = attacker.getCreator();
		UsableItem weapon = attacker.getWeapon();
		// if player can't be damaged yet, return
		if (noDamageTicks > 0) {
			return false;
		}
		// if player is friendly but damager deals no friendly fire, return
		if (!damager.causesFriendlyFire() && getGame().getAttitude(this, creator) == Attitude.FRIENDLY) {
			return false;
		}
		// if creator was this player but damager is not suicidal, return
		if (!damager.isSuicidal() && this.equals(creator)) {
			return false;
		}
		// fire an event
		FlierPlayerHitEvent hitEvent = new FlierPlayerHitEvent(this, attacker);
		Bukkit.getPluginManager().callEvent(hitEvent);
		// stop if the event was canceled
		if (hitEvent.isCancelled()) {
			return false;
		}
		// if this is an intermediate hit then skip handling this hit
		if (damager.isFinalHit()) {
			lastHit = attacker;
			noDamageTicks = damager.getNoDamageTicks();
			// display a message about the hit and play the sound to the creator
			// if he exists and if he hits someone else
			if (creator != null && !creator.equals(this)) {
				if (weapon == null) {
					LangManager.sendMessage(creator, "hit", Utils.formatPlayer(this, creator));
				} else {
					LangManager.sendMessage(creator, "hit_weapon", Utils.formatPlayer(this, creator),
							Utils.formatItem(weapon, creator));
				}
			}
			if (!this.equals(creator)) {
				if (weapon == null) {
					LangManager.sendMessage(this, "get_hit", Utils.formatPlayer(creator, this));
				} else {
					LangManager.sendMessage(this, "get_hit_weapon", Utils.formatPlayer(creator, this),
							Utils.formatItem(weapon, this));
				}
			}
		}
		loop: for (Usage usage : damager.getSubUsages()) {
			if (!usage.canUse(this)) {
				continue;
			}
			for (Activator activator : usage.getActivators()) {
				if (!activator.isActive(this, source == null ? this : source)) {
					continue loop;
				}
			}
			for (Action action : usage.getActions()) {
				action.act(this, source == null ? this : source);
			}
		}
		return true;
	}

	@Override
	public int getNoDamageTicks() {
		return noDamageTicks;
	}
	
	@Override
	public void setNoDamageTicks(int noDamageTicks) {
		this.noDamageTicks = noDamageTicks;
	}
	
	@Override
	public UsableItem getHeldItem() {
		if (player.getInventory().getItemInMainHand() == null ||
				player.getInventory().getItemInMainHand().getType() == Material.AIR) {
			return null;
		}
		int heldSlot = player.getInventory().getHeldItemSlot();
		for (UsableItem item : kit.getItems()) {
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
		UsableItem item = kit.getItems().stream()
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
		kit.removeItem(item);
	}
	
	@Override
	public void takeWingsOff() {
		ItemStack elytra = player.getInventory().getChestplate();
		if (elytra != null) {
			player.getInventory().setChestplate(null);
			player.getInventory().setItem(1, elytra);
		}
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
	public Location getLocation() {
		return player.getLocation().toVector()
			.midpoint(player.getEyeLocation().toVector())
			.toLocation(player.getLocation().getWorld());
	}
	
	@Override
	public Vector getVelocity() {
		return player.getVelocity();
	}
	
	@Override
	public double getWeight() {
		double weight = 0;
		weight += kit.getEngine().getWeight();
		weight += kit.getWings().getWeight();
		for (UsableItem item : kit.getItems()) {
			weight += item.getWeight();
		}
		return weight;
	}

	@Override
	public String getLanguage() {
		return lang;
	}

	@Override
	public boolean isTargetable() {
		return isPlaying();
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
	public Kit getKit() {
		return kit;
	}
	
	@Override
	public void updateKit() {
		Engine engine = kit.getEngine();
		Wings wings = kit.getWings();
		List<UsableItem> items = kit.getItems();
		player.getInventory().clear();
		player.getInventory().setItemInOffHand(engine.getItem(this));
		player.getInventory().setChestplate(wings.getItem(this));
		for (UsableItem item : items) {
			int amount = item.getAmount();
			int slot = item.slot();
			ItemStack itemStack = item.getItem(this);
			itemStack.setAmount(amount);
			if (slot >= 0) {
				player.getInventory().setItem(slot, itemStack);
			} else {
				ItemStack[] content = player.getInventory().getContents();
				int i = 9;
				while (i < 36) {
					if (content[i] == null) {
						content[i] = itemStack;
						break;
					}
				}
				player.getInventory().setContents(content);
			}
		}
	}
	
	@Override
	public Attacker getAttacker() {
		return lastHit;
	}

	@Override
	public void setAttacker(Attacker attacker) {
		lastHit = attacker;
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
	public void clearPlayer() {
		ticker.cancel();
		Utils.clearPlayer(player);
		player.setScoreboard(oldSb);
	}
	
	private void speedUp() {
		Engine engine = kit.getEngine();
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
//		startGlowing(engine.getGlowTime());
	}
	
	private void regenerateFuel() {
		Engine engine = kit.getEngine();
		if (engine == null) {
			return;
		}
		engine.addFuel(engine.getRegeneration());
	}
	
	private void regenerateWings() {
		Wings wings = kit.getWings();
		if (wings == null) {
			return;
		}
		wings.addHealth(wings.getRegeneration());
	}
	
	private void destroyWings() {
		player.getInventory().setChestplate(null);
		player.getInventory().setItem(1, null);
	}
	
	private boolean hasWings() {
		ItemStack wings = kit.getWings().getItem(this);
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
		for (UsableItem i : kit.getItems()) {
			if (i.slot() == slot) {
				item = i;
				break;
			}
		}
		if (item == null || item.getAmount() == 0) {
			player.setExp(0.9999f);
			return;
		}
		int ticks = item.getCooldown();
		int max = item.getWholeCooldown();
		float amount;
		if (ticks == 0 || max == 0) {
			amount = 0.9999f;
		} else {
			amount = (float) (max - ticks) / (float) max;
		}
		player.setExp(amount);
		
	}

	private void createWings() {
		player.getInventory().setItem(1, kit.getWings().getItem(this));
	}

	private void enableWings() {
		kit.getWings().setDisabled(false);
	}

	private void modifyFlight() {
		Vector velocity = kit.getWings().applyFlightModifications(this);
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
		List<UsableItem> copy = new ArrayList<>(kit.getItems());
		for (UsableItem item : copy) {
			if (item.use() && item.getAmmo() == 0 && item.isConsumable()) {
				consumeItem(item);
			}
		}
	}

}
