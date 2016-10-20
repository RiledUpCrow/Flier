/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 * Represents a custom Elytra.
 *
 * @author Jakub Sapalski
 */
public class Wings {
	
	private ItemStack wings;
	
	private double health = 100;
	private double regeneration = 1;
	private double weight = 0;
	private double aerodynamics = 0;
	private double liftingForce = 0;
	
	public Wings(ConfigurationSection section) {
		String name = ChatColor.translateAlternateColorCodes('&', section.getString("name", ChatColor.LIGHT_PURPLE + "Wings"));
		List<String> lore = section.getStringList("lore");
		for (int i = 0; i < lore.size(); i++) {
			lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));
		}
		wings = new ItemStack(Material.ELYTRA);
		ItemMeta wingsMeta = wings.getItemMeta();
		wingsMeta.setDisplayName(name);
		wingsMeta.setLore(lore);
		wingsMeta.spigot().setUnbreakable(true);
		wings.setItemMeta(wingsMeta);
		health = section.getDouble("health", health);
		regeneration = section.getDouble("regeneration", regeneration);
		aerodynamics = section.getDouble("aerodynamics", aerodynamics);
		liftingForce = section.getDouble("liftingforce", liftingForce);
		weight = section.getDouble("weight", weight);
	}
	
	public ItemStack getItem() {
		return wings.clone();
	}
	
	public double getHealth() {
		return health;
	}

	public double getRegeneration() {
		return regeneration;
	}

	public double getWeight() {
		return weight;
	}

	public double getAerodynamics() {
		return aerodynamics;
	}
	
	public double getLiftingForce() {
		return liftingForce;
	}

}
