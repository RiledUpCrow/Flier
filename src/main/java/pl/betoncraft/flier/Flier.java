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

import pl.betoncraft.flier.action.EffectAction;
import pl.betoncraft.flier.action.FuelAction;
import pl.betoncraft.flier.action.LaunchAction;
import pl.betoncraft.flier.action.MoneyAction;
import pl.betoncraft.flier.action.TargetAction;
import pl.betoncraft.flier.action.WingsHealthAction;
import pl.betoncraft.flier.action.attack.AutoDestruction;
import pl.betoncraft.flier.action.attack.HomingMissile;
import pl.betoncraft.flier.action.attack.MachineGun;
import pl.betoncraft.flier.activator.LeftClickActivator;
import pl.betoncraft.flier.activator.RightClickActivator;
import pl.betoncraft.flier.activator.SlowTickActivator;
import pl.betoncraft.flier.activator.WingsHealthActivator;
import pl.betoncraft.flier.api.Action;
import pl.betoncraft.flier.api.Activator;
import pl.betoncraft.flier.api.Bonus;
import pl.betoncraft.flier.api.Damager;
import pl.betoncraft.flier.api.Engine;
import pl.betoncraft.flier.api.Game;
import pl.betoncraft.flier.api.Lobby;
import pl.betoncraft.flier.api.UsableItem;
import pl.betoncraft.flier.api.Wings;
import pl.betoncraft.flier.bonus.EntityBonus;
import pl.betoncraft.flier.command.FlierCommand;
import pl.betoncraft.flier.core.DefaultUsableItem;
import pl.betoncraft.flier.engine.MultiplyingEngine;
import pl.betoncraft.flier.exception.LoadingException;
import pl.betoncraft.flier.game.TeamDeathMatch;
import pl.betoncraft.flier.lobby.PhysicalLobby;
import pl.betoncraft.flier.util.ConfigManager;
import pl.betoncraft.flier.util.Utils;
import pl.betoncraft.flier.wings.SimpleWings;

public class Flier extends JavaPlugin {

	private static Flier instance;
	
	private ConfigManager configManager;
	private FlierCommand flierCommand;

	private Map<String, Factory<Engine>> engineTypes = new HashMap<>();
	private Map<String, Factory<Wings>> wingTypes = new HashMap<>();
	private Map<String, Factory<Game>> gameTypes = new HashMap<>();
	private Map<String, Factory<Lobby>> lobbyTypes = new HashMap<>();
	private Map<String, Factory<Bonus>> bonusTypes = new HashMap<>();
	private Map<String, Factory<Action>> actionTypes = new HashMap<>();
	private Map<String, Factory<Activator>> activatorTypes = new HashMap<>();
	
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
		configManager = new ConfigManager();
		flierCommand = new FlierCommand();

		// register new types
		registerEngine("multiplyingEngine", s -> new MultiplyingEngine(s));
		registerWings("simpleWings", s -> new SimpleWings(s));
		registerLobby("physicalLobby", s -> new PhysicalLobby(s));
		registerGame("teamDeathMatch", s -> new TeamDeathMatch(s));
		registerBonus("entity", s -> new EntityBonus(s));
		registerAction("machineGun", s -> new MachineGun(s));
		registerAction("homingMissile", s -> new HomingMissile(s));
		registerAction("autoDestruction", s -> new AutoDestruction(s));
		registerAction("launcher", s -> new LaunchAction(s));
		registerAction("effect", s -> new EffectAction(s));
		registerAction("money", s -> new MoneyAction(s));
		registerAction("wingsHealth", s -> new WingsHealthAction(s));
		registerAction("fuel", s -> new FuelAction(s));
		registerAction("targetCompass", s -> new TargetAction(s));
		registerActivator("leftClick", s -> new LeftClickActivator(s));
		registerActivator("rightClick", s -> new RightClickActivator(s));
		registerActivator("slowTick", s -> new SlowTickActivator(s));
		registerActivator("wingsHealth", s -> new WingsHealthActivator(s));

		// load stuff
		reload();
		
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
		
		// TODO add after-crash player restore
	}

	public void reload() {
		reloadConfig();
		configManager = new ConfigManager();
		for (Lobby lobby : lobbies.values()) {
			lobby.stop();
		}
		lobbies.clear();
		ConfigurationSection lobbySection = configManager.getLobbies();
		if (lobbySection != null) {
			for (String section : lobbySection.getKeys(false)) {
				try {
					lobbies.put(section, getObject(section, "lobby", lobbySection, lobbyTypes));
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
	 * @return the instance of ConfigManager
	 */
	public ConfigManager getConfigManager() {
		return configManager;
	}
	
	/**
	 * @return the instance of Flier command
	 */
	public FlierCommand getFlierCommand() {
		return flierCommand;
	}

	/**
	 * @return an immutable view of the lobbies map
	 */
	public Map<String, Lobby> getLobbies() {
		return Collections.unmodifiableMap(lobbies);
	}

	/**
	 * @param id ID of the Engine
	 * @return the Engine with specified name, never null
	 * @throws LoadingException
	 *             when the Engine cannot be created due to an error
	 */
	public Engine getEngine(String id) throws LoadingException {
		return getObject(id, "engine", configManager.getEngines(), engineTypes);
	}

	/**
	 * @param id ID of the Item
	 * @return the Item with specified name, never null
	 * @throws LoadingException
	 *             when the Item cannot be created due to an error
	 */
	public UsableItem getItem(String id) throws LoadingException {
		ConfigurationSection section = configManager.getItems().getConfigurationSection(id);
		if (section == null) {
			throw new LoadingException(String.format("Item with ID '%s' does not exist.", id));
		}
		try {
			return new DefaultUsableItem(section);
		} catch (LoadingException e) {
			throw (LoadingException) new LoadingException(String.format("Error in '%s' item.", id)).initCause(e);
		}
	}

	/**
	 * @param id ID of the Wings
	 * @return the Wings with specified name, never null
	 * @throws LoadingException
	 *             when the Wings cannot be created due to an error
	 */
	public Wings getWing(String id) throws LoadingException {
		return getObject(id, "wing", configManager.getWings(), wingTypes);
	}
	
	/**
	 * @param id ID of the Game
	 * @return the Game with specified name, never null
	 * @throws LoadingException
	 *             when the Game cannot be created due to an error
	 */
	public Game getGame(String id) throws LoadingException {
		return getObject(id, "game", configManager.getGames(), gameTypes);
	}
	
	/**
	 * @param id ID of the Action
	 * @return the Action with specified name, never null
	 * @throws LoadingException
	 *             when the Action cannot be created due to an error
	 */
	public Action getAction(String id) throws LoadingException {
		return getObject(id, "action", configManager.getActions(), actionTypes);
	}
	
	/**
	 * @param id ID of the Activator
	 * @return the Activator with specified name, never null
	 * @throws LoadingException
	 *             when the Activator cannot be created due to an error
	 */
	public Activator getActivator(String id) throws LoadingException {
		return getObject(id, "activator", configManager.getActivators(), activatorTypes);
	}
	
	/**
	 * @param id ID of the Bonus
	 * @return the Bonus with specified name, never null
	 * @throws LoadingException
	 *             when the Bonus cannot be created due to an error, type is not defined or Bonus is not defined
	 */
	public Bonus getBonus(String id) throws LoadingException {
		return getObject(id, "bonus", configManager.getBonuses(), bonusTypes);
	}

	private <T> T getObject(String id, String name, ConfigurationSection section, Map<String, Factory<T>> factories)
			throws LoadingException {
		name = Utils.capitalize(name);
		ConfigurationSection config = section.getConfigurationSection(id);
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
	
	/**
	 * Registers a new Action type with specified name. The factory will be used
	 * to obtain copies of the Action.
	 * 
	 * @param name
	 *            name of the type
	 * @param factory
	 *            factory which creates instances of that type
	 */
	public void registerAction(String name, Factory<Action> factory) {
		actionTypes.put(name, factory);
	}
	
	/**
	 * Registers a new Activator type with specified name. The factory will be used
	 * to obtain copies of the Activator.
	 * 
	 * @param name
	 *            name of the type
	 * @param factory
	 *            factory which creates instances of that type
	 */
	public void registerActivator(String name, Factory<Activator> factory) {
		activatorTypes.put(name, factory);
	}

}
