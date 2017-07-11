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

import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.core.Arena;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.LocationSet;

/**
 * Default implementation of an Arena.
 *
 * @author Jakub Sapalski
 */
public class DefaultArena implements Arena {
	
	private String id;
	private Map<String, LocationSet> locationSets = new HashMap<>();
	private boolean used = false;
	
	public DefaultArena(ConfigurationSection section) throws LoadingException {
		id = section.getName();
		for (String key : section.getKeys(false)) {
			LocationSet set;
			try {
				if (section.isList(key)) {
					set = new DefaultLocationSet(section.getStringList(key));
				} else {
					set = new DefaultLocationSet(section.getString(key));
				}
			} catch (LoadingException e) {
				throw (LoadingException) new LoadingException(
						String.format("Error loading '%s' location.", key)).initCause(e);
			}
			locationSets.put(key, set);
		}
	}
	
	@Override
	public String getID() {
		return id;
	}

	@Override
	public LocationSet getLocationSet(String name) throws LoadingException {
		LocationSet loc = locationSets.get(name);
		if (loc == null) {
			throw new LoadingException(String.format("Arena '%s' is missing '%s' location.", id, name));
		}
		return loc;
	}
	
	@Override
	public boolean isUsed() {
		return used;
	}
	
	@Override
	public void setUsed(boolean used) {
		this.used = used;
	}

}
