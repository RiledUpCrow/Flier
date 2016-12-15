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

import pl.betoncraft.flier.api.Effect;
import pl.betoncraft.flier.api.Engine;
import pl.betoncraft.flier.api.Game;
import pl.betoncraft.flier.api.Item;
import pl.betoncraft.flier.api.Lobby;
import pl.betoncraft.flier.api.Wings;
import pl.betoncraft.flier.command.FlierCommand;
import pl.betoncraft.flier.effect.TargetCompass;
import pl.betoncraft.flier.game.TeamDeathMatch;
import pl.betoncraft.flier.item.Launcher;
import pl.betoncraft.flier.item.VanillaItem;
import pl.betoncraft.flier.item.engine.MultiplyingEngine;
import pl.betoncraft.flier.item.weapon.HomingMissile;
import pl.betoncraft.flier.item.weapon.MachineGun;
import pl.betoncraft.flier.item.wings.SimpleWings;
import pl.betoncraft.flier.lobby.FixedPhysicalLobby;

public class Flier extends JavaPlugin implements Listener, CommandExecutor {
	
	private static Flier instance;

	private Map<String, EngineFactory> engineTypes = new HashMap<>();
	private Map<String, ItemFactory> itemTypes = new HashMap<>();
	private Map<String, WingFactory> wingTypes = new HashMap<>();
	private Map<String, LobbyFactory> lobbyTypes = new HashMap<>();
	private Map<String, GameFactory> gameTypes = new HashMap<>();
	private Map<String, EffectFactory> effectTypes = new HashMap<>();
	
	private Map<String, Engine> engines = new HashMap<>();
	private Map<String, Item> items = new HashMap<>();
	private Map<String, Wings> wings = new HashMap<>();
	private Map<String, Lobby> lobbies = new HashMap<>();
	private Map<String, Game> games = new HashMap<>();
	private Map<String, Effect> effects = new HashMap<>();
	
	public interface EngineFactory {
		public Engine get(ConfigurationSection settings);
	}
	
	public interface ItemFactory {
		public Item get(ConfigurationSection settings);
	}
	
	public interface WingFactory {
		public Wings get(ConfigurationSection settings);
	}
	
	public interface LobbyFactory {
		public Lobby get(ConfigurationSection settings);
	}
	
	public interface GameFactory {
		public Game get(ConfigurationSection settings);
	}
	
	public interface EffectFactory {
		public Effect get(ConfigurationSection section);
	}
	
	@Override
	public void onEnable() {
		
		instance = this;
		saveDefaultConfig();
		new FlierCommand();
		// register new types
		registerEngine("multiplyingEngine", s -> new MultiplyingEngine(s));
		registerItem("machineGun", s -> new MachineGun(s));
		registerItem("homingMissile", s -> new HomingMissile(s));
		registerItem("launcher", s -> new Launcher(s));
		registerItem("vanillaItem", s -> new VanillaItem(s));
		registerWings("simpleWings", s -> new SimpleWings(s));
		registerLobby("fixedPhysicalLobby", s -> new FixedPhysicalLobby(s));
		registerGame("teamDeathMatch", s -> new TeamDeathMatch(s));
		registerEffect("targetCompass", s -> new TargetCompass(s));

		ConfigurationSection effectSection = getConfig().getConfigurationSection("effects");
		if (effectSection != null) {
			for (String section : effectSection.getKeys(false)) {
				ConfigurationSection effect = effectSection.getConfigurationSection(section);
				String type = effect.getString("type", "target");
				EffectFactory factory = effectTypes.get(type);
				effects.put(section, factory.get(effect));
			}
		}
		ConfigurationSection engineSection = getConfig().getConfigurationSection("engines");
		if (engineSection != null) {
			for (String section : engineSection.getKeys(false)) {
				ConfigurationSection engine = engineSection.getConfigurationSection(section);
				String type = engine.getString("type", "multiplyingEngine");
				EngineFactory factory = engineTypes.get(type);
				engines.put(section, factory.get(engine));
			}
		}
		ConfigurationSection itemSection = getConfig().getConfigurationSection("items");
		if (itemSection != null) {
			for (String section : itemSection.getKeys(false)) {
				ConfigurationSection item = itemSection.getConfigurationSection(section);
				String type = item.getString("type", "machineGun");
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
		ConfigurationSection lobbySection = getConfig().getConfigurationSection("lobbies");
		if (lobbySection != null) {
			for (String section : lobbySection.getKeys(false)) {
				ConfigurationSection lobby = lobbySection.getConfigurationSection(section);
				String type = lobby.getString("type", "fixedPhysicalLobby");
				LobbyFactory factory = lobbyTypes.get(type);
				lobbies.put(section, factory.get(lobby));
			}
		}
		ConfigurationSection gameSection = getConfig().getConfigurationSection("games");
		if (gameSection != null) {
			for (String section : gameSection.getKeys(false)) {
				ConfigurationSection game = gameSection.getConfigurationSection(section);
				String type = game.getString("type", "teamDeathMatch");
				GameFactory factory = gameTypes.get(type);
				games.put(section, factory.get(game));
			}
		}
		getLogger().info("Loaded " + engines.size() + " engines, " + items.size() + " items, " + wings.size() +
				" wings, " + lobbies.size() + " lobbies, " + games.size() + " games and " + effects.size() + " effects.");
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
	 * @return the engines
	 */
	public Map<String, Engine> getEngines() {
		return engines;
	}

	/**
	 * @return the items
	 */
	public Map<String, Item> getItems() {
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
	public Map<String, Lobby> getLobbies() {
		return lobbies;
	}

	/**
	 * @return the games
	 */
	public Map<String, Game> getGames() {
		return games;
	}
	
	/**
	 * @return the effects
	 */
	public Map<String, Effect> getEffects() {
		return effects;
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
	
	public void registerLobby(String name, LobbyFactory factory) {
		lobbyTypes.put(name, factory);
	}
	
	public void registerGame(String name, GameFactory factory) {
		gameTypes.put(name, factory);
	}
	
	public void registerEffect(String name, EffectFactory factory) {
		effectTypes.put(name, factory);
	}

}
