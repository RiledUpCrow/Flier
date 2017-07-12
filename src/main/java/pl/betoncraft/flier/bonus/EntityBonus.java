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
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.Owner;

/**
 * An entity based Bonus type.
 *
 * @author Jakub Sapalski
 */
public class EntityBonus extends ProximityBonus implements Listener {
	
	protected EntityType type;
	protected Entity entity;
	protected BukkitRunnable rotator;
	
	public EntityBonus(ConfigurationSection section, Game game, Optional<Owner> owner) throws LoadingException {
		super(section, game, owner);
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
