/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api;

import pl.betoncraft.flier.core.PlayerData;

/**
 * Represents an item which can be used by the player.
 *
 * @author Jakub Sapalski
 */
public interface UsableItem extends Item {

	/**
	 * Uses this item once.
	 * 
	 * @param player
	 * @return if the item was used
	 */
	public boolean use(PlayerData player);

	/**
	 * Checks cooldown for this player. If it's true,
	 * it should start another cooldown.
	 * 
	 * @param player
	 */
	public boolean cooldown(PlayerData player);

	/**
	 * @return whenever this item should be consumed on use
	 */
	public boolean isConsumable();

	/**
	 * @return whenever this item is only usable while gliding
	 */
	public boolean onlyAir();

}
