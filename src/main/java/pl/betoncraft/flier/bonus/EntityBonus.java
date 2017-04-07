/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.bonus;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.core.defaults.DefaultBonus;

/**
 * An entity based Bonus type.
 *
 * @author Jakub Sapalski
 */
public class EntityBonus extends DefaultBonus implements Listener {
	
	private EntityType type;
	private Entity entity;
	private Location location;
	private final double distance;
	private final String locationName;
	private BukkitRunnable rotator;
	
	public EntityBonus(ConfigurationSection section) throws LoadingException {
		super(section);
		type = loader.loadEnum("entity", EntityType.class);
		distance = Math.pow(loader.loadPositiveDouble("distance"), 2);
		locationName = loader.loadString("location");
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onMove(PlayerMoveEvent event) {
		InGamePlayer player = game.getPlayers().get(event.getPlayer().getUniqueId());
		if (player != null && player.isPlaying() && event.getTo().distanceSquared(location) <= distance) {
			apply(player);
		}
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
	public void setGame(Game game) throws LoadingException {
		super.setGame(game);
		location = game.getArena().getLocation(locationName);
	}
	
	@Override
	public void release() {
		super.release();
		Bukkit.getPluginManager().registerEvents(this, Flier.getInstance());
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
	}
	
	@Override
	public void block() {
		super.block();
		if (entity != null) {
			entity.remove();
			entity = null;
		}
		if (rotator != null) {
			rotator.cancel();
			rotator = null;
		}
		HandlerList.unregisterAll(this);
	}

}
