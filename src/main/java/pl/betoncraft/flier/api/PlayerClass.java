/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api;

import java.util.Map;

/**
 * Represents a class, which is basically a container for an engine, wings and
 * items.
 * 
 * The class has 3 separate sets of these items: current, stored and default.
 * 
 * Current are the ones currently on the player. It reflects their inventory and
 * is modified on use.
 * 
 * Stored are items with which the player has entered the game. It is loaded
 * into Current items with `load()` method, should you restore these items. You
 * can also save Current items into Stored with `save()`.
 * 
 * Default is the one with which the PlayerClass is created. It should not
 * change. To change it, create a new PlayerClass object. These can be reset
 * into Current and Stored with `reset()` method.
 *
 * @author Jakub Sapalski
 */
public interface PlayerClass extends Replicable {

	/**
	 * Represents what happens with items when the player respawns.
	 *
	 * @author Jakub Sapalski
	 */
	public enum RespawnAction {
		SAVE, LOAD, RESET, NOTHING
	}

	/**
	 * @return the current name of this class
	 */
	public String getCurrentName();

	/**
	 * @return engine currently on the player or null if he doesn't have one
	 */
	public Engine getCurrentEngine();

	/**
	 * @return wings currently on the player or null if he doesn't have the
	 *         wings
	 */
	public Wings getCurrentWings();

	/**
	 * @return mutable map of items and their amounts; the map can be empty if
	 *         the player doesn't have any items currently
	 */
	public Map<UsableItem, Integer> getCurrentItems();

	/**
	 * Sets the current name of this class.
	 * 
	 * @param name
	 *            string to set as current name
	 */
	public void setCurrentName(String name);

	/**
	 * Sets the current engine. It does not modify stored engine.
	 * 
	 * @param engine
	 *            Engine to set as the current one
	 */
	public void setCurrentEngine(Engine engine);

	/**
	 * Sets the current wings. It does not modify stored wings.
	 * 
	 * @param wings
	 *            Wings to set as the current ones
	 */
	public void setCurrentWings(Wings wings);

	/**
	 * Sets the current items. It does not modify stored items. The map is not
	 * copied, so all changes apply.
	 * 
	 * @param items
	 *            map of UsableItems and their amounts to set as the current
	 *            ones
	 */
	public void setCurrentItems(Map<UsableItem, Integer> items);

	/**
	 * @return the stored name of this class
	 */
	public String getStoredName();

	/**
	 * @return the stored engine or null if the player doesn't have it
	 */
	public Engine getStoredEngine();

	/**
	 * @return the stored wings or null if the player doesn't have them
	 */
	public Wings getStoredWings();

	/**
	 * @return an immutable map of stored items; the map can be empty if the
	 *         player doesn't have any stored items
	 */
	public Map<UsableItem, Integer> getStoredItems();

	/**
	 * Sets the stored name of this class.
	 * 
	 * @param name
	 *            string to set as stored name
	 */
	public void setStoredName(String name);

	/**
	 * Sets the stored engine. It's a shortcut for setting current engine and
	 * saving.
	 * 
	 * @param engine
	 *            the Engine to store
	 */
	public void setStoredEngine(Engine engine);

	/**
	 * Sets the stored wings. It's a shortcut for setting current wings and
	 * saving.
	 * 
	 * @param wings
	 *            the Wings to store
	 */
	public void setStoredWings(Wings wings);

	/**
	 * Sets the stored items. It's a shortcut for setting current items and
	 * saving. The map is copies, so later changes won't be applied.
	 * 
	 * @param items
	 *            the map of UsableItems and their amounts to store
	 */
	public void setStoredItems(Map<UsableItem, Integer> items);

	/**
	 * @return the default name of this class
	 */
	public String getDefaultName();

	/**
	 * @return the default, unmodifiable engine
	 */
	public Engine getDefaultEngine();

	/**
	 * @return the default, unmodifiable wings
	 */
	public Wings getDefaultWings();

	/**
	 * @return the copy of default items
	 */
	public Map<UsableItem, Integer> getDefaultItems();

	/**
	 * Saves all current items into stored items.
	 */
	public void save();

	/**
	 * Loads all stored items into current items.
	 */
	public void load();

	/**
	 * Resets default items into stored and current items.
	 */
	public void reset();

}
