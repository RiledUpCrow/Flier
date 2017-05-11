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
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.permissions.Permission;

import pl.betoncraft.flier.api.core.Arena;
import pl.betoncraft.flier.api.core.Attacker;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.SetApplier;
import pl.betoncraft.flier.api.core.Target;
import pl.betoncraft.flier.event.FlierGameEndEvent.GameEndCause;

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
		 * @return the ID of the Button
		 */
		public String getID();
		
		/**
		 * @return the Location of this Button
		 */
		public List<Location> getLocations();
		
		/**
		 * @param location the Location of this Bonus
		 */
		public void setLocations(List<Location> location);

		/**
		 * @return the set of Button names required for unlocking this Button
		 */
		public Set<String> getRequirements();
		
		/**
		 * @return the set of permissions required to use this Button
		 */
		public Set<Permission> getPermissions();

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
	 * @return the ID of this Game
	 */
	public String getID();

	/**
	 * @return the unique number generated for this game, used to distinguish different matches
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
	 *            whenever the player wants to buy (true) or sell (false)
	 * @param message
	 *            whenever to display a message
	 */
	public boolean applyButton(InGamePlayer player, Button button, boolean buy, boolean message);

	/**
	 * Adds the player to the game. It will throw IllegalStateException if the
	 * player is already in the game or the game is locked. If you want to add
	 * the player easily, use {@link Lobby#joinGame(Player, String) joinGame}
	 * method instead. This method is only called by the Lobby.
	 * 
	 * @param player
	 *            player to add
	 */
	public InGamePlayer addPlayer(Player player);

	/**
	 * Removes the player from the game. This method is only called by the Lobby.
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
	 * @return a set containing all targets in this Game
	 */
	public Map<UUID, Target> getTargets();

	/**
	 * This method will be called by the Lobby when the game needs to be started.
	 */
	public void start();

	/**
	 * This method will be called by the Lobby once the game is forced to end.
	 * It should clean up all its data so it can be freshly started again.
	 *
	 * @param cause
	 *            cause of the game ending
	 */
	public void stop(GameEndCause cause);

	/**
	 * @return the Lobby this Game is in
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
		FRIENDLY(0),
		HOSTILE(1),
		NEUTRAL(2);
		private int type;
		private Attitude(int type) {
			this.type = type;
		}
		/**
		 * @return the magic number for database storage
		 */
		public int get() {
			return type;
		}
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
