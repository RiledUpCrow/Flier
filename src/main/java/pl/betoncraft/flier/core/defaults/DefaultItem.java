/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core.defaults;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.Item;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.util.LangManager;
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

	protected final Material material;
	protected final String rawName;
	protected final List<String> rawLore;
	protected final double weight;
	protected final int slot;	

	public DefaultItem(ConfigurationSection section) throws LoadingException {
		id = section.getName();
		loader = new ValueLoader(section);
		modMan = new ModificationManager();
		material = loader.loadEnum("material", Material.class);
		rawName = loader.loadString("name");
		rawLore = section.getStringList("lore");
		weight = loader.loadDouble(WEIGHT, 0.0);
		slot = loader.loadInt("slot", -1);
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public ItemStack getItem(InGamePlayer player) {
		String name = rawName.startsWith("$") ?
				LangManager.getMessage(player, rawName.substring(1)) :
				ChatColor.translateAlternateColorCodes('&', rawName);
		List<String> lore = rawLore.stream()
				.map(s -> s.startsWith("$") ?
						LangManager.getMessage(player, s.substring(1)) :
						ChatColor.translateAlternateColorCodes('&', s))
				.collect(Collectors.toList());
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		meta.spigot().setUnbreakable(true);
		item.setItemMeta(meta);
		return item;
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
			return id.equals(defItem.id);
		}
		return false;
	}

}
