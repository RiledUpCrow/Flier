/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api.content;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import pl.betoncraft.flier.api.core.Arena;
import pl.betoncraft.flier.api.core.Damager;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.SetApplier;

/**
 * Represents a game.
 *
 * @author Jakub Sapalski
 */
public interface Game {

	/**
	 * The Button in a Lobby, which has costs and can be locked.
	 */
	public interface Button {
		
		/**
		 * @return the name of Location of this Button
		 */
		public String getLocationName();
		
		/**
		 * @return the Location of this Button
		 */
		public Location getLocation();
		
		/**
		 * @param location the Location of this Bonus
		 */
		public void setLocation(Location location);

		/**
		 * @return the set of Button names required for unlocking this Button
		 */
		public Set<String> getRequirements();

		/**
		 * @return the cost to buy an ItemSet
		 */
		public int getBuyCost();

		/**
		 * @return the cost to sell an ItemSet
		 */
		public int getSellCost();

		/**
		 * @return the cost to unlock this Button
		 */
		public int getUnlockCost();

		/**
		 * @return the SetApplier for buying
		 */
		public SetApplier getOnBuy();

		/**
		 * @return the SetApplier for selling
		 */
		public SetApplier getOnSell();

		/**
		 * @return the SetApplier for unlocking
		 */
		public SetApplier getOnUnlock();
	}

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
	 *            whenever the player wants to buy (true) or sell (false)
	 * @param message
	 *            whenever to display a message
	 */
	public boolean applyButton(InGamePlayer player, Button button, boolean buy, boolean message);

	/**
	 * Adds the player to the game. It will throw IllegalStateException if the
	 * player is already in the game or the game is locked. If you want to add
	 * the player easily, use {@link Lobby#joinGame(Player, String) joinGame}
	 * method instead.
	 * 
	 * @param player
	 *            player to add
	 */
	public InGamePlayer addPlayer(Player player);

	/**
	 * Removes the player from the game.
	 * 
	 * @param player
	 *            player to remove
	 */
	public void removePlayer(Player player);

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
	 * @return the Lobby this Game is in
	 */
	public Lobby getLobby();

	/**
	 * This method must be used on newly created Game to let it know what
	 * created it.
	 * 
	 * @param lobby
	 *            the Lobby this Game is in
	 */
	public void setLobby(Lobby lobby);

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
	 * @return the height limit in this game; 0 and less means no limit
	 */
	public int getHeightLimit();

	/**
	 * Attitude of one player to another.
	 *
	 * @author Jakub Sapalski
	 */
	public enum Attitude {
		FRIENDLY, NEUTRAL, HOSTILE
	}

	/**
	 * Contains logic for game-specific hit handling. Called by Flier when a
	 * player gets hit with a Damager.
	 * 
	 * @param attacker
	 *            the player who attacked, can be null or equal to attacked
	 * @param attacked
	 *            the player who was attacked
	 * @param damager
	 *            Damager used in the attack
	 */
	public void handleHit(InGamePlayer attacker, InGamePlayer attacked, Damager damager);

	/**
	 * Contains logic for game-specific kill handling. Called by Flier when a
	 * player is killed.
	 * 
	 * @param killer
	 *            the player who killed, can be null or equal to attacked
	 * @param killed
	 *            the player who was killed
	 * @param fall
	 *            whenever the player died because of fall damage
	 */
	public void handleKill(InGamePlayer killer, InGamePlayer killed, boolean fall);

	/**
	 * This method is called for the respawned player. Use it if you want to do
	 * something special after respawning the player, or just pass him to
	 * lobby.respawnPlayer().
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
	 * @param arena Arena to set for this Game or null to remove the Arena
	 */
	public void setArena(Arena arena) throws LoadingException;

	/**
	 * @return the list of names of Arenas which can be used for this Game
	 */
	public List<String> getViableArenas();
	
	/**
	 * @return the maximum amount of players this Game can have or 0 if there is no limit
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
	 * @return the amount of ticks before this game ends.
	 */
	public int getTimeLeft();

}
