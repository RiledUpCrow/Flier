/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.item;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import pl.betoncraft.flier.Flier;
import pl.betoncraft.flier.api.Effect;
import pl.betoncraft.flier.api.Item;

/**
 * A base class for items saved in the configuration sections.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultItem implements Item {
	
	protected ItemStack item;
	protected double weight;
	protected int slot;
	protected List<Effect> passive = new ArrayList<>();
	protected List<Effect> inHand = new ArrayList<>();
	
	public DefaultItem(ConfigurationSection section) {
		Material type = Material.matchMaterial(section.getString("material", "FEATHER"));
		if (type == null) {
			type = Material.FEATHER;
		}
		String name = ChatColor.translateAlternateColorCodes('&', section.getString("name", ChatColor.GREEN + "Engine"));
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
		weight = section.getDouble("weight", weight);
		slot = section.getInt("slot", slot);
		for (String e : section.getStringList("passive_effects")) {
			Effect eff = Flier.getInstance().getEffects().get(e);
			passive.add(eff);
		}
		for (String e : section.getStringList("in_hand_effects")) {
			inHand.add(Flier.getInstance().getEffects().get(e));
		}
	}
	
	@Override
	public ItemStack getItem() {
		return item.clone();
	}
	
	@Override
	public double getWeight() {
		return weight;
	}
	
	@Override
	public int slot() {
		return slot;
	}
	
	@Override
	public List<Effect> getPassiveEffects() {
		return passive;
	}
	
	@Override
	public List<Effect> getInHandEffects() {
		return inHand;
	}

}
