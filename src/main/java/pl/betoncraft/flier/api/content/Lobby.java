/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api.content;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import pl.betoncraft.flier.api.core.Arena;

/**
 * Represents a lobby.
 *
 * @author Jakub Sapalski
 */
public interface Lobby {
	
	public enum JoinResult {
		GAME_CREATED, GAME_JOINED, GAMES_FULL, NO_SUCH_GAME, ALREADY_IN_GAME, BLOCKED
	}

	/**
	 * @return the ID of this lobby
	 */
	public String getID();

	/**
	 * Adds player to the lobby. Creates an InGamePlayer instance and adds the
	 * player to the current Game.
	 * 
	 * @param player
	 *            the player to add to this lobby
	 */
	public void addPlayer(Player player);

	/**
	 * Removes player from the current Game, clears him and removes him from
	 * this Lobby.
	 * 
	 * @param player
	 *            the player to remove from this lobby
	 */
	public void removePlayer(Player player);

	/**
	 * Tries to move this player to the Game with specified name. If it fails
	 * for some reason (Games are full and no available Arenas to create new
	 * Game), it will display a message and return false. This method will
	 * create a new Game if it needs to.
	 * 
	 * @param player
	 *            Player to add to the Game
	 * @param gameName
	 *            name of the Game
	 * @return the result of the join
	 */
	public JoinResult joinGame(Player player, String gameName);

	/**
	 * Makes the player leave the game he's currently in. Removes the game if
	 * it's empty.
	 * 
	 * @param player
	 *            Player to remove from his current Game
	 */
	public void leaveGame(Player player);

	/**
	 * @return a set with UUIDs of players in this Lobby
	 */
	public Set<UUID> getPlayers();

	/**
	 * Called when the game ends.
	 */
	public void stop();

	/**
	 * @return the map of games and their names
	 */
	public Map<String, Set<Game>> getGames();

	/**
	 * @return the map of Arenas and their names
	 */
	public Map<String, Arena> getArenas();

	/**
	 * @return the spawn location of the lobby
	 */
	public Location getSpawn();

}
