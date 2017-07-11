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
package pl.betoncraft.flier.engine;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.content.Engine;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.Modification;
import pl.betoncraft.flier.core.DefaultItem;

/**
 * Default Engine implementation.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultEngine extends DefaultItem implements Engine {

	private static final String REGENERATION = "regeneration";
	private static final String CONSUMPTION = "consumption";
	private static final String MAX_FUEL = "max_fuel";

	protected final double maxFuel;
	protected final double consumption;
	protected final double regeneration;
	
	protected double fuel;

	public DefaultEngine(ConfigurationSection section) throws LoadingException {
		super(section);
		maxFuel = loader.loadPositiveDouble(MAX_FUEL);
		consumption = loader.loadNonNegativeDouble(CONSUMPTION);
		regeneration = loader.loadNonNegativeDouble(REGENERATION);
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
	public double getFuel() {
		double max = getMaxFuel();
		if (max != 0 && fuel > max) {
			fuel = max;
		}
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
