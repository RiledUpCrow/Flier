/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import pl.betoncraft.flier.api.content.Action;
import pl.betoncraft.flier.api.content.Activator;
import pl.betoncraft.flier.api.content.Bonus;
import pl.betoncraft.flier.api.content.Effect;
import pl.betoncraft.flier.api.content.Engine;
import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.api.content.Lobby;
import pl.betoncraft.flier.api.content.Wings;
import pl.betoncraft.flier.api.core.ConfigManager;
import pl.betoncraft.flier.api.core.ItemSet;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.Modification;
import pl.betoncraft.flier.api.core.UsableItem;

/**
 * The Flier plugin.
 *
 * @author Jakub Sapalski
 */
public interface Flier extends Plugin {

	/**
	 * Gets the instance of the Flier plugin. This will return null if it
	 * can't find the plugin for some reason.
	 */
	public static Flier getInstance() {
		Plugin flier = Bukkit.getPluginManager().getPlugin("Flier");
		if (flier == null || !(flier instanceof Flier)) {
			return null;
		} else {
			return (Flier) flier;
		}
	}

	/**
	 * Reloads the plugin.
	 */
	public void reload();

	/**
	 * @return the instance of ConfigManager
	 */
	public ConfigManager getConfigManager();

	/**
	 * @return an immutable view of the lobbies map
	 */
	public Map<String, Lobby> getLobbies();

	/**
	 * @param id ID of the Engine
	 * @return the Engine with specified name, never null
	 * @throws LoadingException
	 *             when the Engine cannot be created due to an error
	 */
	public Engine getEngine(String id) throws LoadingException;

	/**
	 * @param id ID of the Item
	 * @return the Item with specified name, never null
	 * @throws LoadingException
	 *             when the Item cannot be created due to an error
	 */
	public UsableItem getItem(String id) throws LoadingException;

	/**
	 * @param id ID of the Wings
	 * @return the Wings with specified name, never null
	 * @throws LoadingException
	 *             when the Wings cannot be created due to an error
	 */
	public Wings getWing(String id) throws LoadingException;

	/**
	 * @param id ID of the Game
	 * @return the Game with specified name, never null
	 * @throws LoadingException
	 *             when the Game cannot be created due to an error
	 */
	public Game getGame(String id) throws LoadingException;

	/**
	 * @param id ID of the Action
	 * @return the Action with specified name, never null
	 * @throws LoadingException
	 *             when the Action cannot be created due to an error
	 */
	public Action getAction(String id) throws LoadingException;

	/**
	 * @param id ID of the Activator
	 * @return the Activator with specified name, never null
	 * @throws LoadingException
	 *             when the Activator cannot be created due to an error
	 */
	public Activator getActivator(String id) throws LoadingException;

	/**
	 * @param id ID of the Bonus
	 * @return the Bonus with specified name, never null
	 * @throws LoadingException
	 *             when the Bonus cannot be created due to an error, type is not defined or Bonus is not defined
	 */
	public Bonus getBonus(String id) throws LoadingException;
	
	/**
	 * @param id ID of the Modification
	 * @return the Modification with specified name, never null
	 * @throws LoadingException
	 *             when the Modification cannot be created due to an error or Modification is not defined
	 */
	public Modification getModification(String id) throws LoadingException;

	/**
	 * @param id ID of the ItemSet
	 * @return the ItemSet with specified name, never null
	 * @throws LoadingException
	 *             when the ItemSet cannot be created due to an error or ItemSet is not defined
	 */
	public ItemSet getItemSet(String id) throws LoadingException;

	/**
	 * @param id ID of the Effect
	 * @return the Effect with specified name, never null
	 * @throws LoadingException
	 *             when the Effect cannot be created due to an error, type is not defined or Effect is not defined
	 */
	public Effect getEffect(String id) throws LoadingException;

	/**
	 * Registers a new Engine type with specified name. The factory will be used
	 * to obtain copies of the Engine.
	 * 
	 * @param name
	 *            name of the type
	 * @param factory
	 *            factory which creates instances of that type
	 */
	public void registerEngine(String name, Factory<Engine> factory);

	/**
	 * Registers a new Wings type with specified name. The factory will be used
	 * to obtain copies of the Wings.
	 * 
	 * @param name
	 *            name of the type
	 * @param factory
	 *            factory which creates instances of that type
	 */
	public void registerWings(String name, Factory<Wings> factory);

	/**
	 * Registers a new Lobby type with specified name. The factory will be used
	 * to obtain copies of the Lobby.
	 * 
	 * @param name
	 *            name of the type
	 * @param factory
	 *            factory which creates instances of that type
	 */
	public void registerLobby(String name, Factory<Lobby> factory);

	/**
	 * Registers a new Game type with specified name. The factory will be used
	 * to obtain copies of the Game.
	 * 
	 * @param name
	 *            name of the type
	 * @param factory
	 *            factory which creates instances of that type
	 */
	public void registerGame(String name, Factory<Game> factory);

	/**
	 * Registers a new Bonus type with specified name. The factory will be used
	 * to obtain copies of the Bonus.
	 * 
	 * @param name
	 *            name of the type
	 * @param factory
	 *            factory which creates instances of that type
	 */
	public void registerBonus(String name, Factory<Bonus> factory);

	/**
	 * Registers a new Action type with specified name. The factory will be used
	 * to obtain copies of the Action.
	 * 
	 * @param name
	 *            name of the type
	 * @param factory
	 *            factory which creates instances of that type
	 */
	public void registerAction(String name, Factory<Action> factory);

	/**
	 * Registers a new Activator type with specified name. The factory will be used
	 * to obtain copies of the Activator.
	 * 
	 * @param name
	 *            name of the type
	 * @param factory
	 *            factory which creates instances of that type
	 */
	public void registerActivator(String name, Factory<Activator> factory);

	/**
	 * Registers a new Effect type with specified name. The factory will be used
	 * to obtain copies of the Effect.
	 * 
	 * @param name
	 *            name of the type
	 * @param factory
	 *            factory which creates instances of that type
	 */
	void registerEffect(String name, Factory<Effect> factory);

	/**
	 * Factory which creates instances of a type, using ConfigurationSections to
	 * get data.
	 *
	 * @author Jakub Sapalski
	 */
	public interface Factory<T> {
		public T get(ConfigurationSection settings) throws LoadingException;
	}

}