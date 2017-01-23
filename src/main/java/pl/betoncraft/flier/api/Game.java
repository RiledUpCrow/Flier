/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;

/**
 * Represents a game.
 *
 * @author Jakub Sapalski
 */
public interface Game {

	/**
	 * Adds the player to the game.
	 * 
	 * @param player
	 *            player to add
	 */
	public void addPlayer(InGamePlayer player);

	/**
	 * Removes the player from the game.
	 * 
	 * @param player
	 *            player to remove
	 */
	public void removePlayer(InGamePlayer player);

	/**
	 * Moves the player from the lobby to the game.
	 * 
	 * @param data
	 *            player to start
	 */
	void startPlayer(InGamePlayer data);
	
	/**
	 * Returns the map containing players by their UUID.
	 * 
	 * @return the map of players
	 */
	public Map<UUID, InGamePlayer> getPlayers();
	
	/**
	 * This method will be called when the game needs to be started.
	 */
	public void start();

	/**
	 * This method will be called once the game is forced to end. It should
	 * clean up all its data so it can be freshly started again.
	 */
	public void stop();
	
	/**
	 * Returns the Attitude of one player towards another. The game should
	 * tell if these players are enemies or friends, so other components
	 * can behave accordingly.
	 * 
	 * @param toThisOne
	 * @param ofThisOne
	 * @return the attitude
	 */
	public Attitude getAttitude(InGamePlayer toThisOne, InGamePlayer ofThisOne);

	/**
	 * Returns a map where the key is player's name and the value is his color.
	 * Each game should manage its player's colors, so they can be displayed
	 * when using engine.
	 * 
	 * @return the map with colors assigned to player names
	 */
	public Map<String, ChatColor> getColors();
	
	/**
	 * @return the list of Bonuses in this Game
	 */
	public List<Bonus> getBonuses();
	
	/**
	 * Attitude of one player to another.
	 *
	 * @author Jakub Sapalski
	 */
	public enum Attitude {
		FRIENDLY, NEUTRAL, HOSTILE
	}

}
