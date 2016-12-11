/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.item;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

import pl.betoncraft.flier.api.Engine;

/**
 * Engine which multiplies speed instead of adding a fixed acceleration.
 *
 * @author Jakub Sapalski
 */
public class MultiplyingEngine extends DefaultItem implements Engine {
	
	private double maxSpeed = 1.5;
	private double minSpeed = 0.5;
	private double acceleration = 1.2;
	private double maxFuel = 100;
	private double consumption = 2;
	private double regeneration = 1;
	private int glowTime = 100;
	
	public MultiplyingEngine(ConfigurationSection section) {
		super(section);
		maxSpeed = section.getDouble("max_speed", maxSpeed);
		minSpeed = section.getDouble("min_speed", minSpeed);
		acceleration = section.getDouble("acceleration", acceleration);
		maxFuel = section.getDouble("max_fuel", maxFuel);
		consumption = section.getDouble("consumption", consumption);
		regeneration = section.getDouble("regeneration", regeneration);
		glowTime = section.getInt("glow_time", glowTime);
	}
	
	@Override
	public Vector launch(Vector velocity, Vector direction) {
		double speed = velocity.length();
		
		if (speed > maxSpeed) {
			speed = 0;
		} else if (speed < minSpeed) {
			speed = minSpeed;
		}
		velocity.add(direction.multiply(speed * getAcceleration()));

//		// different algorithm
//		if (speed > maxSpeed) {
//			return;
//		}
//		if (speed < minSpeed) {
//			Vector direction = player.getLocation().getDirection();
//			velocity.add(direction.multiply(acceleration));
//		} else {
//			velocity.multiply(acceleration + 1);
//		}
//		speed = velocity.length();
//		if (speed > maxSpeed) {
//			velocity.multiply(maxSpeed / speed);
//		}
		
		return velocity;
	}

	@Override
	public double getMaxSpeed() {
		return maxSpeed;
	}

	@Override
	public double getMinSpeed() {
		return minSpeed;
	}

	@Override
	public double getAcceleration() {
		return acceleration;
	}

	@Override
	public double getMaxFuel() {
		return maxFuel;
	}

	@Override
	public double getConsumption() {
		return consumption;
	}

	@Override
	public double getRegeneration() {
		return regeneration;
	}
	
	@Override
	public int getGlowTime() {
		return glowTime;
	}

}
