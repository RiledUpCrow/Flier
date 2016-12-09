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

	private Map<String, EngineFactory> engineTypes = new HashMap<>();
	private Map<String, ItemFactory> itemTypes = new HashMap<>();
	private Map<String, WingFactory> wingTypes = new HashMap<>();
	private Map<String, ClassFactory> classTypes = new HashMap<>();
	private Map<String, GameFactory> gameTypes = new HashMap<>();
	
	private Map<String, Engine> engines = new HashMap<>();
	private Map<String, UsableItem> items = new HashMap<>();
	private Map<String, Wings> wings = new HashMap<>();
	private Map<String, PlayerClass> classes = new HashMap<>();
	private Map<String, Game> games = new HashMap<>();
	
	public interface EngineFactory {
		public Engine get(ConfigurationSection settings);
	}
	
	public interface ItemFactory {
		public UsableItem get(ConfigurationSection settings);
	}
	
	public interface WingFactory {
		public Wings get(ConfigurationSection settings);
	}
	
	public interface ClassFactory {
		public PlayerClass get(ConfigurationSection settings);
	}
	
	public interface GameFactory {
		public Game get(ConfigurationSection settings);
	}
	
	@Override
	public void onEnable() {
		instance = this;
		saveDefaultConfig();
		new FlierCommand();
		// register new types
		registerEngine("simpleEngine", s -> new SimpleEngine(s));
		registerItem("simpleWeapon", s -> new SimpleWeapon(s));
		registerItem("homingMissile", s -> new HomingMissile(s));
		registerWings("simpleWings", s -> new SimpleWings(s));
		registerClass("fixedClass", s -> new FixedClass(s));
		registerGame("simpleGame", s -> new SimpleGame(s));
		// TODO separate files
		ConfigurationSection engineSection = getConfig().getConfigurationSection("engines");
		if (engineSection != null) {
			for (String section : engineSection.getKeys(false)) {
				ConfigurationSection engine = engineSection.getConfigurationSection(section);
				String type = engine.getString("type", "simpleEngine");
				EngineFactory factory = engineTypes.get(type);
				engines.put(section, factory.get(engine));
			}
		}
		ConfigurationSection itemSection = getConfig().getConfigurationSection("items");
		if (itemSection != null) {
			for (String section : itemSection.getKeys(false)) {
				ConfigurationSection item = itemSection.getConfigurationSection(section);
				String type = item.getString("type", "simpleWeapon");
				ItemFactory factory = itemTypes.get(type);
				items.put(section, factory.get(item));
			}
		}
		ConfigurationSection wingSection = getConfig().getConfigurationSection("wings");
		if (wingSection != null) {
			for (String section : wingSection.getKeys(false)) {
				ConfigurationSection wings = wingSection.getConfigurationSection(section);
				String type = wings.getString("type", "simpleWings");
				WingFactory factory = wingTypes.get(type);
				this.wings.put(section, factory.get(wings));
			}
		}
		ConfigurationSection classSection = getConfig().getConfigurationSection("classes");
		if (classSection != null) {
			for (String section : classSection.getKeys(false)) {
				ConfigurationSection clazz = classSection.getConfigurationSection(section);
				String type = clazz.getString("type", "fixedClass");
				ClassFactory factory = classTypes.get(type);
				classes.put(section, factory.get(clazz));
			}
		}
		ConfigurationSection gameSection = getConfig().getConfigurationSection("games");
		if (gameSection != null) {
			for (String section : gameSection.getKeys(false)) {
				ConfigurationSection game = gameSection.getConfigurationSection(section);
				String type = game.getString("type", "simpleGame");
				GameFactory factory = gameTypes.get(type);
				games.put(section, factory.get(game));
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
	 * @return the instance of the plugin
	 */
	public static Flier getInstance() {
		return instance;
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
	 * @return the engines
	 */
	public Map<String, Engine> getEngines() {
		return engines;
	}

	/**
	 * @return the items
	 */
	public Map<String, UsableItem> getItems() {
		return items;
	}

	/**
	 * @return the wings
	 */
	public Map<String, Wings> getWings() {
		return wings;
	}

	/**
	 * @return the classes
	 */
	public Map<String, PlayerClass> getClasses() {
		return classes;
	}

	/**
	 * @return the games
	 */
	public Map<String, Game> getGames() {
		return games;
	}
	
	public void registerEngine(String name, EngineFactory factory) {
		engineTypes.put(name, factory);
	}
	
	public void registerItem(String name, ItemFactory factory) {
		itemTypes.put(name, factory);
	}
	
	public void registerWings(String name, WingFactory factory) {
		wingTypes.put(name, factory);
	}
	
	public void registerClass(String name, ClassFactory factory) {
		classTypes.put(name, factory);
	}
	
	public void registerGame(String name, GameFactory factory) {
		gameTypes.put(name, factory);
	}

}
