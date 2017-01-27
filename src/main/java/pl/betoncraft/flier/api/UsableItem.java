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
		GROUND, AIR, FALL, NO_GROUND, NO_AIR, NO_FALL, EVERYWHERE, NOWHERE
	}

	/**
	 * Uses this item once.
	 * 
	 * @param player
	 * @return if the item was used
	 */
	public boolean use(InGamePlayer player);

	/**
	 * @return the list of all usages of this item
	 */
	public List<Usage> getUsages();

	/**
	 * @return whenever this item is ready to use
	 */
	public boolean isReady();

	/**
	 * @return whole cooldown time for this item when it started
	 */
	public int getWholeCooldown();

	/**
	 * @return the current remaining cooldown time
	 */
	public int getCooldown();

	/**
	 * @return whenever this item should be consumed on use
	 */
	public boolean isConsumable();

	/**
	 * @return maximum ammunition amount for this item; 0 means it doesn't use
	 *         ammunition
	 */
	public int getMaxAmmo();

	/**
	 * @return the current amount of ammunition
	 */
	public int getAmmo();

	/**
	 * Sets the ammunition of the UsableItem. This won't go above max ammunition
	 * nor below 0.
	 * 
	 * @param ammo
	 *            the amount of ammunition to set
	 */
	public void setAmmo(int ammo);

	/**
	 * @return where this item can be used
	 */
	public Where where();

	/**
	 * Checks if the player is in a correct position to use this item.
	 * 
	 * @param player player to check
	 * @return whenever it's possible to use this item in player's position
	 */
	public boolean canUse(InGamePlayer player);

}
