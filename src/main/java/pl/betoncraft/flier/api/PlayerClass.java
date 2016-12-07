/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api;

import java.util.Map;

/**
 * Represents a class, which is basically a container for an engine, wings and items.
 *
 * @author Jakub Sapalski
 */
public interface PlayerClass {

	/**
	 * @return engine stored in this class or null, if the class doesn't have
	 *         any engine
	 */
	public Engine getEngine();

	/**
	 * @return map of items and their amounts; the map can be empty if the class
	 *         doesn't have any items
	 */
	public Map<UsableItem, Integer> getItems();

	/**
	 * @return wings stored in this class or null if the class doesn't have any
	 *         wings
	 */
	public Wings getWings();

	/**
	 * @return the name of this class
	 */
	public String getName();
}
