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

import pl.betoncraft.flier.api.Engine;
import pl.betoncraft.flier.api.ItemSet;
import pl.betoncraft.flier.api.PlayerClass;
import pl.betoncraft.flier.api.SetApplier;
import pl.betoncraft.flier.api.UsableItem;
import pl.betoncraft.flier.api.UsableItemStack;
import pl.betoncraft.flier.api.Wings;
import pl.betoncraft.flier.core.item.DefaultSetApplier;

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
	
	public DefaultClass(List<ItemSet> sets, RespawnAction respAct) {
		respawnAction = respAct;
		HashMap<String, List<SetApplier>> map = new HashMap<>(sets.size());
		sets.forEach(set -> map.computeIfAbsent(set.getCategory(), k -> new ArrayList<>()).add(new DefaultSetApplier(set)));
		def = Collections.unmodifiableMap(map);
		reset();
		load();
		compile();
	}
	
	private class Compiled {

		private String name;
		private Engine engine;
		private Wings wings;
		private List<UsableItemStack> items = new ArrayList<>();
		
		private Compiled(Collection<ItemSet> sets) {
			for (ItemSet set : sets) {
				if (set.getName() != null) {
					name = set.getName();
				}
				Engine e = set.getEngine();
				if (e != null) {
					engine = (Engine) e.replicate();
				}
				Wings w = set.getWings();
				if (e != null) {
					wings = (Wings) w.replicate();
				}
				List<UsableItemStack> items = set.getItems();
				loop: for (UsableItemStack newItem : items) {
					for (UsableItemStack existingItem : this.items) {
						if (existingItem.isSimilar(newItem)) {
							existingItem.setAmount(existingItem.getAmount() + newItem.getAmount());
							continue loop;
						}
					}
					this.items.add(newItem.clone());
				}
			}
		}
		
		public Engine getEngine() {
			return engine;
		}
		
		public Wings getWings() {
			return wings;
		}
		
		public List<UsableItemStack> getItems() {
			return items;
		}

	}
	
	private void load() {
		current.clear();
		List<SetApplier> list = new ArrayList<>();
		stored.values().forEach(l -> list.addAll(l));
		for (SetApplier applier : list) {
			addCurrent(applier);
		}
	}
	
	private void compile() {
		compiled = new Compiled(current.values());
	}
	
	@Override
	public void reset() {
		stored.putAll(def);
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
	public List<UsableItemStack> getItems() {
		return Collections.unmodifiableList(compiled.getItems());
	}
	
	@Override
	public boolean removeItem(UsableItem item) {
		boolean found = false;
		// find the item on the compiled list
		for (Iterator<UsableItemStack> i = compiled.getItems().iterator(); i.hasNext();) {
			UsableItemStack stack = i.next();
			if (stack.getItem().isSimilar(item)) {
				int newAmount = stack.getAmount() - 1;
				if (newAmount > 0) {
					stack.setAmount(newAmount);
				} else {
					stack.setAmount(0); // because why not
					i.remove();
				}
				found = true;
				break;
			}
		}
		// if the item was on the compiled list, remove it from the current ItemSets
		if (found) {
			loop: for (Iterator<ItemSet> iSet = current.values().iterator(); iSet.hasNext();) {
				ItemSet set = iSet.next();
				for (Iterator<UsableItemStack> iStack = set.getItems().iterator(); iStack.hasNext();) {
					UsableItemStack stack = iStack.next();
					if (stack.getItem().isSimilar(item)) {
						int newAmount = stack.getAmount() - 1;
						if (newAmount > 0) {
							stack.setAmount(newAmount);
						} else {
							stack.setAmount(0);
							iStack.remove();
							// empty ItemSets should be removed, no need to keep them
							if (set.isEmpty()) {
								iSet.remove();
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
			List<SetApplier> list = new ArrayList<>();
			stored.values().forEach(l -> list.addAll(l));
			for (SetApplier applier : list) {
				 addCurrent(applier);
			}
			break;
		case NOTHING: // nothing
		}
		compile();
	}
	
	@Override
	public Map<String, ItemSet> getCurrent() {
		return copyMap(current);
	}
	
	@Override
	public AddResult addCurrent(SetApplier applier) {
		ItemSet set = applier.getItemSet();
		int amount = applier.getAmount();
		String category = set.getCategory();
		AddResult result = null;
		ItemSet c = current.get(category); // current ItemSet
		if (c == null) { // new set
			switch (applier.getAddType()) {
			case INCREASE:
			case FILL:
				// in this case these both add in the same way
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
			if (c.isSimilar(set)) {
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
		List<SetApplier> list = stored.computeIfAbsent(applier.getItemSet().getCategory(), k -> new ArrayList<>());
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
		return def;
	}
	
	private static Map<String, ItemSet> copyMap(Map<String, ItemSet> map) {
		return map.entrySet().stream().collect(Collectors.toMap(
				entry -> entry.getKey(), entry -> entry.getValue().replicate()));
	}

	@Override
	public PlayerClass replicate() {
		List<ItemSet> list = new ArrayList<>();
		def.values().forEach(l -> l.forEach(e -> list.add(e.getItemSet())));
		return new DefaultClass(list, respawnAction);
	}

}
