/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api.core;

import org.bukkit.Location;

/**
 * Holds locations and serves them singly or in arrays.
 *
 * @author Jakub Sapalski
 */
public interface LocationSet {
	
	/**
	 * @return a single (first) Location from this LocationSet
	 */
	public Location getSingle();
	
	/**
	 * @return an array containing all Locations in this LocationSet
	 */
	public Location[] getMultiple();

}
