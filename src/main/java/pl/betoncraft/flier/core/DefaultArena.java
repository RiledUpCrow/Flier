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

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.core.Arena;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.LocationSet;
import pl.betoncraft.flier.util.LangManager;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * Default implementation of an Arena.
 *
 * @author Jakub Sapalski
 */
public class DefaultArena implements Arena {
	
	private String id;
	private String name;
	private Map<String, LocationSet> locationSets = new HashMap<>();
	private boolean used = false;
	
	public DefaultArena(ConfigurationSection section) throws LoadingException {
		id = section.getName();
		ValueLoader loader = new ValueLoader(section);
		name = loader.loadString("name", id);
		ConfigurationSection locations = section.getConfigurationSection("locations");
		if (locations == null || locations.getKeys(false).isEmpty()) {
			throw new LoadingException("Locations must be specified.");
		}
		for (String key : locations.getKeys(false)) {
			LocationSet set;
			try {
				if (locations.isList(key)) {
					set = new DefaultLocationSet(locations.getStringList(key));
				} else {
					set = new DefaultLocationSet(locations.getString(key));
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
	public String getName(CommandSender player) {
		return name.startsWith("$") ? LangManager.getMessage(player, name.substring(1)) : name;
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
