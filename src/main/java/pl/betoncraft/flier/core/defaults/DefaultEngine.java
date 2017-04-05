/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core.defaults;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.content.Engine;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.Modification;

/**
 * Default Engine implementation.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultEngine extends DefaultItem implements Engine {

	private static final String GLOW_TIME = "glow_time";
	private static final String REGENERATION = "regeneration";
	private static final String CONSUMPTION = "consumption";
	private static final String MAX_FUEL = "max_fuel";

	protected final double maxFuel;
	protected final double consumption;
	protected final double regeneration;
	protected final int glowTime;
	
	protected double fuel;

	public DefaultEngine(ConfigurationSection section) throws LoadingException {
		super(section);
		maxFuel = loader.loadPositiveDouble(MAX_FUEL);
		consumption = loader.loadNonNegativeDouble(CONSUMPTION);
		regeneration = loader.loadNonNegativeDouble(REGENERATION);
		glowTime = loader.loadNonNegativeInt(GLOW_TIME);
		fuel = maxFuel;
	}

	@Override
	public double getMaxFuel() {
		return modMan.modifyNumber(MAX_FUEL, maxFuel);
	}

	@Override
	public double getConsumption() {
		return modMan.modifyNumber(CONSUMPTION, consumption);
	}

	@Override
	public double getRegeneration() {
		return modMan.modifyNumber(REGENERATION, regeneration);
	}
	
	@Override
	public int getGlowTime() {
		return (int) modMan.modifyNumber(GLOW_TIME, glowTime);
	}

	@Override
	public double getFuel() {
		return fuel;
	}
	
	@Override
	public boolean addFuel(double amount) {
		double maxFuel = getMaxFuel();
		if (fuel >= maxFuel) {
			return false;
		}
		if (fuel + amount > maxFuel) {
			fuel = maxFuel;
		} else {
			fuel += amount;
		}
		return true;
	}
	
	@Override
	public boolean removeFuel(double amount) {
		if (fuel <= 0) {
			return false;
		}
		if (fuel < amount) {
			fuel = 0;
		} else {
			fuel -= amount;
		}
		return true;
	}
	
	@Override
	public void refill() {
		fuel = getMaxFuel();
	}
	
	@Override
	public void addModification(Modification mod) {
		modMan.addModification(mod);
	}
	
	@Override
	public void removeModification(Modification mod) {
		modMan.removeModification(mod);
	}
	
	@Override
	public void clearModifications() {
		modMan.clear();
	}

}
