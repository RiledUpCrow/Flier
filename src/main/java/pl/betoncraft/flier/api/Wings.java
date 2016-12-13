/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api;

import pl.betoncraft.flier.core.PlayerData;
import pl.betoncraft.flier.core.Utils.ImmutableVector;

/**
 * Represents wings - elytra item with statistics.
 *
 * @author Jakub Sapalski
 */
public interface Wings extends Item {

	/**
	 * Applies modifications to player's velocity vector. Use this method to apply your wings'
	 * flight model.
	 * 
	 * @param velocity player's velocity Vector
	 * @return the same Vector, modified
	 */
	public ImmutableVector applyFlightModifications(PlayerData data);

	/**
	 * @return maximum health of the wings
	 */
	public double getHealth();

	/**
	 * @return the regeneration rate of the wings
	 */
	public double getRegeneration();

}
