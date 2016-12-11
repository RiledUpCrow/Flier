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
import org.bukkit.Location;
import org.bukkit.entity.Player;

import pl.betoncraft.flier.core.PlayerData;

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
	 * Sets the class for this player.
	 * 
	 * @param player player to set the class
	 * @param clazz class to set
	 */
	public void setClass(Player player, PlayerClass clazz);
	
	/**
	 * Returns the map containing players by their UUID.
	 * 
	 * @return the map of players
	 */
	public Map<UUID, PlayerData> getPlayers();
	
	/**
	 * Handles one player killing another one.
	 * In case of suicide killer is null.
	 * 
	 * @param killer the player who killed another
	 * @param killed the player who was killed
	 */
	public void handleKill(PlayerData killer, PlayerData killed);
	
	/**
	 * Returns a respawn location for the player.
	 * 
	 * @param respawned the player who needs respawning
	 */
	public Location respawnLocation(PlayerData respawned);

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

}
