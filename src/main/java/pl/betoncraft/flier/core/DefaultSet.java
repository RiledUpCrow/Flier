/**
 * Copyright (c) 2017 Jakub Sapalski
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */
package pl.betoncraft.flier.core;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Engine;
import pl.betoncraft.flier.api.content.Wings;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.ItemSet;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.Modification;
import pl.betoncraft.flier.api.core.UsableItem;
import pl.betoncraft.flier.util.LangManager;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * A default implementation of ItemSet.
 *
 * @author Jakub Sapalski
 */
public class DefaultSet implements ItemSet {
	
	protected ValueLoader loader;

	protected String id;
	protected String name;
	protected String category;
	protected String className;
	protected Engine engine;
	protected Wings wings;
	protected List<UsableItem> items = new ArrayList<>();
	protected List<Modification> mods = new ArrayList<>();

	public DefaultSet(ConfigurationSection section, InGamePlayer owner) throws LoadingException {
		Flier flier = Flier.getInstance();
		id = section.getName();
		loader = new ValueLoader(section);
		name = loader.loadString("name", id);
		category = loader.loadString("category");
		className = section.getString("class_name", null);
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
			items.add(flier.getItem(itemName, owner));
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
	public String getName(CommandSender player) {
		return name.startsWith("$") ? LangManager.getMessage(player, name.substring(1)) : name;
	}

	@Override
	public String getCategory() {
		return category;
	}
	
	@Override
	public String getClassName() {
		return className;
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
