/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.Wings;

/**
 * Simple wings.
 *
 * @author Jakub Sapalski
 */
public class SimpleWings extends DefaultItem implements Wings {
	
	private double health = 100;
	private double regeneration = 1;
	private double aerodynamics = 0;
	private double liftingForce = 0;
	
	public SimpleWings(ConfigurationSection section) {
		super(section);
		super.item.setType(Material.ELYTRA);
		health = section.getDouble("health", health);
		regeneration = section.getDouble("regeneration", regeneration);
		aerodynamics = section.getDouble("aerodynamics", aerodynamics);
		liftingForce = section.getDouble("liftingforce", liftingForce);
	}
	
	@Override
	public double getHealth() {
		return health;
	}

	@Override
	public double getRegeneration() {
		return regeneration;
	}

	@Override
	public double getAerodynamics() {
		return aerodynamics;
	}
	
	@Override
	public double getLiftingForce() {
		return liftingForce;
	}

}