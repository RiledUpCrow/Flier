/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core;

import java.util.ArrayList;
import java.util.Collections;
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
import pl.betoncraft.flier.util.ValueLoader;

/**
 * A base class for items saved in the configuration sections.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultItem implements Item {
	
	protected final String id;

	protected final ItemStack item;
	protected final double weight;
	protected final int slot;
	protected final List<Effect> passive = new ArrayList<>();
	protected final List<Effect> inHand = new ArrayList<>();

	public DefaultItem(ConfigurationSection section) throws LoadingException {
		id = section.getName();
		Material type = ValueLoader.loadEnum(section, "material", Material.class);
		String name = ChatColor.translateAlternateColorCodes('&', ValueLoader.loadString(section, "name"));
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
		weight = ValueLoader.loadDouble(section, "weight");
		slot = section.getInt("slot", -1);
		for (String effect : section.getStringList("passive_effects")) {
			try {
				Effect eff = Flier.getInstance().getEffect(effect);
				passive.add(eff);
			} catch (LoadingException e) {
				throw (LoadingException) new LoadingException(String.format("Error in '%s' passive effect.", effect))
						.initCause(e);
			}
		}
		for (String effect : section.getStringList("in_hand_effects")) {
			try {
				inHand.add(Flier.getInstance().getEffect(effect));
			} catch (LoadingException e) {
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
		return Collections.unmodifiableList(passive);
	}

	@Override
	public List<Effect> getInHandEffects() {
		return Collections.unmodifiableList(inHand);
	}
	
	@Override
	public Item replicate() {
		try {
			return Flier.getInstance().getItem(id);
		} catch (LoadingException e) {
			return null; // dead code
		}
	}
	
	@Override
	public boolean isSameAs(Item item) {
		if (item instanceof DefaultItem) {
			DefaultItem defItem = (DefaultItem) item;
			return defItem.id.equals(id);
		}
		return false;
	}

}
