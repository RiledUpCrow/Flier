/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Engine;
import pl.betoncraft.flier.api.content.Wings;
import pl.betoncraft.flier.api.core.ItemSet;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.Modification;
import pl.betoncraft.flier.api.core.UsableItem;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * A default implementation of ItemSet.
 *
 * @author Jakub Sapalski
 */
public class DefaultSet implements ItemSet {
	
	protected ValueLoader loader;

	protected String id;
	protected String category;
	protected String name;
	protected Engine engine;
	protected Wings wings;
	protected List<UsableItem> items = new ArrayList<>();
	protected List<Modification> mods = new ArrayList<>();

	public DefaultSet(ConfigurationSection section) throws LoadingException {
		Flier flier = Flier.getInstance();
		id = section.getName();
		loader = new ValueLoader(section);
		category = loader.loadString("category");
		name = section.getString("name");
		String engineName = section.getString("engine");
		if (engineName == null) {
			engine = null;
		} else {
			engine = flier.getEngine(engineName);
		}
		String wingsName = section.getString("wings");
		if (wingsName == null) {
			wings = null;
		} else {
			wings = flier.getWing(wingsName);
		}
		int index = 0;
		try {
			List<Map<?, ?>> maps = section.getMapList("items");
			for (Map<?, ?> map : maps) {
				index++;
				UsableItem item;
				int amount = 1, max = 0, min = 0;
				Object itemObject = map.get("item");
				if (itemObject == null || !(itemObject instanceof String)) {
					throw new LoadingException("Item name is missing.");
				}
				item = flier.getItem((String) itemObject);
				Object amountObject = map.get("amount");
				if (amountObject != null) {
					if (!(amountObject instanceof Integer)) {
						throw new LoadingException("Item amount must be an integer.");
					} else {
						amount = (Integer) amountObject;
					}
				}
				Object maxObject = map.get("max");
				if (maxObject != null) {
					if (!(maxObject instanceof Integer)) {
						throw new LoadingException("Maximum item amount must be an integer.");
					} else {
						max = (Integer) maxObject;
					}
				}
				Object minObject = map.get("min");
				if (minObject != null) {
					if (!(minObject instanceof Integer)) {
						throw new LoadingException("Minimum item amount must be an integer.");
					} else {
						min = (Integer) minObject;
					}
				}
				item.setDefaultAmounts(amount, max, min);
				items.add(item);
			}
		} catch (LoadingException e) {
			throw (LoadingException) new LoadingException(String.format("Error in %s item.", index)).initCause(e);
		}
		for (String modName : section.getStringList("modifications")) {
			mods.add(flier.getModification(modName));
		}
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public String getCategory() {
		return category;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Engine getEngine() {
		return engine;
	}
	
	@Override
	public void setEngine(Engine engine) {
		this.engine = engine;
	}

	@Override
	public Wings getWings() {
		return wings;
	}
	
	@Override
	public void setWings(Wings wings) {
		this.wings = wings;
	}

	@Override
	public List<UsableItem> getItems() {
		return items;
	}
	
	@Override
	public List<Modification> getModifications() {
		return mods;
	}
	
	@Override
	public boolean isEmpty() {
		return engine == null && wings == null && items.stream().allMatch(item -> item.getAmount() == 0) && mods.isEmpty();
	}

	@Override
	public boolean increase(int amount) {
		for (UsableItem item : items) {
			int newAmount = item.getAmount() + (item.getDefaultAmount() * amount);
			if (newAmount < 0 || newAmount < item.getMin() || newAmount > item.getMax()) {
				return false;
			}
		}
		for (UsableItem item : items) {
			item.setAmount(item.getAmount() + (item.getDefaultAmount() * amount));
		}
		return true;
	}
	
	@Override
	public void fill(int amount) {
		for (UsableItem item : items) {
			int fullAmount = item.getDefaultAmount() * amount;
			if (item.getAmount() < fullAmount) {
				// if filling is over the limit, set it to the limit
				if (!item.setAmount(item.getDefaultAmount() * amount)) {
					item.setAmount(item.getMax());
				}
			}
		}
	}

	@Override
	public int getAmount() {
		int amount = -1;
		for (UsableItem item : items) {
			int a = item.getAmount();
			int d = item.getDefaultAmount();
			int r = (a - (a % d)) / d;
			if (amount == -1) {
				amount = r;
			} else {
				if (r < amount) {
					amount = r;
				}
			}
		}
		return amount;
	}
}
