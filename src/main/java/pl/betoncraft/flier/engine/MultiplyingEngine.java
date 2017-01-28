/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.engine;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.core.defaults.DefaultEngine;
import pl.betoncraft.flier.exception.LoadingException;
import pl.betoncraft.flier.util.Utils.ImmutableVector;

/**
 * Engine which multiplies speed instead of adding a fixed acceleration.
 *
 * @author Jakub Sapalski
 */
public class MultiplyingEngine extends DefaultEngine {
	
	private final double maxSpeed;
	private final double minSpeed;
	private final double acceleration;
	
	public MultiplyingEngine(ConfigurationSection section) throws LoadingException {
		super(section);
		maxSpeed = loader.loadNonNegativeDouble("max_speed");
		minSpeed = loader.loadNonNegativeDouble("min_speed");
		acceleration = loader.loadNonNegativeDouble("acceleration");
	}
	
	@Override
	public ImmutableVector launch(ImmutableVector velocity, ImmutableVector direction) {
		double speed = velocity.length();
		if (speed > maxSpeed) {
			speed = 0;
		} else if (speed < minSpeed) {
			speed = minSpeed;
		}
		return velocity.add(direction.multiply(speed * acceleration));
	}

}
