/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.item.engine;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.Engine;
import pl.betoncraft.flier.exception.LoadingException;
import pl.betoncraft.flier.item.DefaultItem;

/**
 * Default Engine implementation.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultEngine extends DefaultItem implements Engine {
	
	protected double maxFuel = 100;
	protected double consumption = 2;
	protected double regeneration = 1;
	protected int glowTime = 100;

	public DefaultEngine(ConfigurationSection section) throws LoadingException {
		super(section);
		maxFuel = section.getDouble("max_fuel", maxFuel);
		consumption = section.getDouble("consumption", consumption);
		regeneration = section.getDouble("regeneration", regeneration);
		glowTime = section.getInt("glow_time", glowTime);
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
