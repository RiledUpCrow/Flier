/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api.core;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Manages different configuration files.
 *
 * @author Jakub Sapalski
 */
public interface ConfigManager {

	/**
	 * @return the lobbies.yml configuration
	 */
	public FileConfiguration getLobbies();

	/**
	 * @return the games.yml configuration
	 */
	public FileConfiguration getGames();

	/**
	 * @return the bonuses.yml configuration
	 */
	public FileConfiguration getBonuses();

	/**
	 * @return the engines.yml configuration
	 */
	public FileConfiguration getEngines();

	/**
	 * @return the wings.yml configuration
	 */
	public FileConfiguration getWings();

	/**
	 * @return the items.yml configuration
	 */
	public FileConfiguration getItems();

	/**
	 * @return the actions.yml configuration
	 */
	public FileConfiguration getActions();

	/**
	 * @return the activators.yml configuration
	 */
	public FileConfiguration getActivators();

	/**
	 * @return the modifications.yml configuration
	 */
	public FileConfiguration getModifications();

	/**
	 * @return the effects.yml configuration
	 */
	public FileConfiguration getEffects();

	/**
	 * Saves the lobbies.yml file.
	 */
	public void saveLobbies();

	/**
	 * Saves the games.yml file.
	 */
	public void saveGames();

	/**
	 * Saves the bonuses.yml file.
	 */
	public void saveBonuses();

	/**
	 * Saves the engines.yml file.
	 */
	public void saveEngines();

	/**
	 * Saves the wings.yml file.
	 */
	public void saveWings();

	/**
	 * Saves the items.yml file.
	 */
	public void saveItems();

	/**
	 * Saves the actions.yml file.
	 */
	public void saveActions();

	/**
	 * Saves the activators.yml file.
	 */
	public void saveActivators();
	
	/**
	 * Saves the modifications.yml file.
	 */
	public void saveModifications();
	
	/**
	 * Saves the effects.yml file.
	 */
	public void saveEffects();

}