/**
 * Copyright (c) 2017 Jakub Sapalski
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
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
	 * @return the sets.yml configuration
	 */
	public FileConfiguration getItemSets();

	/**
	 * @return the arenas.yml configuration
	 */
	public FileConfiguration getArenas();

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

	/**
	 * Saves the sets.yml file.
	 */
	public void saveItemSets();
	
	/**
	 * Saves the arenas.yml file.
	 */
	public void saveArenas();

}