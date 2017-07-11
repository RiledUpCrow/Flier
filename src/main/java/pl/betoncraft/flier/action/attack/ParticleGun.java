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
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.Target;
import pl.betoncraft.flier.api.core.UsableItem;
import pl.betoncraft.flier.core.DefaultAttacker;
import pl.betoncraft.flier.event.FlierProjectileLaunchEvent;

/**
 * Burst shooting weapon with unguided particle-based bullets.
 *
 * @author Jakub Sapalski
 */
public class ParticleGun extends DefaultAttack {
	
	private static final String BURST_AMOUNT = "burst_amount";
	private static final String BURST_TICKS = "burst_ticks";
	private static final String SPREAD = "spread";
	private static final String PROJECTILE_SPEED = "projectile_speed";
	private static final String PROXIMITY = "proximity";
	private static final String PARTICLE = "particle";
	private static final String AMOUNT = "amount";
	private static final String OFFSET = "offset";
	private static final String OFFSET_X = "offset_x";
	private static final String OFFSET_Y = "offset_y";
	private static final String OFFSET_Z = "offset_z";
	private static final String EXTRA = "speed";
	private static final String DENSITY = "density";
	private static final String RANGE = "range";

	private final Flier flier;
	private final Random random;
	private final int burstAmount;
	private final int burstTicks;
	private final double spread;
	private final double projectileSpeed;
	private final double proximity;
	private final double range;

	private final Particle particle;
	private final int amount;
	private final double offsetX;
	private final double offsetY;
	private final double offsetZ;
	private final double extra;
	private final double density;
	
	public ParticleGun(ConfigurationSection section) throws LoadingException {
		super(section);
		flier = Flier.getInstance();
		random = new Random();
		burstAmount = loader.loadPositiveInt(BURST_AMOUNT);
		burstTicks = loader.loadPositiveInt(BURST_TICKS);
		spread = loader.loadNonNegativeDouble(SPREAD, 0.0);
		projectileSpeed = loader.loadPositiveDouble(PROJECTILE_SPEED);
		proximity = loader.loadPositiveDouble(PROXIMITY, 1.0);
		// particle settings
		particle = loader.loadEnum(PARTICLE, Particle.class);
		amount = loader.loadNonNegativeInt(AMOUNT, 0);
		double offset = loader.loadNonNegativeDouble(OFFSET, 0.0);
		offsetX = loader.loadNonNegativeDouble(OFFSET_X, offset);
		offsetY = loader.loadNonNegativeDouble(OFFSET_Y, offset);
		offsetZ = loader.loadNonNegativeDouble(OFFSET_Z, offset);
		extra = loader.loadNonNegativeDouble(EXTRA, 0.0);
		density = loader.loadPositiveDouble(DENSITY, 0.5);
		range = loader.loadPositiveDouble(RANGE, 256.0);
	}
	
	@Override
	public boolean act(Optional<InGamePlayer> creator, Optional<InGamePlayer> source,
			InGamePlayer target, Optional<UsableItem> item) {

		new BukkitRunnable() {
			
			private int burstAmount = (int) modMan.modifyNumber(BURST_AMOUNT, ParticleGun.this.burstAmount);
			private final int burstTicks = (int) modMan.modifyNumber(BURST_TICKS, ParticleGun.this.burstTicks);
			private final double projectileSpeed = modMan.modifyNumber(PROJECTILE_SPEED, ParticleGun.this.projectileSpeed);
			private final double proximity = Math.pow(modMan.modifyNumber(PROXIMITY, ParticleGun.this.proximity), 2);
			private final double spread = modMan.modifyNumber(SPREAD, ParticleGun.this.spread);
			
			private double counter = 0;
			private final double step = (double) burstAmount / (double) burstTicks;
			
			@Override
			public void run() {
				// cancel the runnable if there are no more bursts
				if (burstAmount <= 0) {
					cancel();
					return;
				}
				
				counter += step; // increase already fired bursts
				int am = (int) Math.floor(counter); // get integral amount of already fired bursts
				counter -= am; // remove those integral bursts from counter
				burstAmount -= am;
				
				// get starting values
				Location start = (target.getPlayer().isGliding() ?
								target.getPlayer().getLocation() :
								target.getPlayer().getEyeLocation()
						).add(target.getPlayer().getVelocity())
						.add(target.getPlayer().getLocation().getDirection());
				
				// launch projectiles
				for (int i = 0; i < am; i++) {
					new ParticleTracker(start.clone(), creator.orElse(null),
					        target, projectileSpeed, proximity, spread, range, item.orElse(null));
                         // target becomes the source
					// call event for each projectile launched
					Bukkit.getPluginManager().callEvent(new FlierProjectileLaunchEvent(target, ParticleGun.this));
				}
			}
			
		}.runTaskTimer(flier, 0, 1);
		return true;
	}
	
	private class ParticleTracker extends BukkitRunnable {
		
		private Location start;
		private InGamePlayer creator;
		private InGamePlayer source;
		private double proximity;
		private UsableItem weapon;
		
		private World world;
		private Vector dir;
		private Vector vel;
		private double speed;
		private double range;

		private final BlockIterator tracer;
		private Location end;
		private Vector currentVel;
		private double currentSpeed;
		private double squared;
		private boolean early = false;
		
		public ParticleTracker(Location start, InGamePlayer creator, InGamePlayer source, double projectileSpeed, double proximity,
				double spread, double range, UsableItem weapon) {
			// get starting parameters
			this.start = start;
			this.creator = creator;
			this.source = source;
			this.proximity = proximity;
			this.weapon = weapon;
			this.range = range;
			world = start.getWorld();
			dir = start.getDirection();
			
			// calculate random spread
			if (spread > 0) {
				double x = random.nextGaussian();
				double y = random.nextGaussian();
				double z = random.nextGaussian();
				Vector perpendicular = new Vector(x, y, z).normalize().multiply(spread);
				dir.add(perpendicular).normalize();
			}
			
			// create block tracer
			tracer = new BlockIterator(world, start.toVector(), dir, 0, 0);
			
			// calculate velocity and speed
			vel = dir.clone().multiply(projectileSpeed);
			speed = projectileSpeed;
			runTaskTimer(flier, 0, 1);
		}
		
		@Override
		public void run() {
			
			currentVel = vel.clone();
			currentSpeed = speed;
			squared = speed * speed;
			end = start.clone().add(currentVel);

			// terminate the bullet if it's going out of loaded chunks
			// this prevents loading chunks from file to get block solidness
			if (!end.getChunk().isLoaded()) {
				cancel();
				return;
			}
			
			// check for any solid blocks in the path
			while (tracer.hasNext()) {
				Block block = tracer.next();
				if (block.getType().isSolid()) {
					// found an obstacle, early end
					earlyEnd(block.getLocation());
					break;
				} else if (block.getLocation().distanceSquared(start) > squared) {
					// this block is out of current tick's range
					// we'll continue from here in the next tick
					break;
				}
			}
			
			// check targets in proximity of the bullet's path
			// we're looking only for the closest target
			Target foundTarget = null;
			double smallestDistance = squared;
			for (Target target : creator.getGame().getTargets().values()) {
				// don't hit the shooter
				if (!target.isTargetable() || target.equals(creator)) {
					continue;
				}
				Location loc = target.getLocation().clone();
				double dist = loc.subtract(start).toVector().crossProduct(dir).lengthSquared();
				if (dist < proximity) {
					// found player in bullet's path
					// loc is now a vector from start to player's location
					double thisDistance = loc.lengthSquared();
					if (thisDistance < smallestDistance) {
						smallestDistance = thisDistance;
						foundTarget = target;
					}
				}
			}
			// hit closest player
			if (foundTarget != null) {
				earlyEnd(foundTarget.getLocation());
				foundTarget.getGame().handleHit(foundTarget,
						new DefaultAttacker(ParticleGun.this, creator, source, weapon));
			}
			
			// spawn particles 
			int step = (int) (currentSpeed * density);
			currentVel.multiply(1.0 / step);
			for (int i = 0; i < step; i++) {
				// this moves start so it becomes end,
				// and can still be used on the next tick
				start.add(currentVel);
				world.spawnParticle(particle, start, amount, offsetX, offsetY, offsetZ, extra);
			}
			
			if (early) {
				world.spawnParticle(particle, start, 100, 0, 0, 0, 0.25);
			}
			
			// decrease range and quit if it's out
			range -= currentSpeed;
			if (range <= 0) {
				cancel();
			}
		}

		/**
		 * Calculates the point on the bullet's path closest to specified location.
		 */
		private void earlyEnd(Location loc) {
			Vector p1 = start.toVector();
			Vector p2 = end.toVector();
			Vector q = loc.toVector();
			
			Vector u = p2.subtract(p1);
			Vector pq = q.clone().subtract(p1);
			Vector w2 = pq.subtract(u.multiply(pq.dot(u) / u.lengthSquared()));
			
			end = q.subtract(w2).toLocation(world);
			currentVel = end.clone().subtract(start).toVector();
			currentSpeed = currentVel.length();
			squared = currentSpeed * currentSpeed;
			early = true;
			cancel();
		}
	}

}
