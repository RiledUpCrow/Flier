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

import org.bukkit.inventory.ItemStack;

/**
 * Represents an item.
 *
 * @author Jakub Sapalski
 */
public interface Item extends Named {

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
