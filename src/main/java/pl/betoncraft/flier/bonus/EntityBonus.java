/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.bonus;

import java.util.Arrays;
import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.UsableItem;

/**
 * An entity based Bonus type.
 *
 * @author Jakub Sapalski
 */
public class EntityBonus extends ProximityBonus implements Listener {
	
	protected EntityType type;
	protected Entity entity;
	protected BukkitRunnable rotator;
	
	public EntityBonus(ConfigurationSection section, Game game, Optional<InGamePlayer> creator,
			Optional<UsableItem> item) throws LoadingException {
		super(section, game, creator, item);
		type = loader.loadEnum("entity", EntityType.class);
	}

	@EventHandler
	public void onChunkUnload(ChunkUnloadEvent event) {
		if (entity != null && entity.getLocation().getChunk().equals(event.getChunk())) {
			entity.remove();
		}
	}
	
	private void update() {
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
	public void release() {
		super.release();
		rotator = new BukkitRunnable() {
			@Override
			public void run() {
				update();
			}
		};
		rotator.runTaskTimer(Flier.getInstance(), 1, 1);
		entity = location.getWorld().spawnEntity(location, type);
		entity.setGravity(false);
		entity.setInvulnerable(true);
		entity.setSilent(true);
		entity.setGlowing(true);
		Bukkit.getPluginManager().registerEvents(this, Flier.getInstance());
	}
	
	@Override
	public void block() {
		super.block();
		HandlerList.unregisterAll(this);
		if (entity != null) {
			entity.remove();
			entity = null;
		}
		if (rotator != null) {
			rotator.cancel();
			rotator = null;
		}
	}

}
