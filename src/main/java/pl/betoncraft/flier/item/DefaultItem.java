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
import pl.betoncraft.flier.exception.LoadingException;
import pl.betoncraft.flier.exception.ObjectUndefinedException;
import pl.betoncraft.flier.exception.TypeUndefinedException;

/**
 * A base class for items saved in the configuration sections.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultItem implements Item {

	protected ItemStack item;
	protected double weight = 0;
	protected int slot = 1;
	protected List<Effect> passive = new ArrayList<>();
	protected List<Effect> inHand = new ArrayList<>();

	public DefaultItem(ConfigurationSection section) throws LoadingException {
		String typeName = section.getString("material", "FEATHER");
		Material type = Material.matchMaterial(typeName);
		if (type == null) {
			throw new LoadingException(String.format("Material '%s' does not exist.", typeName));
		}
		String name = ChatColor.translateAlternateColorCodes('&',
				section.getString("name", ChatColor.GREEN + "Engine"));
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
		for (String effect : section.getStringList("passive_effects")) {
			try {
				Effect eff = Flier.getInstance().getEffect(effect);
				passive.add(eff);
			} catch (ObjectUndefinedException | TypeUndefinedException | LoadingException e) {
				throw (LoadingException) new LoadingException(String.format("Error in '%s' passive effect.", effect))
						.initCause(e);
			}
		}
		for (String effect : section.getStringList("in_hand_effects")) {
			try {
				inHand.add(Flier.getInstance().getEffect(effect));
			} catch (ObjectUndefinedException | TypeUndefinedException | LoadingException e) {
				throw (LoadingException) new LoadingException(String.format("Error in '%s' in-hand effect.", effect))
						.initCause(e);
			}
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
