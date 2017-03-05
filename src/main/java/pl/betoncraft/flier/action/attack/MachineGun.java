/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.action.attack;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Explosive;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.core.defaults.DefaultAttack;
import pl.betoncraft.flier.util.Utils;

/**
 * Burst shooting weapon with unguided projectiles.
 *
 * @author Jakub Sapalski
 */
public class MachineGun extends DefaultAttack {
	
	private final EntityType entity;
	private final int burstAmount;
	private final int burstTicks;
	private final double projectileSpeed;
	private final int range = 10 * 20;
	
	public MachineGun(ConfigurationSection section) throws LoadingException {
		super(section);
		entity = loader.loadEnum("entity", EntityType.class);
		burstAmount = loader.loadPositiveInt("burst_amount");
		burstTicks = loader.loadPositiveInt("burst_ticks");
		projectileSpeed = loader.loadPositiveDouble("projectile_speed");
	}
	
	@Override
	public boolean act(InGamePlayer data) {
		Player player = data.getPlayer();
		Map<Projectile, Vector> projectiles = new HashMap<>(burstAmount);
		new BukkitRunnable() {
			int counter = burstAmount;
			@Override
			public void run() {
				Vector velocity = player.getLocation().getDirection().clone().multiply(projectileSpeed);
				Vector pointer = player.getLocation().getDirection().clone().multiply(player.getVelocity().length() * 3);
				Location launch = (player.isGliding() ? player.getLocation() : player.getEyeLocation())
						.clone().add(pointer);
				Projectile projectile = (Projectile) launch.getWorld().spawnEntity(launch, entity);
				projectile.setVelocity(velocity);
				projectile.setShooter(player);
				projectile.setGravity(false);
				projectile.setBounce(false);
				if (projectile instanceof Explosive) {
					Explosive explosive = (Explosive) projectile;
					explosive.setIsIncendiary(false);
					explosive.setYield(0);
				}
				Utils.saveDamager(projectile, MachineGun.this, data);
				projectiles.put(projectile, velocity);
				counter --;
				if (counter <= 0) {
					cancel();
				}
			}
		}.runTaskTimer(Flier.getInstance(), 0, burstTicks);
		new BukkitRunnable() {
			int life = 0;
			@Override
			public void run() {
				// update projectile path to prevent them from flying around
				for (Entry<Projectile, Vector> entry : projectiles.entrySet()) {
					entry.getKey().setVelocity(entry.getValue());
				}
				// cancel after the range has passed
				if (++life >= range) {
					cancel();
				}
			}
		}.runTaskTimer(Flier.getInstance(), 0, 1);
		return true;
	}

}
