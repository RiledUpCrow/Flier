/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.core.Arena;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * Default implementation of an Arena.
 *
 * @author Jakub Sapalski
 */
public class DefaultArena implements Arena {
	
	private String id;
	private Map<String, Location> locations = new HashMap<>();
	private boolean used = false;
	
	public DefaultArena(ConfigurationSection section) throws LoadingException {
		id = section.getName();
		ValueLoader loader = new ValueLoader(section);
		for (String key : section.getKeys(false)) {
			locations.put(key, loader.loadLocation(key));
		}
	}
	
	@Override
	public String getID() {
		return id;
	}

	@Override
	public Location getLocation(String name) throws LoadingException {
		Location loc = locations.get(name);
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
