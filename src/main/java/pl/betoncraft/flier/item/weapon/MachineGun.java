/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.item.weapon;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import pl.betoncraft.flier.Flier;
import pl.betoncraft.flier.api.Damager;
import pl.betoncraft.flier.api.InGamePlayer;
import pl.betoncraft.flier.core.ValueLoader;
import pl.betoncraft.flier.exception.LoadingException;

/**
 * Burst shooting weapon with unguided projectiles.
 *
 * @author Jakub Sapalski
 */
public class MachineGun extends DefaultWeapon {
	
	private final EntityType entity;
	private final int burstAmount;
	private final int burstTicks;
	private final double projectileSpeed;
	
	public MachineGun(ConfigurationSection section) throws LoadingException {
		super(section);
		entity = ValueLoader.loadEnum(section, "entity", EntityType.class);
		burstAmount = ValueLoader.loadPositiveInt(section, "burst_amount");
		burstTicks = ValueLoader.loadPositiveInt(section, "burst_ticks");
		projectileSpeed = ValueLoader.loadPositiveDouble(section, "projectile_speed");
	}
	
	@Override
	public boolean use(InGamePlayer data) {
		Player player = data.getPlayer();
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
				Damager.saveDamager(projectile, MachineGun.this);
				counter --;
				if (counter <= 0) {
					cancel();
				}
			}
		}.runTaskTimer(Flier.getInstance(), 0, burstTicks);
		return true;
	}
	
	@Override
	public MachineGun replicate() {
		try {
			return new MachineGun(base);
		} catch (LoadingException e) {
			return null; // dead code
		}
	}

}
