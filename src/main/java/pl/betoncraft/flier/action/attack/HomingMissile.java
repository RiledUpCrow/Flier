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
package pl.betoncraft.flier.action.attack;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Game.Attitude;
import pl.betoncraft.flier.api.core.Attacker;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.Owner;
import pl.betoncraft.flier.api.core.Target;
import pl.betoncraft.flier.core.DefaultAttacker;
import pl.betoncraft.flier.util.ImmutableVector;

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
	private static final String TARGET_FRIENDS = "target_friends";
	private static final String TARGET_SELF = "target_self";
	
	private static MissileListener listener;

	private final EntityType entity;
	private final int searchRange;
	private final double searchRadius;
	private final double speed;
	private final int lifetime;
	private final double maneuverability;
	private final boolean targetFriends;
	private final boolean targetSelf;

	public HomingMissile(ConfigurationSection section, Optional<Owner> owner) throws LoadingException {
		super(section, owner);
		entity = loader.loadEnum(ENTITY, EntityType.class);
		searchRange = loader.loadPositiveInt(SEARCH_RANGE);
		searchRadius = loader.loadPositiveDouble(SEARCH_RADIUS);
		speed = loader.loadPositiveDouble(SPEED);
		lifetime = loader.loadPositiveInt(LIFETIME);
		maneuverability = loader.loadPositiveDouble(MANEUVERABILITY);
		targetFriends = loader.loadBoolean(TARGET_FRIENDS, true);
		targetSelf = loader.loadBoolean(TARGET_SELF, true);
		// register a single listener for all homing missiles
		if (listener == null) {
			listener = new MissileListener();
			Bukkit.getPluginManager().registerEvents(listener, Flier.getInstance());
		}
	}

	@Override
	public boolean act(InGamePlayer target, InGamePlayer source) {
		Player player = target.getPlayer();
		double speed = modMan.modifyNumber(SPEED, this.speed);
		Vector velocity = player.getLocation().getDirection().clone().multiply(speed);
		Vector pointer = player.getLocation().getDirection().clone().multiply(player.getVelocity().length() * 3);
		Location launch = player.getEyeLocation().clone().add(pointer);
		Projectile missile = (Projectile) launch.getWorld().spawnEntity(launch, modMan.modifyEnum(ENTITY, entity));
		missile.setVelocity(velocity);
		missile.setShooter(player);
		try {
			missile.setGravity(false);
		} catch (NoSuchMethodError e) {}
		missile.setGlowing(true);
		Attacker.saveAttacker(missile, new DefaultAttacker(HomingMissile.this, owner.get().getPlayer(),
				target, owner.get().getItem()));
		new BukkitRunnable() {
			int i = 0;
			Location lastLoc;
			Target nearest;
			boolean foundTarget = false;
			ImmutableVector vec = null;
			int lifetime = (int) modMan.modifyNumber(LIFETIME, HomingMissile.this.lifetime);
			int searchRange = (int) modMan.modifyNumber(SEARCH_RANGE, HomingMissile.this.searchRange);
			double searchRadius = modMan.modifyNumber(SEARCH_RADIUS, HomingMissile.this.searchRadius);
			double maneuverability = modMan.modifyNumber(MANEUVERABILITY, HomingMissile.this.maneuverability);
			int radius = searchRange / 2;
			int radiusSqr = radius * radius;
			boolean friendlyFire = HomingMissile.this.targetFriends;
			boolean suicidal = HomingMissile.this.targetSelf;
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
				Target missileTarget = null;
				double distance = radiusSqr;
				for (Target t : target.getGame().getTargets().values()) {
					// skip the player if he shouldn't be targeted
					Attitude attitude = t.getGame().getAttitude(t, owner.get().getPlayer());
					if (attitude == Attitude.NEUTRAL) {
						continue;
					}
					if (!friendlyFire && attitude == Attitude.FRIENDLY) {
						continue;
					}
					if (!suicidal && owner.get().getPlayer().equals(t)) {
						continue;
					}
					// get the nearest player
					double d = t.getLocation().distanceSquared(searchCenter);
					if (d < distance) {
						missileTarget = t;
						distance = d;
						// TODO decoys will be trivial to implement if it always tracked
						// nearest target, so this should probably be removed...
						// 
						// if the missile tracked someone previously and he's still in the area,
						// it should track him even if he's not the closest one
						if (nearest != null && t.equals(nearest)) {
							break;
						}
					}
				}
				nearest = missileTarget;
				ImmutableVector newVec;
				if (nearest != null) {
					// target found, fly towards it
					foundTarget = true;
					Location loc = nearest.getLocation();
					Vector v = loc.subtract(missile.getLocation()).toVector().add(nearest.getVelocity());
					ImmutableVector aim = ImmutableVector.fromVector(v).normalize().multiply(maneuverability);
					newVec = direction.add(aim).normalize().multiply(speed);
					if (nearest instanceof InGamePlayer) {
						int j = (int) (4.0 * nearest.getLocation().distance(missile.getLocation()) / searchRange);
						j = j <= 0 ? 1 : j;
						if (missile.getTicksLived() % j == 0) {
							Vector soundLoc = missile.getLocation().subtract(
									nearest.getLocation()).toVector().normalize().multiply(10);
							((InGamePlayer) nearest).getPlayer().playSound(
									nearest.getLocation().add(soundLoc),
									Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
						}
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
				// spawn fire particle at rocket's location
				missile.getLocation().getWorld().spawnParticle(Particle.FLAME, missile.getLocation(), 0);
			}
		}.runTaskTimer(Flier.getInstance(), 1, 1);
		return true;
	}
	
	public class MissileListener implements Listener {
			
		@EventHandler(priority=EventPriority.LOW)
		public void onHit(EntityDamageByEntityEvent event) {
			if (event.isCancelled()) {
				return;
			}
			Attacker attacker = Attacker.getAttacker(event.getDamager());
			if (attacker != null && attacker.getDamager() instanceof HomingMissile) {
				event.setCancelled(true);
				event.getDamager().remove();
				Target target = attacker.getCreator().getGame().getTargets().get(event.getEntity().getUniqueId());
				if (target != null && target.isTargetable()) {
					attacker.getCreator().getGame().handleHit(target, attacker);
				}
			}
		}

	}

}
