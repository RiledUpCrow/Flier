/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.Flier;
import pl.betoncraft.flier.api.Engine;
import pl.betoncraft.flier.api.Item;
import pl.betoncraft.flier.api.PlayerClass;
import pl.betoncraft.flier.api.Wings;
import pl.betoncraft.flier.exception.LoadingException;
import pl.betoncraft.flier.exception.ObjectUndefinedException;
import pl.betoncraft.flier.exception.TypeUndefinedException;

/**
 * Default implementation of PlayerClass.
 *
 * @author Jakub Sapalski
 */
public class DefaultPlayerClass implements PlayerClass {

	private String currentName;
	private Engine currentEngine;
	private Wings currentWings;
	private Map<Item, Integer> currentItems = new HashMap<>();
	
	private String storedName;
	private Engine storedEngine;
	private Wings storedWings;
	private Map<Item, Integer> storedItems = new HashMap<>();

	private final String defaultName;
	private final Engine defaultEngine;
	private final Wings defaultWings;
	private final Map<Item, Integer> defaultItems = new HashMap<>();
	
	public DefaultPlayerClass(ConfigurationSection section) throws LoadingException {
		defaultName = Utils.capitalize(section.getString("name", "default"));
		String engineName = section.getString("engine");
		try {
			defaultEngine = Flier.getInstance().getEngine(engineName);
		} catch (ObjectUndefinedException | TypeUndefinedException | LoadingException e) {
			throw (LoadingException) new LoadingException(String.format("Error in '%s' engine.", engineName))
					.initCause(e);
		}
		String wingsName = section.getString("wings");
		try {
			defaultWings = Flier.getInstance().getWing(wingsName);
		} catch (ObjectUndefinedException | TypeUndefinedException | LoadingException e) {
			throw (LoadingException) new LoadingException(String.format("Error in '%s' wings.", wingsName))
					.initCause(e);
		}
		List<String> itemNames = section.getStringList("items");
		for (String item : itemNames) {
			int amount = 1;
			if (item.contains(" ")) {
				try {
					amount = Integer.parseInt(item.substring(item.indexOf(' ') + 1));
					item = item.substring(0, item.indexOf(' '));
				} catch (NumberFormatException e) {}
			}
			if (amount <= 0) {
				amount = 1;
			}
			try {
				defaultItems.put(Flier.getInstance().getItem(item), amount);
			} catch (ObjectUndefinedException | TypeUndefinedException | LoadingException e) {
				throw (LoadingException) new LoadingException(String.format("Error in '%s' item.", item))
						.initCause(e);
			}
		}
		reset();
	}

	private DefaultPlayerClass(String defName, Engine defEngine, Wings defWings, Map<Item, Integer> defItems) {
		defaultName = defName;
		defaultEngine = defEngine;
		defaultWings = defWings;
		defaultItems.putAll(defItems);
	}

	@Override
	public String getCurrentName() {
		return currentName;
	}

	@Override
	public Engine getCurrentEngine() {
		return currentEngine;
	}

	@Override
	public Wings getCurrentWings() {
		return currentWings;
	}

	@Override
	public Map<Item, Integer> getCurrentItems() {
		return currentItems;
	}

	@Override
	public void setCurrentName(String name) {
		currentName = name;
	}

	@Override
	public void setCurrentEngine(Engine engine) {
		currentEngine = engine;
	}

	@Override
	public void setCurrentWings(Wings wings) {
		currentWings = wings;
	}

	@Override
	public void setCurrentItems(Map<Item, Integer> items) {
		currentItems = items;
	}

	@Override
	public String getStoredName() {
		return storedName;
	}

	@Override
	public Engine getStoredEngine() {
		return storedEngine;
	}

	@Override
	public Wings getStoredWings() {
		return storedWings;
	}

	@Override
	public Map<Item, Integer> getStoredItems() {
		return new HashMap<>(storedItems);
	}

	@Override
	public void setStoredName(String name) {
		storedName = name;
	}

	@Override
	public void setStoredEngine(Engine engine) {
		storedEngine = engine;
	}

	@Override
	public void setStoredWings(Wings wings) {
		storedWings = wings;
	}

	@Override
	public void setStoredItems(Map<Item, Integer> items) {
		storedItems = new HashMap<>(items);
	}

	@Override
	public String getDefaultName() {
		return defaultName;
	}

	@Override
	public Engine getDefaultEngine() {
		return defaultEngine;
	}

	@Override
	public Wings getDefaultWings() {
		return defaultWings;
	}

	@Override
	public Map<Item, Integer> getDefaultItems() {
		return new HashMap<>(defaultItems);
	}

	@Override
	public void save() {
		// stored
		storedName = getCurrentName();
		storedEngine = getCurrentEngine();
		storedWings = getCurrentWings();
		storedItems = getCurrentItems();
	}

	@Override
	public void load() {
		// current
		currentName = getStoredName();
		currentEngine = getStoredEngine();
		currentWings = getStoredWings();
		currentItems = getStoredItems();
	}

	@Override
	public void reset() {
		// stored
		storedName = getDefaultName();
		storedEngine = getDefaultEngine();
		storedWings = getDefaultWings();
		storedItems = getDefaultItems();
		// current
		currentName = getDefaultName();
		currentEngine = getDefaultEngine();
		currentWings = getDefaultWings();
		currentItems = getDefaultItems();
	}
	
	@Override
	public DefaultPlayerClass clone() {
		DefaultPlayerClass pc = new DefaultPlayerClass(defaultName, defaultEngine, defaultWings, defaultItems);
		pc.setStoredName(storedName);
		pc.setStoredEngine(getStoredEngine());
		pc.setStoredWings(getStoredWings());
		pc.setStoredItems(getStoredItems());
		pc.setCurrentName(getCurrentName());
		pc.setCurrentEngine(getCurrentEngine());
		pc.setCurrentWings(getCurrentWings());
		pc.setCurrentItems(new HashMap<>(getCurrentItems()));
		return pc;
	}

}
