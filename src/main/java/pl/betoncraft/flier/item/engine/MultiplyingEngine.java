/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.item.engine;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.core.Utils.ImmutableVector;

/**
 * Engine which multiplies speed instead of adding a fixed acceleration.
 *
 * @author Jakub Sapalski
 */
public class MultiplyingEngine extends DefaultEngine {
	
	private double maxSpeed = 1.5;
	private double minSpeed = 0.5;
	private double acceleration = 1.2;
	
	public MultiplyingEngine(ConfigurationSection section) {
		super(section);
		maxSpeed = section.getDouble("max_speed", maxSpeed);
		minSpeed = section.getDouble("min_speed", minSpeed);
		acceleration = section.getDouble("acceleration", acceleration);
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
