/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.engine;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

import pl.betoncraft.flier.api.core.Item;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.core.defaults.DefaultEngine;

/**
 * Engine which multiplies speed instead of adding a fixed acceleration.
 *
 * @author Jakub Sapalski
 */
public class MultiplyingEngine extends DefaultEngine {
	
	private static final String ACCELERATION = "acceleration";
	private static final String MIN_SPEED = "min_speed";
	private static final String MAX_SPEED = "max_speed";

	private final double maxSpeed;
	private final double minSpeed;
	private final double acceleration;
	
	public MultiplyingEngine(ConfigurationSection section) throws LoadingException {
		super(section);
		maxSpeed = loader.loadNonNegativeDouble(MAX_SPEED);
		minSpeed = loader.loadNonNegativeDouble(MIN_SPEED);
		acceleration = loader.loadNonNegativeDouble(ACCELERATION);
	}
	
	@Override
	public Vector launch(Vector velocity, Vector direction) {
		double speed = velocity.length();
		if (speed > modMan.modifyNumber(MAX_SPEED, maxSpeed)) {
			speed = 0;
		} else {
			double minSpeed = modMan.modifyNumber(MIN_SPEED, this.minSpeed);
			if (speed < minSpeed) {
				speed = minSpeed;
			}
		}
		return velocity.add(direction.multiply(speed * modMan.modifyNumber(ACCELERATION, acceleration)));
	}
	
	@Override
	public boolean isSimilar(Item key) {
		if (key instanceof MultiplyingEngine && super.isSimilar(key)) {
			MultiplyingEngine engine = (MultiplyingEngine) key;
			return engine.maxFuel == maxFuel &&
					engine.maxSpeed == maxSpeed &&
					engine.acceleration == acceleration;
		}
		return false;
	}

}
