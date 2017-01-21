/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.item.engine;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.core.Utils.ImmutableVector;
import pl.betoncraft.flier.core.ValueLoader;
import pl.betoncraft.flier.exception.LoadingException;

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
		maxSpeed = ValueLoader.loadNonNegativeDouble(section, "max_speed");
		minSpeed = ValueLoader.loadNonNegativeDouble(section, "min_speed");
		acceleration = ValueLoader.loadNonNegativeDouble(section, "acceleration");
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
	
	@Override
	public MultiplyingEngine replicate() {
		try {
			return new MultiplyingEngine(base);
		} catch (LoadingException e) {
			return null; // dead code
		}
	}

}
