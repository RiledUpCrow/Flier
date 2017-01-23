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

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;

import pl.betoncraft.flier.api.Bonus;
import pl.betoncraft.flier.api.Damager;
import pl.betoncraft.flier.api.Effect;
import pl.betoncraft.flier.api.Engine;
import pl.betoncraft.flier.api.Game;
import pl.betoncraft.flier.api.Item;
import pl.betoncraft.flier.api.Lobby;
import pl.betoncraft.flier.api.Wings;
import pl.betoncraft.flier.bonus.EffectBonus;
import pl.betoncraft.flier.bonus.MoneyBonus;
import pl.betoncraft.flier.command.FlierCommand;
import pl.betoncraft.flier.core.Utils;
import pl.betoncraft.flier.effect.TargetCompass;
import pl.betoncraft.flier.exception.LoadingException;
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
	private Map<String, Factory<Bonus>> bonusTypes = new HashMap<>();
	
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
		registerBonus("effect", s -> new EffectBonus(s));
		registerBonus("money", s -> new MoneyBonus(s));

		loadLobbies();
		
		// add projectile cleanup listener
		Bukkit.getPluginManager().registerEvents(new Listener() {
			@EventHandler
			public void onChunkUnload(ChunkUnloadEvent event) {
				Entity[] entities = event.getChunk().getEntities();
				for (int i = 0; i < entities.length; i++) {
					if (entities[i] instanceof Projectile && Damager.getDamager((Projectile) entities[i]) != null) {
						entities[i].remove();
					}
				}
			}
		}, this);
	}

	private void loadLobbies() {
		lobbies.clear();
		ConfigurationSection lobbySection = getConfig().getConfigurationSection("lobbies");
		if (lobbySection != null) {
			for (String section : lobbySection.getKeys(false)) {
				try {
					lobbies.put(section, getObject(section, "lobby", "lobbies", lobbyTypes));
				} catch (LoadingException e) {
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
	 * @param id ID of the Engine
	 * @return the Engine with specified name, never null
	 * @throws ObjectUndefinedException
	 *             when the Engine is not defined
	 * @throws TypeUndefinedException
	 *             when the Engine type is not defined
	 * @throws LoadingException
	 *             when the Engine cannot be created due to an error
	 */
	public Engine getEngine(String id) throws LoadingException {
		return getObject(id, "engine", "engines", engineTypes);
	}

	/**
	 * @param id ID of the Item
	 * @return the Item with specified name, never null
	 * @throws ObjectUndefinedException
	 *             when the Item is not defined
	 * @throws TypeUndefinedException
	 *             when the Item type is not defined
	 * @throws LoadingException
	 *             when the Item cannot be created due to an error
	 */
	public Item getItem(String id) throws LoadingException {
		return getObject(id, "item", "items", itemTypes);
	}

	/**
	 * @param id ID of the Wings
	 * @return the Wings with specified name, never null
	 * @throws ObjectUndefinedException
	 *             when the Wings are not defined
	 * @throws TypeUndefinedException
	 *             when the Wings type is not defined
	 * @throws LoadingException
	 *             when the Wings cannot be created due to an error
	 */
	public Wings getWing(String id) throws LoadingException {
		return getObject(id, "wing", "wings", wingTypes);
	}

	/**
	 * @param id ID of the Effect
	 * @return the Effect with specified name, never null
	 * @throws ObjectUndefinedException
	 *             when the Effect is not defined
	 * @throws TypeUndefinedException
	 *             when the Effect type is not defined
	 * @throws LoadingException
	 *             when the Effect cannot be created due to an error
	 */
	public Effect getEffect(String id) throws LoadingException {
		return getObject(id, "effect", "effects", effectTypes);
	}
	
	/**
	 * @param id ID of the Game
	 * @return the Game with specified name, never null
	 * @throws ObjectUndefinedException
	 *             when the Game is not defined
	 * @throws TypeUndefinedException
	 *             when the Game type is not defined
	 * @throws LoadingException
	 *             when the Game cannot be created due to an error
	 */
	public Game getGame(String id) throws LoadingException {
		return getObject(id, "game", "games", gameTypes);
	}
	
	/**
	 * @param id ID of the Bonus
	 * @return the Bonus with specified name, never null
	 * @throws LoadingException
	 *             when the Bonus cannot be created due to an error, type is not defined or Bonus is not defined
	 */
	public Bonus getBonus(String id) throws LoadingException {
		return getObject(id, "bonus", "bonuses", bonusTypes);
	}

	private <T> T getObject(String id, String name, String section, Map<String, Factory<T>> factories)
			throws LoadingException {
		name = Utils.capitalize(name);
		ConfigurationSection config = getConfig().getConfigurationSection(String.format("%s.%s", section, id));
		if (config == null || config.getKeys(false).size() == 0) {
			throw new LoadingException(String.format("%s with ID '%s' does not exist.", name, id));
		}
		String type = config.getString("type");
		Factory<T> factory = factories.get(type);
		if (factory == null) {
			throw new LoadingException(String.format("%s type '%s' does not exist.", name, type));
		}
		try {
			return factory.get(config);
		} catch (LoadingException e) {
			throw (LoadingException) new LoadingException(String.format("Error in '%s' %s.", id, name)).initCause(e);
		}
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
	
	/**
	 * Registers a new Bonus type with specified name. The factory will be used
	 * to obtain copies of the Bonus.
	 * 
	 * @param name
	 *            name of the type
	 * @param factory
	 *            factory which creates instances of that type
	 */
	public void registerBonus(String name, Factory<Bonus> factory) {
		bonusTypes.put(name, factory);
	}

}
