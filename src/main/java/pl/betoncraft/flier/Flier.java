/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import pl.betoncraft.flier.api.Effect;
import pl.betoncraft.flier.api.Engine;
import pl.betoncraft.flier.api.Game;
import pl.betoncraft.flier.api.Item;
import pl.betoncraft.flier.api.Lobby;
import pl.betoncraft.flier.api.Wings;
import pl.betoncraft.flier.command.FlierCommand;
import pl.betoncraft.flier.effect.TargetCompass;
import pl.betoncraft.flier.exception.LoadingException;
import pl.betoncraft.flier.exception.ObjectUndefinedException;
import pl.betoncraft.flier.exception.TypeUndefinedException;
import pl.betoncraft.flier.game.TeamDeathMatch;
import pl.betoncraft.flier.item.Launcher;
import pl.betoncraft.flier.item.VanillaItem;
import pl.betoncraft.flier.item.engine.MultiplyingEngine;
import pl.betoncraft.flier.item.weapon.HomingMissile;
import pl.betoncraft.flier.item.weapon.MachineGun;
import pl.betoncraft.flier.item.wings.SimpleWings;
import pl.betoncraft.flier.lobby.PhysicalLobby;

public class Flier extends JavaPlugin {

	private static Flier instance;

	private Map<String, Factory<Engine>> engineTypes = new HashMap<>();
	private Map<String, Factory<Item>> itemTypes = new HashMap<>();
	private Map<String, Factory<Wings>> wingTypes = new HashMap<>();
	private Map<String, Factory<Game>> gameTypes = new HashMap<>();
	private Map<String, Factory<Effect>> effectTypes = new HashMap<>();
	private Map<String, Factory<Lobby>> lobbyTypes = new HashMap<>();
	
	private Map<String, Lobby> lobbies = new HashMap<>();

	/**
	 * Factory which creates instances of a type, using ConfigurationSections to
	 * get data.
	 *
	 * @author Jakub Sapalski
	 */
	public interface Factory<T> {
		public T get(ConfigurationSection settings) throws LoadingException;
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
		registerLobby("fixedPhysicalLobby", s -> new PhysicalLobby(s));
		registerGame("teamDeathMatch", s -> new TeamDeathMatch(s));
		registerEffect("targetCompass", s -> new TargetCompass(s));

		loadLobbies();
	}

	private void loadLobbies() {
		lobbies.clear();
		ConfigurationSection lobbySection = getConfig().getConfigurationSection("lobbies");
		if (lobbySection != null) {
			for (String section : lobbySection.getKeys(false)) {
				try {
					lobbies.put(section, getObject(section, "Lobby", "lobbies", lobbyTypes));
				} catch (ObjectUndefinedException | TypeUndefinedException | LoadingException e) {
					getLogger().severe(String.format("Error while loading lobby '%s':", section));
					getLogger().severe(String.format("    - %s", e.getMessage()));
					Throwable cause = e.getCause();
					while (cause != null) {
						getLogger().severe(String.format("    - %s", cause.getMessage()));
						cause = cause.getCause();
					}
				}
			}
		}
		getLogger().info(String.format("Loaded %d lobbies.", lobbies.size()));
	}

	@Override
	public void onDisable() {
		for (Lobby lobby : lobbies.values()) {
			lobby.stop();
		}
	}

	/**
	 * @return the instance of the plugin
	 */
	public static Flier getInstance() {
		return instance;
	}

	/**
	 * Returns an immutable view of the lobbies map.
	 * 
	 * @return the lobbies
	 */
	public Map<String, Lobby> getLobbies() {
		return Collections.unmodifiableMap(lobbies);
	}

	/**
	 * @return the Engine with specified name, never null
	 * @throws ObjectUndefinedException
	 *             when the Engine is not defined
	 * @throws TypeUndefinedException
	 *             when the Engine type is not defined
	 * @throws LoadingException
	 *             when the Engine cannot be created due to an error
	 */
	public Engine getEngine(String id) throws ObjectUndefinedException, TypeUndefinedException, LoadingException {
		return getObject(id, "Engine", "engines", engineTypes);
	}

	/**
	 * @return the Item with specified name, never null
	 * @throws ObjectUndefinedException
	 *             when the Item is not defined
	 * @throws TypeUndefinedException
	 *             when the Item type is not defined
	 * @throws LoadingException
	 *             when the Item cannot be created due to an error
	 */
	public Item getItem(String id) throws ObjectUndefinedException, TypeUndefinedException, LoadingException {
		return getObject(id, "Item", "items", itemTypes);
	}

	/**
	 * @return the Wings with specified name, never null
	 * @throws ObjectUndefinedException
	 *             when the Wings are not defined
	 * @throws TypeUndefinedException
	 *             when the Wings type is not defined
	 * @throws LoadingException
	 *             when the Wings cannot be created due to an error
	 */
	public Wings getWing(String id) throws ObjectUndefinedException, TypeUndefinedException, LoadingException {
		return getObject(id, "Wings", "wings", wingTypes);
	}

	/**
	 * @return the Effect with specified name, never null
	 * @throws ObjectUndefinedException
	 *             when the Effect is not defined
	 * @throws TypeUndefinedException
	 *             when the Effect type is not defined
	 * @throws LoadingException
	 *             when the Effect cannot be created due to an error
	 */
	public Effect getEffect(String id) throws ObjectUndefinedException, TypeUndefinedException, LoadingException {
		return getObject(id, "Effect", "effects", effectTypes);
	}
	
	/**
	 * @return the Game with specified name, never null
	 * @throws ObjectUndefinedException
	 *             when the Game is not defined
	 * @throws TypeUndefinedException
	 *             when the Game type is not defined
	 * @throws LoadingException
	 *             when the Game cannot be created due to an error
	 */
	public Game getGame(String id) throws ObjectUndefinedException, TypeUndefinedException, LoadingException {
		return getObject(id, "Game", "games", gameTypes);
	}

	private <T> T getObject(String id, String name, String section, Map<String, Factory<T>> factories)
			throws ObjectUndefinedException, TypeUndefinedException, LoadingException {
		ConfigurationSection config = getConfig().getConfigurationSection(String.format("%s.%s", section, id));
		if (config == null || config.getKeys(false).size() == 0) {
			throw new ObjectUndefinedException(String.format("%s do not exist.", name, id));
		}
		String type = config.getString("type", "simpleWings");
		Factory<T> factory = factories.get(type);
		if (factory == null) {
			throw new TypeUndefinedException(String.format("%s type with ID %s does not exist.", name, type));
		}
		return factory.get(config);
	}

	/**
	 * Registers a new Engine type with specified name. The factory will be used
	 * to obtain copies of the Engine.
	 * 
	 * @param name
	 *            name of the type
	 * @param factory
	 *            factory which creates instances of that type
	 */
	public void registerEngine(String name, Factory<Engine> factory) {
		engineTypes.put(name, factory);
	}

	/**
	 * Registers a new Item type with specified name. The factory will be used
	 * to obtain copies of the Item.
	 * 
	 * @param name
	 *            name of the type
	 * @param factory
	 *            factory which creates instances of that type
	 */
	public void registerItem(String name, Factory<Item> factory) {
		itemTypes.put(name, factory);
	}

	/**
	 * Registers a new Wings type with specified name. The factory will be used
	 * to obtain copies of the Wings.
	 * 
	 * @param name
	 *            name of the type
	 * @param factory
	 *            factory which creates instances of that type
	 */
	public void registerWings(String name, Factory<Wings> factory) {
		wingTypes.put(name, factory);
	}

	/**
	 * Registers a new Lobby type with specified name. The factory will be used
	 * to obtain copies of the Lobby.
	 * 
	 * @param name
	 *            name of the type
	 * @param factory
	 *            factory which creates instances of that type
	 */
	public void registerLobby(String name, Factory<Lobby> factory) {
		lobbyTypes.put(name, factory);
	}

	/**
	 * Registers a new Game type with specified name. The factory will be used
	 * to obtain copies of the Game.
	 * 
	 * @param name
	 *            name of the type
	 * @param factory
	 *            factory which creates instances of that type
	 */
	public void registerGame(String name, Factory<Game> factory) {
		gameTypes.put(name, factory);
	}

	/**
	 * Registers a new Effect type with specified name. The factory will be used
	 * to obtain copies of the Effect.
	 * 
	 * @param name
	 *            name of the type
	 * @param factory
	 *            factory which creates instances of that type
	 */
	public void registerEffect(String name, Factory<Effect> factory) {
		effectTypes.put(name, factory);
	}

}
