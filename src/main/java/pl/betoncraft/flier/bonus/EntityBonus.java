/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.bonus;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;

import pl.betoncraft.flier.Flier;
import pl.betoncraft.flier.core.defaults.DefaultBonus;
import pl.betoncraft.flier.exception.LoadingException;

/**
 * A default Bonus implementation.
 *
 * @author Jakub Sapalski
 */
public class EntityBonus extends DefaultBonus {
	
	private EntityType type;
	private Entity entity;
	
	public EntityBonus(ConfigurationSection section) throws LoadingException {
		super(section);
		type = loader.loadEnum("entity", EntityType.class);
		Bukkit.getPluginManager().registerEvents(new Listener() {
			@EventHandler
			public void onChunkUnload(ChunkUnloadEvent event) {
				if (entity != null && entity.getLocation().getChunk().equals(event.getChunk())) {
					entity.remove();
				}
			}
		}, Flier.getInstance());
	}
	
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

}
