/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandExecutor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import pl.betoncraft.flier.api.Engine;
import pl.betoncraft.flier.api.Game;
import pl.betoncraft.flier.api.PlayerClass;
import pl.betoncraft.flier.api.UsableItem;
import pl.betoncraft.flier.api.Wings;

public class Flier extends JavaPlugin implements Listener, CommandExecutor {
	
	private static Flier instance;
	
	private Map<String, Engine> engines = new HashMap<>();
	private Map<String, UsableItem> items = new HashMap<>();
	private Map<String, Wings> wings = new HashMap<>();
	private Map<String, PlayerClass> classes = new HashMap<>();
	private Map<String, Game> games = new HashMap<>();
	
	@Override
	public void onEnable() {
		instance = this;
		saveDefaultConfig();
		new FlierCommand();
		// TODO dynamic type loading, separate files
		ConfigurationSection engineSection = getConfig().getConfigurationSection("engines");
		if (engineSection != null) {
			for (String section : engineSection.getKeys(false)) {
				engines.put(section, new SimpleEngine(engineSection.getConfigurationSection(section)));
			}
		}
		ConfigurationSection itemSection = getConfig().getConfigurationSection("items");
		if (itemSection != null) {
			for (String section : itemSection.getKeys(false)) {
				items.put(section, new SimpleWeapon(itemSection.getConfigurationSection(section)));
			}
		}
		ConfigurationSection wingSection = getConfig().getConfigurationSection("wings");
		if (wingSection != null) {
			for (String section : wingSection.getKeys(false)) {
				wings.put(section, new SimpleWings(wingSection.getConfigurationSection(section)));
			}
		}
		ConfigurationSection classSection = getConfig().getConfigurationSection("classes");
		if (classSection != null) {
			for (String section : classSection.getKeys(false)) {
				classes.put(section, new FixedClass(classSection.getConfigurationSection(section)));
			}
		}
		ConfigurationSection gameSection = getConfig().getConfigurationSection("games");
		if (gameSection != null) {
			for (String section : gameSection.getKeys(false)) {
				games.put(section, new SimpleGame(gameSection.getConfigurationSection(section)));
			}
		}
		getLogger().info("Loaded " + engines.size() + " engines, " + items.size() + " items, " + wings.size() +
				" wings, " + classes.size() + " classes and " + games.size() + " games.");
	}
	
	@Override
	public void onDisable() {
		for (Game game : games.values()) {
			game.stop();
		}
	}
	
	/**
	 * @param name name of the engine
	 * @return the engine
	 */
	public Engine getEngine(String name) {
		return engines.get(name);
	}
	
	/**
	 * @param name name of the item
	 * @return the item
	 */
	public UsableItem getItem(String name) {
		return items.get(name);
	}
	
	/**
	 * @param name name of the wings
	 * @return the wings
	 */
	public Wings getWings(String name) {
		return wings.get(name);
	}
	
	/**
	 * @param name name of the class
	 * @return the class
	 */
	public PlayerClass getClass(String name) {
		return classes.get(name);
	}
	
	/**
	 * @param name name of the game
	 * @return the game
	 */
	public Game getGame(String name) {
		return games.get(name);
	}
	
	/**
	 * @return the instance of the plugin
	 */
	public static Flier getInstance() {
		return instance;
	}

}
