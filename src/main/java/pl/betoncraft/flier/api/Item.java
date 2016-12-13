/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api;

import org.bukkit.inventory.ItemStack;

/**
 * Represents anything with weight.
 *
 * @author Jakub Sapalski
 */
public interface Item {
	
	/**
	 * @return the ItemStack equal to this item
	 */
	public ItemStack getItem();
	
	/**
	 * @return weight of an item
	 */
	public double getWeight();

}
