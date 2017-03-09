/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import pl.betoncraft.flier.api.content.Engine;
import pl.betoncraft.flier.api.content.Wings;
import pl.betoncraft.flier.api.core.ItemSet;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.PlayerClass;
import pl.betoncraft.flier.api.core.SetApplier;
import pl.betoncraft.flier.api.core.UsableItem;

/**
 * Default implementation of PlayerClass.
 *
 * @author Jakub Sapalski
 */
public class DefaultClass implements PlayerClass {
	
	private final RespawnAction respawnAction;
	
	private Compiled compiled;

	private final Map<String, ItemSet> current = new HashMap<>();
	private final Map<String, List<SetApplier>> stored = new HashMap<>();
	private final Map<String, List<SetApplier>> def;
	
	public DefaultClass(List<String> sets, RespawnAction respAct) throws LoadingException {
		respawnAction = respAct;
		Map<String, List<SetApplier>> map = new HashMap<>(sets.size());
		for (String set : sets) {
			SetApplier applier = new DefaultSetApplier(set);
			map.computeIfAbsent(applier.getCategory(), k -> new ArrayList<>()).add(applier);
		}
		def = Collections.unmodifiableMap(map);
		reset();
		load();
	}
	
	private DefaultClass(Map<String, List<SetApplier>> map, RespawnAction respawnAction) {
		this.respawnAction = respawnAction;
		def = Collections.unmodifiableMap(map);
		reset();
		load();
	}
	
	private class Compiled {

		private String name;
		private Engine engine;
		private Wings wings;
		private List<UsableItem> items = new ArrayList<>();

		private Compiled(Collection<ItemSet> sets) {
			for (ItemSet set : sets) {
				if (set.getName() != null) {
					name = set.getName();
				}
				// don't override existing stuff with nulls!
				engine = set.getEngine() == null ? engine : set.getEngine();
				wings = set.getWings() == null ? wings : set.getWings();
				List<UsableItem> items = set.getItems();
				loop: for (UsableItem newItem : items) {
					for (UsableItem existingItem : this.items) {
						if (existingItem.isSimilar(newItem)) {
							existingItem.setAmount(existingItem.getAmount() + newItem.getAmount());
							continue loop;
						}
					}
					this.items.add(newItem);
				}
			}
			// apply modifications
			sets.forEach(set -> set.getModifications().forEach(mod -> {
				switch (mod.getTarget()) {
				case ENGINE:
					if (mod.getNames().contains(engine.getID())) {
						engine.addModification(mod);
					}
					break;
				case WINGS:
					if (mod.getNames().contains(wings.getID())) {
						wings.addModification(mod);
					}
					break;
				case USABLE_ITEM:
					items.stream()
							.filter(item -> mod.getNames().contains(item.getID()))
							.forEach(item -> item.addModification(mod));
				case ACTION:
				case ACTIVATOR:
					items.forEach(item -> item.addModification(mod));
					break;
				}
			}));
		}
		
		public Engine getEngine() {
			return engine;
		}
		
		public Wings getWings() {
			return wings;
		}
		
		public List<UsableItem> getItems() {
			return items;
		}

	}
	
	private void load() {
		current.clear();
		stored.values().forEach(list -> list.forEach(applier -> addCurrent(applier)));
	}
	
	private void compile() {
		compiled = new Compiled(current.values());
	}
	
	@Override
	public void reset() {
		stored.putAll(getDefault());
	}

	@Override
	public String getName() {
		return compiled.name;
	}
	
	@Override
	public Engine getEngine() {
		return compiled.getEngine();
	}
	
	@Override
	public boolean removeEngine() {
		Engine e = compiled.engine;
		if (e != null) {
			compiled.engine = null;
			for (Iterator<ItemSet> i = current.values().iterator(); i.hasNext();) {
				ItemSet set = i.next();
				set.setEngine(null);
				if (set.isEmpty()) {
					i.remove();
				}
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Wings getWings() {
		return compiled.getWings();
	}
	
	@Override
	public boolean removeWings() {
		Wings w = compiled.wings;
		if (w != null) {
			compiled.wings = null;
			for (Iterator<ItemSet> i = current.values().iterator(); i.hasNext();) {
				ItemSet set = i.next();
				set.setWings(null);
				if (set.isEmpty()) {
					i.remove();
				}
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<UsableItem> getItems() {
		return Collections.unmodifiableList(compiled.getItems());
	}
	
	@Override
	public boolean removeItem(UsableItem removeItem) {
		boolean found = false;
		// find the item on the compiled list
		for (Iterator<UsableItem> it = compiled.getItems().iterator(); it.hasNext();) {
			UsableItem item = it.next();
			if (item.isSimilar(removeItem)) {
				int newAmount = item.getAmount() - 1;
				if (newAmount > 0) {
					item.setAmount(newAmount);
				} else {
					item.setAmount(0); // because why not
					it.remove();
				}
				found = true;
				break;
			}
		}
		// if the item was on the compiled list, remove it from the current ItemSets
		if (found) {
			loop: for (Iterator<ItemSet> itSet = current.values().iterator(); itSet.hasNext();) {
				ItemSet set = itSet.next();
				for (Iterator<UsableItem> itItem = set.getItems().iterator(); itItem.hasNext();) {
					UsableItem item = itItem.next();
					if (item.isSimilar(removeItem)) {
						int newAmount = item.getAmount() - 1;
						if (newAmount > 0) {
							item.setAmount(newAmount);
						} else {
							item.setAmount(0);
							itItem.remove();
							// empty ItemSets should be removed, no need to keep them
							if (set.isEmpty()) {
								itSet.remove();
							}
						}
						break loop;
					}
				}
			}
		}
		return found;
	}
	
	@Override
	public void onRespawn() {
		switch (respawnAction) {
		case CLEAR:
			load();
			break;
		case COMBINE:
			stored.values().forEach(list -> list.forEach(applier -> addCurrent(applier)));
			break;
		case NOTHING: // nothing
		}
		refill();
	}
	
	@Override
	public void refill() {
		compiled.engine.refill();
		compiled.wings.refill();
		compiled.items.forEach(item -> item.refill());
	}
	
	@Override
	public Map<String, ItemSet> getCurrent() {
		return current;
	}
	
	@Override
	public AddResult addCurrent(SetApplier applier) {
		int amount = applier.getAmount();
		String category = applier.getCategory();
		AddResult result = null;
		ItemSet c = current.get(category); // current ItemSet
		if (c == null) { // new set
			switch (applier.getAddType()) {
			case INCREASE:
			case FILL:
				// in this case these both add in the same way
				ItemSet set = applier.getItemSet();
				set.increase(amount - 1);
				current.put(category, set);
				result = AddResult.ADDED;
				break;
			case DECREASE:
				// decreasing is not possible if there are no items
				result = AddResult.ALREADY_EMPTIED;
				break;
			}
			// no conflict checking - the category was empty
		} else { // existing set
			if (c.getID().equals(applier.getID())) {
				switch (applier.getAddType()) {
				case INCREASE:
					result = c.increase(amount) ? AddResult.ADDED : AddResult.ALREADY_MAXED;
					break;
				case DECREASE:
					result = c.increase(-amount) ? AddResult.REMOVED : AddResult.ALREADY_EMPTIED;
					// remove empty sets
					if (c.isEmpty()) {
						current.remove(category);
					}
					break;
				case FILL:
					c.fill(amount);
					result = AddResult.FILLED;
					break;
				}
			} else {
				switch (applier.getConflictAction()) {
				case REPLACE:
					ItemSet set = applier.getItemSet();
					set.increase(amount - 1);
					current.put(category, set);
					result = AddResult.REPLACED;
					break;
				case SKIP:
					result = AddResult.SKIPPED;
					break;
				}
			}
		}
		if (result != AddResult.ALREADY_MAXED && result != AddResult.ALREADY_EMPTIED && result != AddResult.SKIPPED) {
			compile();
		}
		return result;
	}
	
	@Override
	public Map<String, List<SetApplier>> getStored() {
		return stored;
	}
	
	@Override
	public AddResult addStored(SetApplier applier) {
		AddResult result = addCurrent(applier);
		List<SetApplier> list = stored.computeIfAbsent(applier.getCategory(), k -> new ArrayList<>());
		switch (result) {
		case ADDED:
		case FILLED:
			list.add(applier);
			break;
		case REMOVED:
			boolean removed = false;
			for (Iterator<SetApplier> it = list.iterator(); it.hasNext();) {
				if (it.next().isSimilar(applier)) {
					it.remove();
					removed = true;
					break;
				}
			}
			if (!removed) {
				list.add(applier);
			}
			break;
		case REPLACED:
			list.clear();
			list.add(applier);
			break;
		default:
			break;
		}
		return result;
	}
	
	@Override
	public Map<String, List<SetApplier>> getDefault() {
		return def.entrySet().stream().collect(Collectors.toMap(entry -> entry.getKey(), entry -> new ArrayList<>(entry.getValue())));
	}

	@Override
	public PlayerClass replicate() {
		return new DefaultClass(getDefault(), respawnAction);
	}

}
