/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.action.attack;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Game.Attitude;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.util.ImmutableVector;
import pl.betoncraft.flier.util.Utils;

/**
 * A homing missile which targets flying players.
 *
 * @author Jakub Sapalski
 */
public class HomingMissile extends DefaultAttack {
	
	private static final String MANEUVERABILITY = "maneuverability";
	private static final String LIFETIME = "lifetime";
	private static final String SPEED = "speed";
	private static final String SEARCH_RADIUS = "search_radius";
	private static final String SEARCH_RANGE = "search_range";
	private static final String ENTITY = "entity";

	private final EntityType entity;
	private final int searchRange;
	private final double searchRadius;
	private final double speed;
	private final int lifetime;
	private final double maneuverability;

	public HomingMissile(ConfigurationSection section) throws LoadingException {
		super(section);
		entity = loader.loadEnum(ENTITY, EntityType.class);
		searchRange = loader.loadPositiveInt(SEARCH_RANGE);
		searchRadius = loader.loadPositiveDouble(SEARCH_RADIUS);
		speed = loader.loadPositiveDouble(SPEED);
		lifetime = loader.loadPositiveInt(LIFETIME);
		maneuverability = loader.loadPositiveDouble(MANEUVERABILITY);
	}

	@Override
	public boolean act(InGamePlayer data) {
		Player player = data.getPlayer();
		double speed = modMan.modifyNumber(SPEED, this.speed);
		Vector velocity = player.getLocation().getDirection().clone().multiply(speed);
		Vector pointer = player.getLocation().getDirection().clone().multiply(player.getVelocity().length() * 3);
		Location launch = player.getEyeLocation().clone().add(pointer);
		Projectile missile = (Projectile) launch.getWorld().spawnEntity(launch, modMan.modifyEnum(ENTITY, entity));
		missile.setVelocity(velocity);
		missile.setShooter(player);
		missile.setGravity(false);
		missile.setGlowing(true);
		Utils.saveDamager(missile, HomingMissile.this, data);
		new BukkitRunnable() {
			int i = 0;
			Location lastLoc;
			Player nearest;
			boolean foundTarget = false;
			ImmutableVector vec = null;
			int lifetime = (int) modMan.modifyNumber(LIFETIME, HomingMissile.this.lifetime);
			int searchRange = (int) modMan.modifyNumber(SEARCH_RANGE, HomingMissile.this.searchRange);
			double searchRadius = modMan.modifyNumber(SEARCH_RADIUS, HomingMissile.this.searchRadius);
			double maneuverability = modMan.modifyNumber(MANEUVERABILITY, HomingMissile.this.maneuverability);
			int radius = searchRange / 2;
			int radiusSqr = radius * radius;
			boolean friendlyFire = friendlyFire();
			boolean suicidal = suicidal();
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
				for (InGamePlayer p : data.getGame().getPlayers().values()) {
					// skip the player if he shouldn't be targeted
					Attitude attitude = data.getGame().getAttitude(p, data);
					if (attitude == Attitude.NEUTRAL) {
						continue;
					}
					if (!friendlyFire && attitude == Attitude.FRIENDLY) {
						continue;
					}
					if (!suicidal && data.equals(p)) {
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
