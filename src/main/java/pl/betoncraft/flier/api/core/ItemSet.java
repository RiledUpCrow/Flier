/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api.core;

import java.util.List;

import pl.betoncraft.flier.api.content.Engine;
import pl.betoncraft.flier.api.content.Wings;

/**
 * Represents a set of items which can be applied to PlayerClass.
 *
 * @author Jakub Sapalski
 */
public interface ItemSet {
	
	/**
	 * @return the ID of this ItemSet
	 */
	public String getID();

	/**
	 * Category is used to group the ItemSets together and handle adding new
	 * ItemSets.
	 *
	 * @return the category name of this ItemSet
	 */
	public String getCategory();

	/**
	 * @return the name of the class after applying this ItemSet
	 */
	public String getName();

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
	 * @return the map of UsableItems and their amounts in this ItemSet
	 */
	public List<UsableItem> getItems();

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

}
