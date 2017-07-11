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
package pl.betoncraft.flier.bonus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Action;
import pl.betoncraft.flier.api.content.Bonus;
import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.UsableItem;
import pl.betoncraft.flier.event.FlierCollectBonusEvent;
import pl.betoncraft.flier.util.LangManager;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * A default Bonus implementation.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultBonus implements Bonus {
	
	protected final ValueLoader loader;
	protected final String id;
	protected final String name;
	protected final Optional<InGamePlayer> creator;
	protected final Optional<UsableItem> item;
	
	protected final boolean consumable;
	protected final int cooldown;
	protected final int respawn;
	protected final List<Action> actions = new ArrayList<>();

	protected boolean available = false;
	protected Map<UUID, Integer> cooldowns = new HashMap<>();
	protected BukkitRunnable starter;
	protected Game game;
	protected int ticks = 0;
	protected BukkitRunnable ticker;
	
	public DefaultBonus(ConfigurationSection section, Game game, Optional<InGamePlayer> creator,
			Optional<UsableItem> item) throws LoadingException {
		id = section.getName();
		this.game = game;
		this.creator = creator;
		this.item = item;
		loader = new ValueLoader(section);
		name = loader.loadString("name", id);
		consumable = loader.loadBoolean("consumable");
		cooldown = loader.loadNonNegativeInt("cooldown");
		respawn = loader.loadNonNegativeInt("respawn", 0);
		Flier flier = Flier.getInstance();
		for (String id : section.getStringList("actions")) {
			Action action = flier.getAction(id);
			if (action.needsItem() && !item.isPresent()) {
				throw new LoadingException(
						String.format("Action '%s' needs to be fired from an item, thus can't be used in a bonus.", id));
			}
			if (action.needsSource() && !creator.isPresent()) {
				throw new LoadingException(
						String.format("Action '%s' needs to be fired by a player, thus can't be used in a bonus.", id));
			}
			actions.add(action);
		}
	}
	
	@Override
	public String getID() {
		return id;
	}
	
	@Override
	public String getName(CommandSender player) {
		return name.startsWith("$") ? LangManager.getMessage(player, name.substring(1)) : name;
	}

	@Override
	public boolean isConsumable() {
		return consumable;
	}
	
	@Override
	public boolean isAvailable() {
		return available;
	}
	
	@Override
	public int getCooldown() {
		return cooldown;
	}

	@Override
	public int getRespawn() {
		return respawn;
	}

	@Override
	public List<Action> getActions() {
		return actions;
	}
	
	@Override
	public void apply(InGamePlayer player) {
		if (!available) {
			return;
		}
		UUID uuid = player.getPlayer().getUniqueId();
		if (cooldown > 0) {
			Integer cd = cooldowns.get(uuid);
			if (cd != null) {
				if (ticks < cd) {
					return;
				}
			}
		}
		FlierCollectBonusEvent event = new FlierCollectBonusEvent(player, this);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return;
		}
		if (use(player)) {
			if (cooldown > 0) {
				cooldowns.put(uuid, ticks + cooldown);
			}
			if (consumable) {
				block();
				starter = new BukkitRunnable() {
					@Override
					public void run() {
						release();
					}
				};
				starter.runTaskLater(Flier.getInstance(), respawn);
			}
		}
	}
	
	/**
	 * Called upon Bonus creation and after {@link #block()} when the respawn
	 * time has passed.
	 */
	protected void release() {
		available = true;
		starter = null;
	}
	
	/**
	 * Called when the Bonus is taken by the player or upon Bonus removal.
	 */
	protected void block() {
		available = false;
	}

	@Override
	public void start() {
		available = true;
		ticker = new BukkitRunnable() {
			@Override
			public void run() {
				ticks++;
			}
		};
		ticker.runTaskTimer(Flier.getInstance(), 0, 1);
		release();
	}
	
	@Override
	public void stop() {
		if (starter != null) {
			starter.cancel();
			starter = null;
		}
		if (ticker != null) {
			ticker.cancel();
			ticker = null;
		}
		block();
	}
	
	private boolean use(InGamePlayer player) {
		boolean used = false;
		for (Action action : actions) {
			if (action.act(creator, creator, player, item)) {
				used = true;
			}
		}
		return used;
	}

}
