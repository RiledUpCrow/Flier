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
