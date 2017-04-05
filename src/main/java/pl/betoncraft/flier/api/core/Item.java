/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api.core;

import org.bukkit.inventory.ItemStack;

/**
 * Represents an item.
 *
 * @author Jakub Sapalski
 */
public interface Item {

	/**
	 * @return the ID of this Item, which is the section defining it in the file
	 */
	public String getID();

	/**
	 * @return the ItemStack equal to this item, with strings translated for specified player
	 */
	public ItemStack getItem(InGamePlayer player);

	/**
	 * @return weight of an item
	 */
	public double getWeight();

	/**
	 * @return the slot in which this item is to be placed
	 */
	public int slot();

	/**
	 * Compares two items by the default values (ignores mutable values). This
	 * returns true for replicated items.
	 * 
	 * @param key
	 *            another item to compare
	 * @return true if the item was replicated from this one, false if not
	 */
	public boolean isSimilar(Item key);
	
	/**
	 * Applies passed modification to this Item.
	 * 
	 * @param mod
	 */
	public void addModification(Modification mod);
	
	/**
	 * Removes passed modification from this Item.
	 * 
	 * @param mod
	 */
	public void removeModification(Modification mod);
	
	/**
	 * Removes all modifications from this item.
	 */
	public void clearModifications();

}
