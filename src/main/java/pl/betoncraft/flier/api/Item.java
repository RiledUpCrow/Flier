/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api;

import java.util.List;

import org.bukkit.inventory.ItemStack;

/**
 * Represents an item.
 *
 * @author Jakub Sapalski
 */
public interface Item extends Replicable {
	
	/**
	 * @return the ItemStack equal to this item
	 */
	public ItemStack getItem();
	
	/**
	 * @return weight of an item
	 */
	public double getWeight();

	/**
	 * @return the slot in which this item is to be placed
	 */
	public int slot();
	
	/**
	 * @return the list of passive effects of this item
	 */
	public List<Effect> getPassiveEffects();
	
	/**
	 * @return the list of effects applied when this item is held
	 */
	public List<Effect> getInHandEffects();

}
