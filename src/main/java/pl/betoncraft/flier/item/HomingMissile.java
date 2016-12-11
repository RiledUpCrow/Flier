/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.item;

import java.util.Date;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import pl.betoncraft.flier.Flier;
import pl.betoncraft.flier.api.Damager;
import pl.betoncraft.flier.core.PlayerData;

/**
 * A homing missile which targets flying players.
 *
 * @author Jakub Sapalski
 */
public class HomingMissile extends DefaultWeapon {
	
	private EntityType entity = EntityType.ARROW;
	private int range = 64;
	private double speed = 3;
	private int life = 400;
	private double maneuverability = 1;
	private int radius;
	private int radiusSqr;

	public HomingMissile(ConfigurationSection section) {
		super(section);
		entity = EntityType.valueOf(section.getString("entity", entity.toString()).toUpperCase().replace(' ', '_'));
		range = section.getInt("range", range);
		speed = section.getDouble("speed", speed);
		life = section.getInt("life", life);
		maneuverability = section.getDouble("maneuverability", maneuverability);
		radius = range / 2;
		radiusSqr = radius * radius;
	}

	@Override
	public boolean use(PlayerData data) {
		Player player = data.getPlayer();
		UUID id = player.getUniqueId();
		if (weaponCooldown.containsKey(id)) {
			return false;
		}
		weaponCooldown.put(id, new Date().getTime() + 50*cooldown);
		Vector velocity = player.getLocation().getDirection().clone().multiply(speed);
		Vector pointer = player.getLocation().getDirection().clone().multiply(player.getVelocity().length() * 3);
		Location launch = (player.isGliding() ? player.getLocation() : player.getEyeLocation())
				.clone().add(pointer);
		Projectile missile = (Projectile) launch.getWorld().spawnEntity(launch, entity);
		missile.setVelocity(velocity);
		missile.setShooter(player);
		missile.setGravity(false);
		Damager.saveDamager(missile, HomingMissile.this);
		new BukkitRunnable() {
			Location lastLoc;;
			@Override
			public void run() {
				if (missile.isDead() || !missile.isValid() || missile.getTicksLived() >= life
						|| (lastLoc != null && missile.getLocation().distanceSquared(lastLoc) < 1)) {
					cancel();
					missile.remove();
				}
				lastLoc = missile.getLocation();
				Vector direction = missile.getVelocity().normalize();
				Location searchCenter = missile.getLocation().clone().add(direction.multiply(radius));
				Player nearest = null;
				double distance = radiusSqr;
				for (PlayerData p : data.getGame().getPlayers().values()) {
					double d = p.getPlayer().getLocation().distanceSquared(searchCenter);
					if (d < distance) {
						nearest = p.getPlayer();
						distance = d;
					}
				}
				if (nearest != null) {
					Vector aim = nearest.getLocation().subtract(missile.getLocation()).toVector().normalize()
							.multiply(maneuverability);
					missile.setVelocity(missile.getVelocity().add(aim).normalize().multiply(speed));
					nearest.playSound(nearest.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
				} else {
					missile.setVelocity(missile.getVelocity().normalize().multiply(speed));
				}
			}
		}.runTaskTimer(Flier.getInstance(), 1, 1);
		return true;
	}

}
