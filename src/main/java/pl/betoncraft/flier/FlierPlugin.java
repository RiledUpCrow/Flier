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
import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Action;
import pl.betoncraft.flier.api.content.Activator;
import pl.betoncraft.flier.api.content.Bonus;
import pl.betoncraft.flier.api.content.Engine;
import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.api.content.Lobby;
import pl.betoncraft.flier.api.content.Wings;
import pl.betoncraft.flier.api.core.ConfigManager;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.UsableItem;
import pl.betoncraft.flier.bonus.EntityBonus;
import pl.betoncraft.flier.command.FlierCommand;
import pl.betoncraft.flier.core.DefaultUsableItem;
import pl.betoncraft.flier.engine.MultiplyingEngine;
import pl.betoncraft.flier.game.TeamDeathMatch;
import pl.betoncraft.flier.lobby.PhysicalLobby;
import pl.betoncraft.flier.util.DefaultConfigManager;
import pl.betoncraft.flier.util.Utils;
import pl.betoncraft.flier.wings.SimpleWings;

public class FlierPlugin extends JavaPlugin implements Flier {
	
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

	@Override
	public void onEnable() {
		saveDefaultConfig();
		configManager = new DefaultConfigManager();
		flierCommand = new FlierCommand();
		getCommand("flier").setExecutor(flierCommand);

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
					if (entities[i] instanceof Projectile && Utils.getDamager((Projectile) entities[i]) != null) {
						entities[i].remove();
					}
				}
			}
		}, this);
		
		// TODO add after-crash player restore
	}

	@Override
	public void onDisable() {
		for (Lobby lobby : lobbies.values()) {
			lobby.stop();
		}
	}

	@Override
	public void reload() {
		reloadConfig();
		configManager = new DefaultConfigManager();
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
	public ConfigManager getConfigManager() {
		return configManager;
	}

	@Override
	public Map<String, Lobby> getLobbies() {
		return Collections.unmodifiableMap(lobbies);
	}

	@Override
	public Engine getEngine(String id) throws LoadingException {
		return getObject(id, "engine", configManager.getEngines(), engineTypes);
	}

	@Override
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

	@Override
	public Wings getWing(String id) throws LoadingException {
		return getObject(id, "wing", configManager.getWings(), wingTypes);
	}
	
	@Override
	public Game getGame(String id) throws LoadingException {
		return getObject(id, "game", configManager.getGames(), gameTypes);
	}
	
	@Override
	public Action getAction(String id) throws LoadingException {
		return getObject(id, "action", configManager.getActions(), actionTypes);
	}
	
	@Override
	public Activator getActivator(String id) throws LoadingException {
		return getObject(id, "activator", configManager.getActivators(), activatorTypes);
	}
	
	@Override
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

	@Override
	public void registerEngine(String name, Factory<Engine> factory) {
		engineTypes.put(name, factory);
	}

	@Override
	public void registerWings(String name, Factory<Wings> factory) {
		wingTypes.put(name, factory);
	}

	@Override
	public void registerLobby(String name, Factory<Lobby> factory) {
		lobbyTypes.put(name, factory);
	}

	@Override
	public void registerGame(String name, Factory<Game> factory) {
		gameTypes.put(name, factory);
	}
	
	@Override
	public void registerBonus(String name, Factory<Bonus> factory) {
		bonusTypes.put(name, factory);
	}
	
	@Override
	public void registerAction(String name, Factory<Action> factory) {
		actionTypes.put(name, factory);
	}
	
	@Override
	public void registerActivator(String name, Factory<Activator> factory) {
		activatorTypes.put(name, factory);
	}

}
