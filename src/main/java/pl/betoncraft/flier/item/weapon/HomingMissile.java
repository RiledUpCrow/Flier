/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.item.weapon;

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
import pl.betoncraft.flier.api.Game.Attitude;
import pl.betoncraft.flier.core.PlayerData;
import pl.betoncraft.flier.core.Utils.ImmutableVector;

/**
 * A homing missile which targets flying players.
 *
 * @author Jakub Sapalski
 */
public class HomingMissile extends DefaultWeapon {
	
	private EntityType entity = EntityType.ARROW;
	private int searchRange = 64;
	private double searchRadius = 0.2;
	private double speed = 3;
	private int lifetime = 400;
	private double maneuverability = 1;
	private int radius;
	private int radiusSqr;

	public HomingMissile(ConfigurationSection section) {
		super(section);
		entity = EntityType.valueOf(section.getString("entity", entity.toString()).toUpperCase().replace(' ', '_'));
		searchRange = section.getInt("search_range", searchRange);
		searchRadius = section.getDouble("search_radius", searchRadius);
		speed = section.getDouble("speed", speed);
		lifetime = section.getInt("lifetime", lifetime);
		maneuverability = section.getDouble("maneuverability", maneuverability);
		radius = searchRange / 2;
		radiusSqr = radius * radius;
	}

	@Override
	public boolean use(PlayerData data) {
		Player player = data.getPlayer();
		Vector velocity = player.getLocation().getDirection().clone().multiply(speed);
		Vector pointer = player.getLocation().getDirection().clone().multiply(player.getVelocity().length() * 3);
		Location launch = player.getEyeLocation().clone().add(pointer);
		Projectile missile = (Projectile) launch.getWorld().spawnEntity(launch, entity);
		missile.setVelocity(velocity);
		missile.setShooter(player);
		missile.setGravity(false);
		missile.setGlowing(true);
		Damager.saveDamager(missile, HomingMissile.this);
		new BukkitRunnable() {
			int i = 0;
			Location lastLoc;
			Player nearest;
			boolean foundTarget = false;
			ImmutableVector vec = null;
			@Override
			public void run() {
				// stop if the missile does not exist
				if (missile.isDead() || !missile.isValid() || missile.getTicksLived() >= lifetime) {
					cancel();
					missile.remove();
					return;
				}
				// stop if the missile did not move for 5 ticks
				if (lastLoc != null && missile.getLocation().distanceSquared(lastLoc) == 0) {
					i++;
					if (i > 5) {
						cancel();
						missile.remove();
						return;
					}
				} else {
					i = 0;
				}
				lastLoc = missile.getLocation();
				// get missile velocity and store it
				// velocity is held here to avoid corruption
				if (vec == null) {
					vec = ImmutableVector.fromVector(missile.getVelocity()).normalize().multiply(speed);
				}
				// get the search area
				ImmutableVector direction = vec.normalize();
				Location searchCenter = missile.getLocation().clone().add(direction.multiply(radius).toVector());
				// find a target in the area
				Player target = null;
				double distance = radiusSqr;
				for (PlayerData p : data.getGame().getPlayers().values()) {
					// skip the player if he shouldn't be targeted
					Attitude attitude = data.getGame().getAttitude(p, data);
					if (attitude == Attitude.NEUTRAL) {
						continue;
					}
					if (!friendlyFire() && attitude == Attitude.FRIENDLY) {
						continue;
					}
					if (!suicidal() && data.equals(p)) {
						continue;
					}
					// get the nearest player
					double d = p.getPlayer().getLocation().distanceSquared(searchCenter);
					if (d < distance) {
						target = p.getPlayer();
						distance = d;
						// if the missile tracked someone previously and he's still in the area,
						// it should track him even if he's not the closest one
						if (nearest != null && p.getPlayer().equals(nearest)) {
							break;
						}
					}
				}
				nearest = target;
				ImmutableVector newVec;
				if (nearest != null) {
					// target found, fly towards it
					foundTarget = true;
					// target between feet and eye location
					Location loc = nearest.getLocation().toVector().midpoint(nearest.getEyeLocation().toVector()).toLocation(nearest.getWorld());
					Vector v = loc.subtract(missile.getLocation()).toVector().add(nearest.getVelocity());
					ImmutableVector aim = ImmutableVector.fromVector(v).normalize().multiply(maneuverability);
					newVec = direction.add(aim).normalize().multiply(speed);
					int j = (int) (4.0 * nearest.getLocation().distance(missile.getLocation()) / searchRange);
					j = j <= 0 ? 1 : j;
					if (missile.getTicksLived() % j == 0) {
						Vector soundLoc = missile.getLocation().subtract(nearest.getLocation()).toVector().normalize().multiply(10);
						nearest.playSound(nearest.getLocation().add(soundLoc), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
					}
				} else if (foundTarget) {
					// target was lost, fly in circles
					ImmutableVector d = new ImmutableVector(direction.getZ(), -direction.getY(), -direction.getX()).multiply(searchRadius);
					newVec = direction.add(d).normalize().multiply(speed);
				} else {
					// no target yet, fly straight
					newVec = direction.multiply(speed);
				}
				// store new velocity to avoid corruption
				vec = newVec;
				missile.setVelocity(newVec.toVector());
			}
		}.runTaskTimer(Flier.getInstance(), 1, 1);
		return true;
	}

}
