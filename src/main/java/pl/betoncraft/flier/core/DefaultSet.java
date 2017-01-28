/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.Flier;
import pl.betoncraft.flier.api.Engine;
import pl.betoncraft.flier.api.ItemSet;
import pl.betoncraft.flier.api.PlayerClass;
import pl.betoncraft.flier.api.UsableItem;
import pl.betoncraft.flier.api.Wings;
import pl.betoncraft.flier.exception.LoadingException;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * A default implementation of ItemSet.
 *
 * @author Jakub Sapalski
 */
public class DefaultSet implements ItemSet {
	
	protected ValueLoader loader;

	protected final AddType addType;
	protected String name;
	protected Engine engine;
	protected Wings wings;
	protected List<Items> items = new ArrayList<>();
	protected boolean saving;
	
	private class Items {
		private UsableItem item;
		private int amount = 1;
		private int max = 0;
	}

	public DefaultSet(ConfigurationSection section) throws LoadingException {
		loader = new ValueLoader(section);
		addType = loader.loadEnum("type", AddType.class);
		if (addType == AddType.RESET) {
			return;
		}
		name = section.getString("name");
		saving = loader.loadBoolean("saving", true);
		String engineName = section.getString("engine");
		if (engineName == null) {
			engine = null;
		} else {
			engine = Flier.getInstance().getEngine(engineName);
		}
		String wingsName = section.getString("wings");
		if (wingsName == null) {
			wings = null;
		} else {
			wings = Flier.getInstance().getWing(wingsName);
		}
		try {
			List<Map<?, ?>> maps = section.getMapList("items");
			for (Map<?, ?> map : maps) {
				Items items = new Items();
				Object itemObject = map.get("item");
				if (itemObject == null || !(itemObject instanceof String)) {
					throw new LoadingException("Item name is missing.");
				}
				items.item = Flier.getInstance().getItem((String) itemObject);
				Object amountObject = map.get("amount");
				if (amountObject != null) {
					if (!(amountObject instanceof Integer)) {
						throw new LoadingException("Item amount must be an integer.");
					} else {
						items.amount = (Integer) amountObject;
					}
				}
				Object maxObject = map.get("max");
				if (maxObject != null) {
					if (!(maxObject instanceof Integer)) {
						throw new LoadingException("Maximum item amount must be an integer.");
					} else {
						items.max = (Integer) maxObject;
					}
				}
				this.items.add(items);
			}
		} catch (LoadingException e) {
			throw (LoadingException) new LoadingException("Error in items.").initCause(e);
		}
	}

	private void setName(PlayerClass c, String name) {
		c.setCurrentName(name);
		if (saving) {
			c.setStoredName(name);
		}
	}

	private void setEngine(PlayerClass c, Engine engine) {
		c.setCurrentEngine((Engine) engine.replicate());
		if (saving) {
			c.setStoredEngine(engine);
		}
	}

	private void setWings(PlayerClass c, Wings wings) {
		c.setCurrentWings((Wings) wings.replicate());
		if (saving) {
			c.setStoredWings(wings);
		}
	}

	private void setItems(PlayerClass c, List<Items> items) {
		c.setCurrentItems(itemsToMap(items));
		if (saving) {
			c.setStoredItems(itemsToMap(items));
		}
	}
	
	private void setItems(PlayerClass c, Map<UsableItem, Integer> items) {
		c.setCurrentItems(items.entrySet().stream().collect(Collectors.toMap(
				e -> (UsableItem) e.getKey(), e -> e.getValue()
		)));
		if (saving) {
			c.setStoredItems(items);
		}
	}

	@Override
	public boolean apply(PlayerClass c) {
		if (name != null) {
			setName(c, name);
		}
		switch (addType) {
		case RESET: // reset all items to default class
			c.reset();
			break;
		case CLEAR: // set inventory to these items only
			setEngine(c, engine);
			setWings(c, wings);
			setItems(c, items);
			break;
		case REPLACE: // replace these items, don't touch others
			if (engine != null) {
				setEngine(c, engine);
			}
			if (wings != null) {
				setWings(c, wings);
			}
			Map<UsableItem, Integer> storedItems1 = c.getCurrentItems();
			for (Iterator<Items> i = items.iterator(); i.hasNext();) {
				Items e = i.next();
				for (Iterator<Entry<UsableItem, Integer>> si = storedItems1.entrySet().iterator(); si.hasNext();) {
					Entry<UsableItem, Integer> se = si.next();
					if (e.item.slot() == se.getKey().slot()) {
						si.remove();
					}
				}
				storedItems1.put(e.item, e.amount);
			}
			setItems(c, storedItems1);
			break;
		case ADD: // add items to existing ones
			if (engine != null) {
				if (c.getCurrentEngine() != null) {
					return false;
				}
				setEngine(c, engine);
			}
			if (wings != null) {
				if (c.getCurrentWings() != null) {
					return false;
				}
				setWings(c, wings);
			}
			Map<UsableItem, Integer> storedItems2 = new HashMap<>(c.getCurrentItems());
			loop: for (Iterator<Items> i = items.iterator(); i.hasNext();) {
				Items e = i.next();
				for (Iterator<Entry<UsableItem, Integer>> si = storedItems2.entrySet().iterator(); si.hasNext();) {
					Entry<UsableItem, Integer> se = si.next();
					if (e.item.slot() == se.getKey().slot()) {
						if (e.item.isSameAs(se.getKey()) && (e.max <= 0 || se.getValue() + e.amount <= e.max)) {
							se.setValue(se.getValue() + e.amount);
							continue loop;
						} else {
							return false;
						}
					}
				}
				storedItems2.put(e.item, e.amount);
			}
			setItems(c, storedItems2);
			break;
		case TAKE: // take items from existing ones
			if (engine != null && engine.equals(c.getCurrentEngine())) {
				setEngine(c, null);
			}
			if (wings != null && wings.equals(c.getCurrentWings())) {
				setWings(c, null);
			}
			Map<UsableItem, Integer> storedItems3 = c.getCurrentItems();
			for (Iterator<Items> i = items.iterator(); i.hasNext();) {
				Items e = i.next();
				int oldAmount = 0;
				for (Iterator<Entry<UsableItem, Integer>> si = storedItems3.entrySet().iterator(); si.hasNext();) {
					Entry<UsableItem, Integer> se = si.next();
					if (e.item.slot() == se.getKey().slot()) {
						si.remove();
						if (e.item.equals(se.getKey())) {
							oldAmount = se.getValue();
							break;
						}
					}
				}
				int newAmount = oldAmount - e.amount;
				if (newAmount > 0) {
					storedItems3.put(e.item, newAmount);
				} else if (newAmount < 0) {
					return false;
				}
			}
			setItems(c, storedItems3);
			break;
		}
		return true;
	}

	@Override
	public Engine getEngine() {
		return (Engine) engine.replicate();
	}

	@Override
	public Wings getWings() {
		return (Wings) wings.replicate();
	}

	@Override
	public Map<UsableItem, Integer> getItems() {
		return itemsToMap(items);
	}

	@Override
	public AddType getType() {
		return addType;
	}
	
	private Map<UsableItem, Integer> itemsToMap(List<Items> list) {
		return list.stream().collect(Collectors.toMap(i -> (UsableItem) i.item.replicate(), i -> i.amount));
	}

}
