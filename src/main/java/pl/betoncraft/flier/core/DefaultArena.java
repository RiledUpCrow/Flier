/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
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
