/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api;

import java.util.Map;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

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
	public void addPlayer(Player player);

	/**
	 * Removes the player from the game.
	 * 
	 * @param player
	 *            player to remove
	 */
	public void removePlayer(Player player);
	
	/**
	 * Starts the game for a player, moving him from the lobby to the actual game.
	 * 
	 * @param player player to start a game
	 */
	public void startPlayer(Player player);
	
	/**
	 * Returns the map containing players by their UUID.
	 * 
	 * @return the map of players
	 */
	public Map<UUID, InGamePlayer> getPlayers();
	
	/**
	 * Returns the Attitude of one player towards another. The game should
	 * tell if these players are enemies or friends, so other components
	 * can behave accordingly.
	 * 
	 * @param toThisOne
	 * @param ofThisOne
	 * @return
	 */
	public Attitude getAttitude(InGamePlayer toThisOne, InGamePlayer ofThisOne);

	/**
	 * This method will be called once the game is forced to end.
	 */
	public void stop();

	/**
	 * Returns a map where the key is player's name and the value is his color.
	 * Each game should manage its player's colors, so they can be displayed
	 * when using engine.
	 * 
	 * @return the map with colors assigned to player names
	 */
	public Map<String, ChatColor> getColors();
	
	public enum Attitude {
		FRIENDLY, NEUTRAL, HOSTILE
	}

}
