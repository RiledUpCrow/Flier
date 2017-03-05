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

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Action;
import pl.betoncraft.flier.api.content.Bonus;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * Default implementation of a Bonus.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultBonus implements Bonus {
	
	protected final ValueLoader loader;
	
	protected final double distance;
	protected final boolean consumable;
	protected final int cooldown;
	protected final int respawn;
	protected final Location location;
	protected final List<Action> actions = new ArrayList<>();

	protected boolean available = false;
	protected Map<UUID, Long> cooldowns = new HashMap<>();
	protected BukkitRunnable starter;
	
	public DefaultBonus(ConfigurationSection section) throws LoadingException {
		loader = new ValueLoader(section);
		distance = loader.loadPositiveDouble("distance");
		consumable = loader.loadBoolean("consumable");
		cooldown = loader.loadNonNegativeInt("cooldown");
		respawn = loader.loadNonNegativeInt("respawn");
		location = loader.loadLocation("location");
		for (String id : section.getStringList("actions")) {
			actions.add(Flier.getInstance().getAction(id));
		}
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public double getDistance() {
		return distance;
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
			Long cd = cooldowns.get(uuid);
			if (cd != null) {
				long time = System.currentTimeMillis();
				if (time < cd) {
					return;
				}
			}
		}
		if (use(player)) {
			if (cooldown > 0) {
				cooldowns.put(uuid, System.currentTimeMillis() + (cooldown * 50));
			}
			if (consumable) {
				stop();
				starter = new BukkitRunnable() {
					@Override
					public void run() {
						start();
					}
				};
				starter.runTaskLater(Flier.getInstance(), respawn);
			}
		}
	}
	
	@Override
	public void start() {
		stop();
		available = true;
	}
	
	@Override
	public void stop() {
		available = false;
		if (starter != null) {
			starter.cancel();
			starter = null;
		}
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
