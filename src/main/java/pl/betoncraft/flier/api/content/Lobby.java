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
package pl.betoncraft.flier.api.content;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import pl.betoncraft.flier.api.core.Arena;
import pl.betoncraft.flier.api.core.Named;
import pl.betoncraft.flier.event.FlierGameEndEvent.GameEndCause;

/**
 * Represents a lobby, which can create Games on Arenas and assign players to
 * those Games.
 *
 * @author Jakub Sapalski
 */
public interface Lobby extends Named {

	public enum JoinResult {
		GAME_CREATED, GAME_JOINED, GAMES_FULL, NO_SUCH_GAME, ALREADY_IN_GAME, BLOCKED
	}

	/**
	 * @return whenever the Lobby is open for players or not
	 */
	public boolean isOpen();

	/**
	 * Opens or closes the Lobby.
	 *
	 * @param open
	 *            whenever the Lobby should be open or closed
	 */
	public void setOpen(boolean open);

	/**
	 * Adds player to the lobby.
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
	 * Tries to move this player to the Game with specified name. It doesn't
	 * guarantee success, the result will be returned. This method will create a
	 * new Game if it needs (and is able) to.
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
	 * Ends the game. It will stop this Game, remove remove it from the Lobby
	 * and release its arena.
	 * 
	 * @param game
	 *            Game to end
	 * @param cause
	 *            cause of the game ending
	 */
	public void endGame(Game game, GameEndCause cause);

	/**
	 * @return a set with UUIDs of players in this Lobby
	 */
	public Set<UUID> getPlayers();

	/**
	 * Stops all Games and removes all players from this Lobby.
	 */
	public void stop();

	/**
	 * @return the map of Game lists by their names; each Game can run in
	 *         multiple instances at once, that's why there are lists
	 */
	public Map<String, List<Game>> getGames();

	/**
	 * @return the map of Arenas and their names
	 */
	public Map<String, Arena> getArenas();

	/**
	 * @return the spawn location of the lobby
	 */
	public Location getSpawn();

}
