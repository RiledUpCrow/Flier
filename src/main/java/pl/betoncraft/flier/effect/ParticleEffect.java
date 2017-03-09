/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.effect;

import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.core.defaults.DefaultEffect;

/**
 * Creates an explosion without damage at player's location.
 *
 * @author Jakub Sapalski
 */
public class ParticleEffect extends DefaultEffect {
	
	private final Random random;
	
	private static final String PARTICLE = "particle";
	private static final String AMOUNT = "amount";
	private static final String OFFSET = "offset";
	private static final String OFFSET_X = "offset_x";
	private static final String OFFSET_Y = "offset_y";
	private static final String OFFSET_Z = "offset_z";
	private static final String SPEED = "speed";
	private static final String COUNT = "count";
	private static final String MANUAL_OFFSET = "manual_offset";
	private static final String MANUAL_OFFSET_X = "manual_offset_x";
	private static final String MANUAL_OFFSET_Y = "manual_offset_y";
	private static final String MANUAL_OFFSET_Z = "manual_offset_z";
	
	private Particle particle;
	private int count;
	private double manualOffsetX;
	private double manualOffsetY;
	private double manualOffsetZ;
	private int amount;
	private double offsetX;
	private double offsetY;
	private double offsetZ;
	private double speed;

	public ParticleEffect(ConfigurationSection section) throws LoadingException {
		super(section);
		random = new Random();
		playerOnly();
		particle = loader.loadEnum(PARTICLE, Particle.class);
		amount = loader.loadNonNegativeInt(AMOUNT, 0);
		double offset = loader.loadNonNegativeDouble(OFFSET, 0.0);
		offsetX = loader.loadNonNegativeDouble(OFFSET_X, offset);
		offsetY = loader.loadNonNegativeDouble(OFFSET_Y, offset);
		offsetZ = loader.loadNonNegativeDouble(OFFSET_Z, offset);
		speed = loader.loadNonNegativeDouble(SPEED, 0.0);
		// manual particle spawning
		count = loader.loadNonNegativeInt(COUNT, 1);
		double manualOffset = loader.loadNonNegativeDouble(MANUAL_OFFSET, 0.0);
		manualOffsetX = loader.loadNonNegativeDouble(MANUAL_OFFSET_X, manualOffset);
		manualOffsetY = loader.loadNonNegativeDouble(MANUAL_OFFSET_Y, manualOffset);
		manualOffsetZ = loader.loadNonNegativeDouble(MANUAL_OFFSET_Z, manualOffset);
	}

	@Override
	public void fire(InGamePlayer player) {
		for (int i = 0; i < count; i++) {
			Location loc = player.getPlayer().getLocation();
			loc.add(manualOffsetX * random.nextGaussian(),
					manualOffsetY * random.nextGaussian(),
					manualOffsetZ * random.nextGaussian());
			player.getPlayer().getWorld().spawnParticle(particle, loc, amount, offsetX, offsetY, offsetZ, speed);
		}
	}

}
