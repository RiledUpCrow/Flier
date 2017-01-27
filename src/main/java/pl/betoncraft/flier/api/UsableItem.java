/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api;

import java.util.List;

/**
 * Represents an item which can be used by the player to run an action.
 *
 * @author Jakub Sapalski
 */
public interface UsableItem extends Item {
	
	public enum Where {
		GROUND, AIR, FALL, NO_GROUND, NO_AIR, NO_FALL, EVERYWHERE
	}

	/**
	 * Uses this item once.
	 * 
	 * @param player
	 * @return if the item was used
	 */
	public boolean use(InGamePlayer player);
	
	/**
	 * @return the list of all actions in this item
	 */
	public List<Action> getActions();

	/**
	 * Checks cooldown. It will start another cooldown upon returning true.
	 */
	public boolean cooldown();

	/**
	 * @return whenever this item should be consumed on use
	 */
	public boolean isConsumable();

	/**
	 * @return where this item can be used
	 */
	public Where where();

}
