/**
 * Copyright (c) 2017 Jakub Sapalski
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */
package pl.betoncraft.flier.api.core;

import java.util.List;

/**
 * Represents an item which can be used by the player to run an action.
 *
 * @author Jakub Sapalski
 */
public interface UsableItem extends Item {

	/**
	 * Uses this item once.
	 * 
	 * @param player
	 *            the player for which the item will be used
	 * @return if the item was used or not
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
	 * @return the amount of this Item
	 */
	public int getAmount();

	/**
	 * @param amount
	 *            the new amount
	 * @return true when it's possible to set that many items, false if the new
	 *         amount is illegal (less than 0 or greater than maximum)
	 */
	public boolean setAmount(int amount);

	/**
	 * @return the maximum amount of these Items
	 */
	public int getMaxAmount();

	/**
	 * @return the minimum amount of these Items (in case you want to sell
	 *         too much)
	 */
	public int getMinAmount();

	/**
	 * @return the default amount of these Items
	 */
	public int getDefAmount();

	/**
	 * Refills the UsableItem, making it as new.
	 */
	public void refill();

}
