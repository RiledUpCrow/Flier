/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api;

import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

import pl.betoncraft.flier.PlayerData;

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
	 * Returns the map containing players by their UUID.
	 * 
	 * @return the map of players
	 */
	public Map<UUID, PlayerData> getPlayers();

	/**
	 * Removes the player from the game.
	 * 
	 * @param player
	 *            player to remove
	 */
	public void removePlayer(Player player);

	/**
	 * This method will be called once the game is forced to end.
	 */
	public void stop();

	/**
	 * Returns a team with this name from the game.
	 * 
	 * @param name name of the team
	 * @return Team with that name
	 */
	public Team getTeam(String name);
	
	/**
	 * @return map containing all teams in this game.
	 */
	public Map<String, Team> getTeams();

}
