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

/**
 * Represents a lobby.
 *
 * @author Jakub Sapalski
 */
public interface Lobby {

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
	 * @return a set with UUIDs of players in this Lobby
	 */
	public Set<UUID> getPlayers();

	/**
	 * Called when the game ends.
	 */
	public void stop();

	/**
	 * Sets the game for this lobby.
	 * 
	 * @param game
	 */
	void setGame(Game game);

	/**
	 * @return the current game
	 */
	public Game getGame();

	/**
	 * @return the map of games and their names
	 */
	public Map<String, Game> getGames();

	/**
	 * @return the spawn location of the lobby
	 */
	public Location getSpawn();

}
