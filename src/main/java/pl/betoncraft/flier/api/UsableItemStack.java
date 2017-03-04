/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api;

/**
 * Represents a stack of UsableItems, which can have amount and a maximum
 * amount.
 *
 * @author Jakub Sapalski
 */
public interface UsableItemStack {

	/**
	 * @return the UsableItem type of this stack
	 */
	public UsableItem getItem();

	/**
	 * @return the amount of UsableItems on this stack
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
	 * @return the maximum amount of items on this stack
	 */
	public int getMax();

	/**
	 * @return the minimum amount of items on this stack (in case you want to
	 *         sell too much)
	 */
	public int getMin();

	/**
	 * @return the default amount in this stack
	 */
	public int getDefaultAmount();

	/**
	 * @param item
	 *            another UsableItemStack
	 * @return whenever these two stacks are of the same UsableItem type
	 */
	public boolean isSimilar(UsableItemStack item);

	/**
	 * @return the new, identical UsableItemStack. It also replicated the
	 *         UsableItem inside.
	 */
	public UsableItemStack clone();

}