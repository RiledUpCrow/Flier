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
import java.util.Optional;

import pl.betoncraft.flier.api.content.Engine;
import pl.betoncraft.flier.api.content.Wings;

/**
 * Represents a set of items which can be applied to Kit.
 *
 * @author Jakub Sapalski
 */
public interface ItemSet extends Named {

	/**
	 * Category is used to group the ItemSets together and handle adding new
	 * ItemSets.
	 *
	 * @return the category name of this ItemSet
	 */
	public String getCategory();

	/**
	 * @return the optional name of the class after applying this ItemSet
	 */
	public Optional<String> getClassName();

	/**
	 * @return the Engine in this ItemSet
	 */
	public Engine getEngine();

	/**
	 * @param engine
	 *            the Engine to set in this ItemSet
	 */
	public void setEngine(Engine engine);

	/**
	 * @return the Wings in this ItemSet
	 */
	public Wings getWings();

	/**
	 * @param wings
	 *            the Wings to set in this ItemSet
	 */
	public void setWings(Wings wings);

	/**
	 * @return the list of UsableItems in this ItemSet
	 */
	public List<UsableItem> getItems();

	/**
	 * @return the list of Modifications in this ItemSet
	 */
	List<Modification> getModifications();

	/**
	 * @return whenever this ItemSet is empty.
	 */
	public boolean isEmpty();

	/**
	 * @return the amount of these ItemSets in this ItemSet (increased by the
	 *         increase() method).
	 */
	public int getAmount();

	/**
	 * Increases the amount of UsableItems in this ItemSet by specified amount
	 * of these ItemSets (can be negative) If by default there were 3 items
	 * here, increasing it by 2 will add 2*3=6 items, making it 9. It will not
	 * increase over the maximum limit, nor decrease below 0.
	 * 
	 * @param amount
	 *            the amount of these ItemSets to add
	 * @return whenever the ItemSet was increased or not
	 */
	public boolean increase(int amount);

	/**
	 * Fills the ItemSet to the specified amount of these ItemSets. If by
	 * default there were 3 items here and now there are 2, filling it to 4 will
	 * add 4*3-2=10, making it 12 (4*3). It will not fill over the maximum
	 * limit. It never decreases the amount of any item.
	 * 
	 * @param amount
	 *            the amount of ItemSets to fill to
	 */
	public void fill(int amount);

	/**
	 * @return whenever applying this ItemSet should refill all player's items.
	 */
	public boolean refills();

}
