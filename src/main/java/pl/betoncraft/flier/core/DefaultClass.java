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

import javafx.util.Pair;
import pl.betoncraft.flier.api.Engine;
import pl.betoncraft.flier.api.ItemSet;
import pl.betoncraft.flier.api.LoadingException;
import pl.betoncraft.flier.api.PlayerClass;
import pl.betoncraft.flier.api.UsableItem;
import pl.betoncraft.flier.api.Wings;

/**
 * Default implementation of PlayerClass.
 *
 * @author Jakub Sapalski
 */
public class DefaultClass implements PlayerClass {

	private String currentName;
	private Engine currentEngine;
	private Wings currentWings;
	private Map<UsableItem, Integer> currentItems = new HashMap<>();
	
	private String storedName;
	private Engine storedEngine;
	private Wings storedWings;
	private Map<UsableItem, Integer> storedItems = new HashMap<>();

	private final String defaultName;
	private final Engine defaultEngine;
	private final Wings defaultWings;
	private final Map<UsableItem, Integer> defaultItems;
	
	public DefaultClass(List<ItemSet> sets) throws LoadingException {
		for (ItemSet set : sets) {
			if (set == null) {
				throw new LoadingException("One of the item sets is not defined.");
			}
			set.apply(this);
		}
		if (currentName == null) {
			throw new LoadingException("Name is not specified.");
		}
		defaultName = currentName;
		defaultEngine = currentEngine;
		defaultWings = currentWings;
		defaultItems = currentItems;
		reset();
	}

	private DefaultClass(String defName, Engine defEngine, Wings defWings, Map<UsableItem, Integer> defItems) {
		defaultName = defName;
		defaultEngine = defEngine;
		defaultWings = defWings;
		defaultItems = defItems;
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
	public Map<UsableItem, Integer> getCurrentItems() {
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
	public void setCurrentItems(Map<UsableItem, Integer> items) {
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
	public Map<UsableItem, Integer> getStoredItems() {
		return storedItems.entrySet().stream().map(
				entry -> new Pair<>((UsableItem) entry.getKey().replicate(), entry.getValue())
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
	public void setStoredItems(Map<UsableItem, Integer> items) {
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
	public Map<UsableItem, Integer> getDefaultItems() {
		return defaultItems.entrySet().stream().collect(Collectors.toMap(
				entry -> (UsableItem) entry.getKey().replicate(), entry -> entry.getValue()
		));
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
	public DefaultClass replicate() {
		return new DefaultClass(getDefaultName(), getDefaultEngine(), getDefaultWings(), getDefaultItems());
	}

}
