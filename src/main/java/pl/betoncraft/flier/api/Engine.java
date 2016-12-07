/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api;

import org.bukkit.inventory.ItemStack;

/**
 * Represents an engine, which speeds players up.
 *
 * @author Jakub Sapalski
 */
public interface Engine extends Weight {
	
	/**
	 * @return the engine item
	 */
	public ItemStack getItem();

	/**
	 * @return the maximum acceleration speed
	 */
	public double getMaxSpeed();

	/**
	 * @return the minimum speed for multiplication accelerator
	 */
	public double getMinSpeed();

	/**
	 * @return the acceleration multiplier per flight tick
	 */
	public double getAcceleration();

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

}
