/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api;

import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

/**
 * Represents wings - elytra item with statistics.
 *
 * @author Jakub Sapalski
 */
public interface Wings extends Weight {

	/**
	 * Applies modifications to player's velocity vector. Use this method to apply your wings'
	 * flight model.
	 * 
	 * @param velocity player's velocity Vector
	 * @return the same Vector, modified
	 */
	Vector applyFlightModifications(Vector velocity);

	/**
	 * @return ItemStack equal to these wings
	 */
	public ItemStack getItem();

	/**
	 * @return maximum health of the wings
	 */
	public double getHealth();

	/**
	 * @return the regeneration rate of the wings
	 */
	public double getRegeneration();

	/**
	 * @return property describing how well the wings deal with air resistance
	 */
	public double getAerodynamics();

	/**
	 * @return property describing how much lifting force these wings generate
	 *         when flying at speed
	 */
	public double getLiftingForce();

}
