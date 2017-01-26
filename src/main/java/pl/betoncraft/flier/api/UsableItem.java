/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api;

/**
 * Represents an item which can be used by the player.
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
	 * Checks cooldown for this player. If it's true,
	 * it should start another cooldown.
	 * 
	 * @param player
	 */
	public boolean cooldown(InGamePlayer player);

	/**
	 * @return whenever this item should be consumed on use
	 */
	public boolean isConsumable();

	/**
	 * @return where this item can be used
	 */
	public Where where();

}
