/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.Flier;
import pl.betoncraft.flier.api.Engine;
import pl.betoncraft.flier.api.Item;
import pl.betoncraft.flier.api.ItemSet;
import pl.betoncraft.flier.api.PlayerClass;
import pl.betoncraft.flier.api.Wings;
import pl.betoncraft.flier.exception.LoadingException;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * A default implementation of ItemSet.
 *
 * @author Jakub Sapalski
 */
public class DefaultSet implements ItemSet {

	protected final AddType addType;
	protected String name;
	protected Engine engine;
	protected Wings wings;
	protected Map<Item, Integer> items = new HashMap<>();
	protected boolean saving;

	public DefaultSet(ConfigurationSection section) throws LoadingException {
		addType = ValueLoader.loadEnum(section, "type", AddType.class);
		if (addType == AddType.RESET) {
			return;
		}
		name = section.getString("name");
		saving = section.getBoolean("saving", true);
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
				items.put(Flier.getInstance().getItem(item), amount);
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
		c.setCurrentEngine(engine);
		if (saving) {
			c.setStoredEngine(engine);
		}
	}

	private void setWings(PlayerClass c, Wings wings) {
		c.setCurrentWings(wings);
		if (saving) {
			c.setStoredWings(wings);
		}
	}

	private void setItems(PlayerClass c, Map<Item, Integer> items) {
		c.setCurrentItems(items);
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
			Map<Item, Integer> storedItems1 = c.getCurrentItems();
			for (Iterator<Entry<Item, Integer>> i = items.entrySet().iterator(); i.hasNext();) {
				Entry<Item, Integer> e = i.next();
				for (Iterator<Entry<Item, Integer>> si = storedItems1.entrySet().iterator(); si.hasNext();) {
					Entry<Item, Integer> se = si.next();
					if (e.getKey().slot() == se.getKey().slot()) {
						si.remove();
					}
				}
				storedItems1.put(e.getKey(), e.getValue());
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
			Map<Item, Integer> storedItems2 = new HashMap<>(c.getCurrentItems());
			loop: for (Iterator<Entry<Item, Integer>> i = items.entrySet().iterator(); i.hasNext();) {
				Entry<Item, Integer> e = i.next();
				for (Iterator<Entry<Item, Integer>> si = storedItems2.entrySet().iterator(); si.hasNext();) {
					Entry<Item, Integer> se = si.next();
					if (e.getKey().slot() == se.getKey().slot()) {
						if (e.getKey().isSameAs(se.getKey())) {
							se.setValue(se.getValue() + e.getValue());
							continue loop;
						} else {
							return false;
						}
					}
				}
				storedItems2.put(e.getKey(), e.getValue());
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
			Map<Item, Integer> storedItems3 = c.getCurrentItems();
			for (Iterator<Entry<Item, Integer>> i = items.entrySet().iterator(); i.hasNext();) {
				Entry<Item, Integer> e = i.next();
				int oldAmount = 0;
				for (Iterator<Entry<Item, Integer>> si = storedItems3.entrySet().iterator(); si.hasNext();) {
					Entry<Item, Integer> se = si.next();
					if (e.getKey().slot() == se.getKey().slot()) {
						si.remove();
						if (e.getKey().equals(se.getKey())) {
							oldAmount = se.getValue();
							break;
						}
					}
				}
				int newAmount = oldAmount - e.getValue();
				if (newAmount > 0) {
					storedItems3.put(e.getKey(), newAmount);
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
		return engine;
	}

	@Override
	public Wings getWings() {
		return wings;
	}

	@Override
	public Map<Item, Integer> getItems() {
		return items;
	}

	@Override
	public AddType getType() {
		return addType;
	}

}
