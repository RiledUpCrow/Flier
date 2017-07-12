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
import java.util.Map;

import pl.betoncraft.flier.api.content.Engine;
import pl.betoncraft.flier.api.content.Wings;

/**
 * The Kit stores and manages the items for the player.
 *
 * @author Jakub Sapalski
 */
public interface Kit {

	/**
	 * What happens when the player is respawned. CLEAR will remove all current
	 * items and give stored ones. COMBINE will apply all stored items on top of
	 * the current ones and NOTHING will do nothing.
	 */
	public enum RespawnAction {
		CLEAR, COMBINE, NOTHING,
	}

	public enum AddResult {
		ADDED, REMOVED, ALREADY_MAXED, ALREADY_EMPTIED, FILLED, REPLACED, SKIPPED
	}

	/**
	 * Returns the name of the class, as specified by the last applied ItemSet
	 * with a name. A Kit must have a class name to be used by the player.
	 * 
	 * @return the name of the class
	 */
	public String getClassName();

	/**
	 * Returns the Engine of the player.
	 * 
	 * @return the currently equipped engine
	 */
	public Engine getEngine();

	/**
	 * Removes the Engine completely from the current list of items.
	 * 
	 * @return whenever the engine was removed or was no engine
	 */
	public boolean removeEngine();

	/**
	 * Returns the Wings of the player (doesn't matter if they are on or not).
	 * 
	 * @return the currently equipped wings
	 */
	public Wings getWings();

	/**
	 * Removes the Wings completely from the current list of items.
	 * 
	 * @return whenever the wings were removed or there were no wings
	 */
	public boolean removeWings();

	/**
	 * @return the list of current item stacks
	 */
	public List<UsableItem> getItems();

	/**
	 * Removes one specified UsableItem from the inventory.
	 * 
	 * @param item
	 *            item to remove
	 * @return whenever the item was removed or there were no items of this type
	 */
	public boolean removeItem(UsableItem item);

	/**
	 * Performs the RespawnAction.
	 */
	public void onRespawn();

	/**
	 * Refills the current items to their default inner values (like fuel an
	 * ammunition).
	 */
	public void refill();

	/**
	 * Resets the Kit to default values.
	 */
	public void reset();

	/**
	 * Returns a map of current ItemSets, where the key is a category, for
	 * easier browsing.
	 * 
	 * @return the map of current ItemSets
	 */
	public Map<String, ItemSet> getCurrent();

	/**
	 * Applies the SetApplier to the current list.
	 * 
	 * @param set
	 *            SetApplier to apply.
	 * @return whenever the ItemSet was applied or not
	 */
	public AddResult addCurrent(SetApplier set);

	/**
	 * Returns a map of stored SetAppliers, where the key is a category, for
	 * easier browsing.
	 * 
	 * @return the map of stored ItemSets
	 */
	public Map<String, List<SetApplier>> getStored();

	/**
	 * Applies the SetApplier to the stored list.
	 * 
	 * @param set
	 *            SetApplier to apply
	 * @return whenever the ItemSet was applied or not
	 */
	public AddResult addStored(SetApplier set);

	/**
	 * Returns a map of default SetAppliers, where the key is a category, for
	 * easier browsing.
	 * 
	 * @return the map of default ItemSets
	 */
	public Map<String, List<SetApplier>> getDefault();

	/**
	 * Replicates the current state of this Kit.
	 * 
	 * @return the copy of this Kit
	 */
	public Kit replicate(InGamePlayer newOwner);

}