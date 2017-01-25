/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import pl.betoncraft.flier.Flier;
import pl.betoncraft.flier.api.Bonus;
import pl.betoncraft.flier.api.InGamePlayer;
import pl.betoncraft.flier.exception.LoadingException;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * A default Bonus implementation.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultBonus implements Bonus {
	
	protected final String id;
	
	protected final double distance;
	protected final boolean consumable;
	protected final int cooldown;
	protected final int respawn;
	protected final Location location;

	private EntityType type;
	private Entity entity;
	private boolean available;
	private Map<UUID, Long> cooldowns = new HashMap<>();
	
	public DefaultBonus(ConfigurationSection section) throws LoadingException {
		id = section.getName();
		distance = ValueLoader.loadPositiveDouble(section, "distance");
		consumable = ValueLoader.loadBoolean(section, "consumable");
		cooldown = ValueLoader.loadNonNegativeInt(section, "cooldown");
		respawn = ValueLoader.loadNonNegativeInt(section, "respawn");
		type = ValueLoader.loadEnum(section, "entity", EntityType.class);
		location = ValueLoader.loadLocation(section, "location");
	}
	
	/**
	 * Specified the behavior of this bonus.
	 */
	protected abstract boolean use(InGamePlayer player);
	
	@Override
	public void update() {
		if (entity == null) {
			return;
		}
		if (!entity.isValid()) {
			if (!location.getChunk().isLoaded()) {
				return;
			}
			Entity newEntity = Arrays.asList(location.getChunk().getEntities()).stream()
					.filter(e -> e.getUniqueId().equals(entity.getUniqueId()))
					.findFirst().orElse(null);
			if (newEntity == null) {
				return;
			}
			entity = newEntity;
		}
		float yaw = location.getYaw();
		location.setYaw((yaw + 10) % 360);
		entity.teleport(location);
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
				Bukkit.getScheduler().scheduleSyncDelayedTask(Flier.getInstance(), () -> start(), respawn);
			}
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
	public void start() {
		stop();
		available = true;
		entity = location.getWorld().spawnEntity(location, type);
		entity.setGravity(false);
		entity.setInvulnerable(true);
		entity.setSilent(true);
		entity.setGlowing(true);
	}
	
	@Override
	public void stop() {
		if (entity != null) {
			available = false;
			entity.remove();
			entity = null;
		}
	}
	
	@Override
	public Bonus replicate() {
		try {
			return Flier.getInstance().getBonus(id);
		} catch (LoadingException e) {
			return null; // dead code
		}
	}

}
