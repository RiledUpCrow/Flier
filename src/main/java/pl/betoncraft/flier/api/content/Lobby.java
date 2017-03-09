/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api.content;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.SetApplier;

/**
 * Represents a lobby.
 *
 * @author Jakub Sapalski
 */
public interface Lobby {
	
	/**
	 * The Button in a Lobby, which has costs and can be locked.
	 */
	public interface Button {

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
	 * Teleports the player to the lobby spawn and prepares him for further
	 * playing, according to RespawnAction. It does not actually respawns the
	 * player, because this is the Game's responsibility. The game can then pass
	 * the player to the Lobby when it decides to using this method.
	 * 
	 * @param player
	 *            player to respawn in the lobby
	 */
	public void respawnPlayer(InGamePlayer player);

	/**
	 * Called when the game ends.
	 */
	public void stop();

	/**
	 * @return the map containing all Buttons in this Game and their names.
	 */
	public Map<String, Button> getButtons();

	/**
	 * Applies the specified Button to the player.
	 * 
	 * @param player the player
	 * @param button the button to apply
	 * @param buy whenever the player wants to buy (true) or sell (false)
	 * @param message whenever to display a message
	 */
	public boolean applyButton(InGamePlayer player, Button button, boolean buy, boolean message);

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
