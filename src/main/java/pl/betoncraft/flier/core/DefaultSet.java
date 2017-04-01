/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core;

import java.util.ArrayList;
import java.util.List;

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
		List<String> itemNames = section.getStringList("items");
		for (String itemName : itemNames) {
			items.add(flier.getItem(itemName));
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
			int newAmount = item.getAmount() + (item.getDefAmount() * amount);
			if (newAmount < 0 || newAmount < item.getMinAmount() || newAmount > item.getMaxAmount()) {
				return false;
			}
		}
		for (UsableItem item : items) {
			item.setAmount(item.getAmount() + (item.getDefAmount() * amount));
		}
		return true;
	}
	
	@Override
	public void fill(int amount) {
		for (UsableItem item : items) {
			int fullAmount = item.getDefAmount() * amount;
			if (item.getAmount() < fullAmount) {
				// if filling is over the limit, set it to the limit
				if (!item.setAmount(item.getDefAmount() * amount)) {
					item.setAmount(item.getMaxAmount());
				}
			}
		}
	}

	@Override
	public int getAmount() {
		int amount = -1;
		for (UsableItem item : items) {
			int a = item.getAmount();
			int d = item.getDefAmount();
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
