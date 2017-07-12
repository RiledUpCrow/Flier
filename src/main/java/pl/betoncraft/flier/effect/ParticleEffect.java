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
package pl.betoncraft.flier.effect;

import java.util.Optional;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;

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
		if (!type.isPlayerInvolved()) {
			throw new LoadingException("Game sound effect cannot be run on a non-player event.");
		}
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
	public void fire(Optional<InGamePlayer> player) {
		for (int i = 0; i < count; i++) {
			Location loc = player.get().getLocation();
			loc.add(manualOffsetX * random.nextGaussian(),
					manualOffsetY * random.nextGaussian(),
					manualOffsetZ * random.nextGaussian());
			player.get().getPlayer().getWorld().spawnParticle(particle, loc, amount, offsetX, offsetY, offsetZ, speed);
		}
	}

}
