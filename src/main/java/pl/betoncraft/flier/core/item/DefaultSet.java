/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.Engine;
import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.ItemSet;
import pl.betoncraft.flier.api.LoadingException;
import pl.betoncraft.flier.api.UsableItem;
import pl.betoncraft.flier.api.UsableItemStack;
import pl.betoncraft.flier.api.Wings;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * A default implementation of ItemSet.
 *
 * @author Jakub Sapalski
 */
public class DefaultSet implements ItemSet {
	
	protected ValueLoader loader;

	protected String name;
	protected Engine engine;
	protected Wings wings;
	protected List<UsableItemStack> items = new ArrayList<>();
	protected String category;

	public DefaultSet(ConfigurationSection section) throws LoadingException {
		loader = new ValueLoader(section);
		name = section.getString("name");
		category = loader.loadString("category");
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
				UsableItem item;
				int amount = 1, max = 0, min = 0;
				Object itemObject = map.get("item");
				if (itemObject == null || !(itemObject instanceof String)) {
					throw new LoadingException("Item name is missing.");
				}
				item = Flier.getInstance().getItem((String) itemObject);
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
				this.items.add(new DefaultUsableItemStack(item, amount, max, min));
			}
		} catch (LoadingException e) {
			throw (LoadingException) new LoadingException("Error in items.").initCause(e);
		}
	}
	
	private DefaultSet() {
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Engine getEngine() {
		return engine == null ? engine : (Engine) engine.replicate();
	}
	
	@Override
	public void setEngine(Engine engine) {
		this.engine = engine;
	}

	@Override
	public Wings getWings() {
		return wings == null ? wings : (Wings) wings.replicate();
	}
	
	@Override
	public void setWings(Wings wings) {
		this.wings = wings;
	}

	@Override
	public List<UsableItemStack> getItems() {
		return items;
	}

	@Override
	public String getCategory() {
		return category;
	}
	
	@Override
	public boolean isEmpty() {
		return engine == null && wings == null && items.stream().allMatch(item -> item.getAmount() == 0);
	}

	@Override
	public boolean isSimilar(ItemSet set) {
		if (set instanceof DefaultSet) {
			DefaultSet s = (DefaultSet) set;
			if (!((engine == null && s.engine == null) || (engine != null && s.engine != null && engine.isSimilar(s.engine)))) {
				return false;
			}
			if (!((wings == null && s.wings == null) || (wings != null && s.wings != null && wings.isSimilar(s.wings)))) {
				return false;
			}
			if (items.size() != set.getItems().size()) {
				return false;
			}
			for (int i = 0; i < items.size(); i++) {
				if (!items.get(i).isSimilar(set.getItems().get(i))) {
					return false;
				}
			}
		}
		return true;
	}
	
	@Override
	public ItemSet replicate() {
		DefaultSet set = new DefaultSet();
		set.category = category;
		set.engine = engine == null ? null : (Engine) engine.replicate();
		set.wings = wings == null ? null : (Wings) wings.replicate();
		set.items = new ArrayList<>(items.size());
		for (UsableItemStack stack : items) {
			set.items.add(stack.clone());
		}
		set.name = name;
		return set;
	}

	@Override
	public boolean increase(int amount) {
		for (UsableItemStack stack : items) {
			int newAmount = stack.getAmount() + (stack.getDefaultAmount() * amount);
			if (newAmount < 0 || newAmount < stack.getMin() || newAmount > stack.getMax()) {
				return false;
			}
		}
		for (UsableItemStack stack : items) {
			stack.setAmount(stack.getAmount() + (stack.getDefaultAmount() * amount));
		}
		return true;
	}
	
	@Override
	public void fill(int amount) {
		for (UsableItemStack stack : items) {
			int fullAmount = stack.getDefaultAmount() * amount;
			if (stack.getAmount() < fullAmount) {
				// if filling is over the limit, set it to the limit
				if (!stack.setAmount(stack.getDefaultAmount() * amount)) {
					stack.setAmount(stack.getMax());
				}
			}
		}
	}

	@Override
	public int getAmount() {
		int amount = -1;
		for (UsableItemStack stack : items) {
			int a = stack.getAmount();
			int d = stack.getDefaultAmount();
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
