/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api.core;

import org.bukkit.Location;

/**
 * Contains Locations for Games.
 *
 * @author Jakub Sapalski
 */
public interface Arena {

	/**
	 * @return the ID of the Arena
	 */
	public String getID();

	
	/**
	 * @param name name of the Location to get
	 * @return the Location, never null
	 * @throws when it can't load this Location
	 */
	public Location getLocation(String name) throws LoadingException;
	
	/**
	 * @return whenever this Arena is currently used
	 */
	public boolean isUsed();
	
	/**
	 * @param used whenever this Arena is currently used
	 */
	public void setUsed(boolean used);
}
