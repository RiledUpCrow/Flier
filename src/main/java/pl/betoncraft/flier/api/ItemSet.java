/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api;

import java.util.Map;

/**
 * Represents a set of items which can be applied to PlayerClass.
 *
 * @author Jakub Sapalski
 */
public interface ItemSet {

	public enum AddType {
		RESET, CLEAR, REPLACE, ADD, TAKE // TODO add "SINGLE" for a single item
	}

	/**
	 * Tries to apply this ItemSet to the PlayerClass by setting items in a
	 * defined way. Returns true if it successfully applied all items, false if
	 * it wasn't possible.
	 * 
	 * @param clazz
	 *            PlayerClass to apply items to
	 * @return whenever applying was successful
	 */
	public boolean apply(PlayerClass clazz);

	public Engine getEngine();

	public Wings getWings();

	public Map<UsableItem, Integer> getItems();

	public AddType getType();

}
