/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api.content;

import org.bukkit.util.Vector;

import pl.betoncraft.flier.api.core.Item;

/**
 * Represents an engine, which speeds players up.
 *
 * @author Jakub Sapalski
 */
public interface Engine extends Item {

	/**
	 * Launches the player in the direction he's looking, making modifications
	 * to his velocity. Use this method to implement your engine's mechanics.
	 * 
	 * @param velocity
	 *            velocity Vector of the player
	 * @param direction
	 *            direction Vector where the player is looking
	 * @return the modified velocity Vector
	 */
	public Vector launch(Vector velocity, Vector direction);

	/**
	 * @return the maximum amount of stored fuel
	 */
	public double getMaxFuel();

	/**
	 * @return the fuel consumption per flight tick
	 */
	public double getConsumption();

	/**
	 * @return the fuel regeneration per flight tick
	 */
	public double getRegeneration();

	/**
	 * @return the amount of tick to glow after speed up
	 */
	public int getGlowTime();

	/**
	 * @return the amount of fuel currently in the Engine
	 */
	public double getFuel();

	/**
	 * @param amount
	 *            amount of fuel to add; must be positive
	 * @return whenever the fuel was successfully added; false means the tank is
	 *         full
	 */
	public boolean addFuel(double amount);

	/**
	 * @param amount
	 *            amount of fuel to remove; must be positive
	 * @return whenever the fuel was successfully removed; false means the tank
	 *         is empty
	 */
	public boolean removeFuel(double amount);

	/**
	 * Refills the Engine, making it as new.
	 */
	public void refill();

}
