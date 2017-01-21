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
import java.util.stream.Collectors;

import org.bukkit.configuration.ConfigurationSection;

import javafx.util.Pair;
import pl.betoncraft.flier.Flier;
import pl.betoncraft.flier.api.Engine;
import pl.betoncraft.flier.api.Item;
import pl.betoncraft.flier.api.PlayerClass;
import pl.betoncraft.flier.api.Wings;
import pl.betoncraft.flier.exception.LoadingException;

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
		defaultName = Utils.capitalize(ValueLoader.loadString(section, "name"));
		defaultEngine = ValueLoader.loadEngine(section, "engine");
		defaultWings = ValueLoader.loadWings(section, "wings");
		List<String> itemNames = section.getStringList("items");
		for (String item : itemNames) {
			item = item.trim();
			int amount = 1;
			if (item.contains(" ")) {
				String[] parts = item.split(" ");
				if (parts.length != 2) {
					throw new LoadingException(String.format("Item format in '%s' is incorrect.", item));
				}
				try {
					amount = Integer.parseInt(parts[0]);
				} catch (NumberFormatException e) {
					throw new LoadingException(String.format("Cannot parse item amount in '%s'.", item));
				}
				item = parts[1];
			}
			if (amount <= 0) {
				throw new LoadingException(String.format("Item amount in '%s' must be positive.", item));
			}
			try {
				defaultItems.put(Flier.getInstance().getItem(item), amount);
			} catch (LoadingException e) {
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
		reset();
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
		return (Engine) storedEngine.replicate();
	}

	@Override
	public Wings getStoredWings() {
		return (Wings) storedWings.replicate();
	}

	@Override
	public Map<Item, Integer> getStoredItems() {
		return storedItems.entrySet().stream().map(
				entry -> new Pair<>((Item) entry.getKey().replicate(), entry.getValue())
			).collect(Collectors.toMap(pair -> pair.getKey(), pair -> pair.getValue()));
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
		return (Engine) defaultEngine.replicate();
	}

	@Override
	public Wings getDefaultWings() {
		return (Wings) defaultWings.replicate();
	}

	@Override
	public Map<Item, Integer> getDefaultItems() {
		return defaultItems.entrySet().stream().map(
					entry -> new Pair<>((Item) entry.getKey().replicate(), entry.getValue())
				).collect(Collectors.toMap(pair -> pair.getKey(), pair -> pair.getValue()));
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
	public DefaultPlayerClass replicate() {
		return new DefaultPlayerClass(defaultName, defaultEngine, defaultWings, defaultItems);
	}

}
