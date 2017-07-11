/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core;

import java.util.List;

import org.bukkit.Location;

import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.LocationSet;
import pl.betoncraft.flier.util.Utils;

/**
 * Default implementation of a LocationSet.
 *
 * @author Jakub Sapalski
 */
public class DefaultLocationSet implements LocationSet {
	
	private final Location[] locs;
	
	public DefaultLocationSet(String location) throws LoadingException {
		locs = new Location[1];
		locs[0] = Utils.parseLocation(location);
	}
	
	public DefaultLocationSet(List<String> locations) throws LoadingException {
		locs = new Location[locations.size()];
		int i = 0;
		for (String location : locations) {
			try {
				locs[i] = Utils.parseLocation(location);
			} catch (LoadingException e) {
				throw (LoadingException) new LoadingException(
						String.format("Error in location %d.", i)).initCause(e);
			}
			i++;
		}
	}

	@Override
	public Location getSingle() {
		return locs[0].clone();
	}

	@Override
	public Location[] getMultiple() {
		Location[] newLocs = new Location[locs.length];
		for (int i = 0; i < locs.length; i++) {
			newLocs[i] = locs[i].clone();
		}
		return newLocs;
	}

}
