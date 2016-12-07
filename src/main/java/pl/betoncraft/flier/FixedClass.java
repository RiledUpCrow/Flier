/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.Engine;
import pl.betoncraft.flier.api.PlayerClass;
import pl.betoncraft.flier.api.UsableItem;
import pl.betoncraft.flier.api.Wings;

/**
 * A class with fixed items, loaded from the configuration.
 *
 * @author Jakub Sapalski
 */
public class FixedClass implements PlayerClass {
	
	private Engine engine;
	private Map<UsableItem, Integer> items = new HashMap<>();
	private Wings wings;
	
	public FixedClass(ConfigurationSection section) {
		engine = Flier.getInstance().getEngine(section.getString("engine", "default"));
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
			UsableItem ui = Flier.getInstance().getItem(item);
			if (ui != null) {
				items.put(ui, amount);
			} else {}
		}
		wings = Flier.getInstance().getWings(section.getString("wings", "default"));
	}
	
	@Override
	public Engine getEngine() {
		return engine;
	}
	
	@Override
	public Map<UsableItem, Integer> getItems() {
		return items;
	}
	
	@Override
	public Wings getWings() {
		return wings;
	}

}
