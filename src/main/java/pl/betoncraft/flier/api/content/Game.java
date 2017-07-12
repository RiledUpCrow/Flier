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
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import pl.betoncraft.flier.api.core.Arena;
import pl.betoncraft.flier.api.core.Attacker;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.Named;
import pl.betoncraft.flier.api.core.Target;
import pl.betoncraft.flier.event.FlierGameEndEvent.GameEndCause;

/**
 * Represents a game.
 *
 * @author Jakub Sapalski
 */
public interface Game extends Named {

	/**
	 * @return the unique number generated for this game, used to distinguish
	 *         different matches
	 */
	public int getUniqueNumber();

	/**
	 * @return the map containing all Buttons in this Game and their names.
	 */
	public Map<String, Button> getButtons();

	/**
	 * Applies the specified Button to the player.
	 * 
	 * @param player
	 *            the player
	 * @param button
	 *            the button to apply
	 * @param buy
	 *            whenever the player wants to buy (true) or sell (false);
	 *            unlocking gets triggered by both
	 * @param message
	 *            whenever to display a message
	 */
	public boolean applyButton(InGamePlayer player, Button button, boolean buy, boolean message);

	/**
	 * Adds the player to the game. It will throw IllegalStateException if the
	 * player is already in the Game or the Game is locked. If you want to add
	 * the player easily, use {@link Lobby#joinGame(Player, String) joinGame}
	 * method instead. This method is only called by the Lobby.
	 * 
	 * @param player
	 *            player to add
	 * @throws IllegalStateException
	 *             when the player is already in the Game or the Game is locked
	 */
	public InGamePlayer addPlayer(Player player) throws IllegalStateException;

	/**
	 * Removes the player from the game. This method is only called by the
	 * Lobby.
	 * 
	 * @param player
	 *            player to remove
	 */
	public void removePlayer(Player player);

	/**
	 * @return the map containing players in this Game by their UUID
	 */
	public Map<UUID, InGamePlayer> getPlayers();

	/**
	 * @return a map containing all targets in this Game by their UUID
	 */
	public Map<UUID, Target> getTargets();

	/**
	 * Modifies the amount of points of the player specified by the UUID. This
	 * method will return false if the player is not in the Game.
	 * 
	 * @param player
	 *            the UUID of the player
	 * @param amount
	 *            the amount of points to modify (can be negative)
	 * @return whenever the points were correctly modified
	 */
	public boolean modifyPoints(UUID player, int amount);

	/**
	 * This method will be called by the Lobby when the game needs to be
	 * started.
	 */
	public void start();

	/**
	 * This method will be called by the Lobby once the game is forced to end.
	 *
	 * @param cause
	 *            cause of the game ending
	 */
	public void stop(GameEndCause cause);

	/**
	 * @return the Lobby which started this Game
	 */
	public Lobby getLobby();

	/**
	 * Returns the Attitude of one player towards another. The game should tell
	 * if these players are enemies or friends, so other components can behave
	 * accordingly.
	 * 
	 * @param toThisOne
	 *            the attitude towards this player will be returned
	 * @param ofThisOne
	 *            this player's attitude will be returned
	 * @return the attitude of the second player to the first one (this relation
	 *         is usually symmetric)
	 */
	public Attitude getAttitude(Target toThisOne, Target ofThisOne);

	/**
	 * Returns a map where the key is player's name and the value is his color.
	 * Each game should manage its player's colors, so they can be displayed for
	 * players correctly.
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
		FRIENDLY, HOSTILE, NEUTRAL;
	}

	/**
	 * Contains logic for game-specific hit handling. Called by Flier when a
	 * Target gets hit with an Attacker.
	 * 
	 * @param target
	 *            Target which was hit
	 * @param attacker
	 *            the Attacker which is responsible for the hit
	 */
	public void handleHit(Target target, Attacker attacker);

	/**
	 * Contains logic for game-specific kill handling. Called by Flier when a
	 * player is killed.
	 * 
	 * @param killed
	 *            the player who got killed
	 * @param cause
	 *            the direct cause of death
	 */
	public void handleKill(InGamePlayer killed, DamageCause cause);

	/**
	 * This method is called for the respawned player. Use it to correctly
	 * respawn that player.
	 * 
	 * @param player
	 *            the player who has just respawned
	 */
	public void handleRespawn(InGamePlayer player);

	/**
	 * @return the location of the Game's center
	 */
	public Location getCenter();

	/**
	 * @return the Arena currently used by this Game
	 */
	public Arena getArena();

	/**
	 * @return the maximum amount of players this Game can have or 0 if there is
	 *         no limit
	 */
	public int getMaxPlayers();

	/**
	 * @return whenever the Game is currently running
	 */
	public boolean isRunning();

	/**
	 * @return whenever the Game is currently locked for new players
	 */
	public boolean isLocked();

	/**
	 * @return the amount of ticks before this game ends
	 */
	public int getTimeLeft();

}
