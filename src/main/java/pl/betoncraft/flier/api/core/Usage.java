/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api.core;

import java.util.List;

import pl.betoncraft.flier.api.content.Action;
import pl.betoncraft.flier.api.content.Activator;

/**
 * Represents a set of Activators which can activate a set of Actions.
 *
 * @author Jakub Sapalski
 */
public interface Usage {

	/**
	 * Represents a location where the player can be.
	 *
	 * @author Jakub Sapalski
	 */
	public enum Where {
		GROUND, AIR, FALL, NO_GROUND, NO_AIR, NO_FALL, EVERYWHERE
	}
	
	/**
	 * @return the ID of this Usage
	 */
	public String getID();

	/**
	 * @return the list of Activators in this Usage
	 */
	public List<Activator> getActivators();

	/**
	 * @return the list of Actions in this Usage
	 */
	public List<Action> getActions();

	/**
	 * @return the cooldown time this Usage takes
	 */
	public int getCooldown();

	/**
	 * @return the amount of ammo this Usage uses per use
	 */
	public int getAmmoUse();

	/**
	 * @return where this item can be used
	 */
	public Where where();

	/**
	 * Checks if the player is in a correct position to use this item.
	 * 
	 * @param player
	 *            player to check
	 * @return whenever it's possible to use this item in player's position
	 */
	public boolean canUse(InGamePlayer player);

}
