/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api;

import pl.betoncraft.flier.core.Utils.ImmutableVector;

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
	 * @param velocity velocity Vector of the player
	 * @param direction direction Vector where the player is looking
	 * @return the modified velocity Vector
	 */
	public ImmutableVector launch(ImmutableVector velocity, ImmutableVector direction);

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
