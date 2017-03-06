/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core.defaults;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import pl.betoncraft.flier.api.core.Item;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.util.ModificationManager;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * A base class for items saved in the configuration sections.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultItem implements Item {

	private static final String WEIGHT = "weight";

	protected final String id;
	protected final ValueLoader loader;
	protected final ModificationManager modMan;

	protected final ItemStack item;
	protected final double weight;
	protected final int slot;	

	public DefaultItem(ConfigurationSection section) throws LoadingException {
		id = section.getName();
		loader = new ValueLoader(section);
		modMan = new ModificationManager();
		Material type = loader.loadEnum("material", Material.class);
		String name = ChatColor.translateAlternateColorCodes('&', loader.loadString("name"));
		List<String> lore = section.getStringList("lore");
		for (int i = 0; i < lore.size(); i++) {
			lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));
		}
		item = new ItemStack(type);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		meta.spigot().setUnbreakable(true);
		item.setItemMeta(meta);
		weight = loader.loadDouble(WEIGHT, 0.0);
		slot = loader.loadInt("slot", -1);
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public ItemStack getItem() {
		return item.clone();
	}

	@Override
	public double getWeight() {
		return modMan.modifyNumber(WEIGHT, weight);
	}

	@Override
	public int slot() {
		return slot;
	}
	
	@Override
	public boolean isSimilar(Item item) {
		if (item instanceof DefaultItem) {
			DefaultItem defItem = (DefaultItem) item;
			return defItem.item.isSimilar(this.item) &&
					defItem.weight == weight &&
					defItem.slot == slot;
		}
		return false;
	}

}
