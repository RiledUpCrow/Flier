/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core.defaults;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Action;
import pl.betoncraft.flier.api.content.Bonus;
import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.event.FlierCollectBonusEvent;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * A default Bonus implementation.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultBonus implements Bonus {
	
	protected final ValueLoader loader;
	protected final String id;
	
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
	
	public DefaultBonus(ConfigurationSection section) throws LoadingException {
		id = section.getName();
		loader = new ValueLoader(section);
		consumable = loader.loadBoolean("consumable");
		cooldown = loader.loadNonNegativeInt("cooldown");
		respawn = loader.loadNonNegativeInt("respawn", 0);
		Flier flier = Flier.getInstance();
		for (String id : section.getStringList("actions")) {
			actions.add(flier.getAction(id));
		}
	}
	
	@Override
	public String getID() {
		return id;
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
	public void setGame(Game game) throws LoadingException {
		this.game = game;
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
		ticker.cancel();
		block();
	}
	
	private boolean use(InGamePlayer player) {
		boolean used = false;
		for (Action action : actions) {
			if (action.act(player)) {
				used = true;
			}
		}
		return used;
	}

}
