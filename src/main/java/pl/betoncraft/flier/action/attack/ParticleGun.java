/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.action.attack;

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
import pl.betoncraft.flier.core.defaults.DefaultAttack;

/**
 * Burst shooting weapon with unguided particle-based bullets.
 *
 * @author Jakub Sapalski
 */
public class ParticleGun extends DefaultAttack {
	
	private static final String BURST_AMOUNT = "burst_amount";
	private static final String BURST_TICKS = "burst_ticks";
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

	private final Flier flier;
	private final int burstAmount;
	private final int burstTicks;
	private final double projectileSpeed;
	private final double proximity;

	private final Particle particle;
	private final int amount;
	private final double offsetX;
	private final double offsetY;
	private final double offsetZ;
	private final double extra;
	private final double density;
	
	public ParticleGun(ConfigurationSection section) throws LoadingException {
		super(section);
		this.flier = Flier.getInstance();
		burstAmount = loader.loadPositiveInt(BURST_AMOUNT);
		burstTicks = loader.loadPositiveInt(BURST_TICKS);
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
	}
	
	@Override
	public boolean act(InGamePlayer data) {
		
		double projectileSpeed = modMan.modifyNumber(PROJECTILE_SPEED, ParticleGun.this.projectileSpeed);
		double proximity = Math.pow(modMan.modifyNumber(PROXIMITY, ParticleGun.this.proximity), 2);
		
		new BukkitRunnable() {
			
			private int burstAmount = (int) modMan.modifyNumber(BURST_AMOUNT, ParticleGun.this.burstAmount);
			
			@Override
			public void run() {
				if (burstAmount == 0) {
					cancel();
					return;
				}
				Location start = (data.getPlayer().isGliding() ?
								data.getPlayer().getLocation() :
								data.getPlayer().getEyeLocation()
						).add(data.getPlayer().getVelocity())
						.add(data.getPlayer().getLocation().getDirection());
				World world = start.getWorld();
				Vector dir = start.getDirection();
				Vector vel = dir.clone().multiply(projectileSpeed);
				double speed = projectileSpeed;
				
				new BukkitRunnable() {

					private final BlockIterator tracer = new BlockIterator(world, start.toVector(), dir, 0, 0);
					private double range = 256;
					private Location end;
					private Vector currentVel;
					private double currentSpeed;
					private double squared;
					
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
						
						// check players in proximity of the bullet's path
						// we're looking only for the closest player
						InGamePlayer foundPlayer = null;
						double smallestDistance = squared;
						for (InGamePlayer player : data.getLobby().getGame().getPlayers().values()) {
							// don't hit the shooter
							if (player.equals(data)) {
								continue;
							}
							Location loc = player.getPlayer().getLocation().toVector()
									.midpoint(player.getPlayer().getEyeLocation().toVector())
									.toLocation(world);
							double dist = loc.subtract(start).toVector().crossProduct(dir).lengthSquared();
							if (dist < proximity) {
								// found player in bullet's path
								// loc is now a vector from start to player's location
								double thisDistance = loc.lengthSquared();
								if (thisDistance < smallestDistance) {
									smallestDistance = thisDistance;
									foundPlayer = player;
								}
							}
						}
						// hit closest player
						if (foundPlayer != null) {
							earlyEnd(foundPlayer.getPlayer().getLocation());
							foundPlayer.getLobby().getGame().handleHit(data, foundPlayer, ParticleGun.this);
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
						cancel();
					}

				}.runTaskTimer(flier, 0, 1);
				burstAmount--;
			}
			
		}.runTaskTimer(flier, 0, (int) modMan.modifyNumber(BURST_TICKS, ParticleGun.this.burstTicks));
		return true;
	}

}
