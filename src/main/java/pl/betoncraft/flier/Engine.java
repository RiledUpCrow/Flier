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
 * Represents an engine.
 *
 * @author Jakub Sapalski
 */
public class Engine {
	
	private ItemStack engine;
	
	private double maxSpeed = 1.5;
	private double acceleration = 1.2;
	private double maxFuel = 100;
	private double consumption = 2;
	private double regeneration = 1;
	private int glowTime = 100;
	
	public Engine(ConfigurationSection section) {
		Material type = Material.matchMaterial(section.getString("material", "FEATHER"));
		if (type == null) {
			type = Material.FEATHER;
		}
		String name = ChatColor.translateAlternateColorCodes('&', section.getString("name", ChatColor.GREEN + "Engine"));
		List<String> lore = section.getStringList("lore");
		for (int i = 0; i < lore.size(); i++) {
			lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));
		}
		engine = new ItemStack(type);
		ItemMeta engineMeta = engine.getItemMeta();
		engineMeta.setDisplayName(name);
		engineMeta.setLore(lore);
		engineMeta.spigot().setUnbreakable(true);
		engine.setItemMeta(engineMeta);
		maxSpeed = section.getDouble("max_speed", maxSpeed);
		acceleration = section.getDouble("acceleration", acceleration);
		maxFuel = section.getDouble("max_fuel", maxFuel);
		consumption = section.getDouble("consumption", consumption);
		regeneration = section.getDouble("regeneration", regeneration);
		glowTime = section.getInt("glow_time", glowTime);
	}

	/**
	 * @return the engine item
	 */
	public ItemStack getItem() {
		return engine.clone();
	}

	/**
	 * @return the maximum acceleration speed
	 */
	public double getMaxSpeed() {
		return maxSpeed;
	}

	/**
	 * @return the acceleration multiplier per flight tick
	 */
	public double getAcceleration() {
		return acceleration;
	}

	/**
	 * @return the maximum amount of stored fuel
	 */
	public double getMaxFuel() {
		return maxFuel;
	}

	/**
	 * @return the fuel consumption per flight tick
	 */
	public double getConsumption() {
		return consumption;
	}

	/**
	 * @return the fuel regeneration per flight tick
	 */
	public double getRegeneration() {
		return regeneration;
	}
	
	/**
	 * @return the amount of tick to glow after speed up
	 */
	public int getGlowTime() {
		return glowTime;
	}

}
