/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api.core;

import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * Represents a player who is in a Game.
 *
 * @author Jakub Sapalski
 */
public interface InGamePlayer extends Target {

	/**
	 * Represents a result of player being hit by Damager. There can be multiple
	 * results.
	 *
	 * @author Jakub Sapalski
	 */
	public enum DamageResult {

		/**
		 * When the player is gliding and Damager has "wings off" option
		 */
		WINGS_OFF,

		/**
		 * When the player is gliding and his wings can receive damage.
		 */
		WINGS_DAMAGE,

		/**
		 * When the player is on ground and was not instantly killed.
		 */
		REGULAR_DAMAGE,

		/**
		 * When a hit is accepted at all.
		 */
		HIT,

	}

	/**
	 * Adds a trigger to the list. Triggers are events that happen on
	 * this particular tick, like left or right clicks.
	 *
	 * @param name
	 *            name of the trigger
	 */
	public void addTrigger(String name);
	
	/**
	 * @return the list of triggers which happened during this tick
	 */
	public List<String> getTriggers();

	/**
	 * @return the UsableItem which is currently held by the player
	 */
	public UsableItem getHeldItem();

	/**
	 * @return whenever the player is holding this item or not
	 */
	public boolean isHolding(UsableItem item);

	/**
	 * @return whenever the player is currently using his engine
	 */
	public boolean isAccelerating();

	/**
	 * Takes wings off the player.
	 */
	public void takeWingsOff();

	/**
	 * Consumes the UsableItem, removing it both from the kit and from the player's inventory.
	 * 
	 * @param item
	 */
	public void consumeItem(UsableItem item);

	/**
	 * @return the Player object of this player
	 */
	public Player getPlayer();

	/**
	 * @return total weight of all items carried by the player
	 */
	public double getWeight();
	
	/**
	 * @return the language chosen by this player
	 */
	public String getLanguage();

	/**
	 * @return whenever the player is currently playing
	 */
	public boolean isPlaying();

	/**
	 * @param isPlaying
	 *            whenever the player is currently playing
	 */
	public void setPlaying(boolean isPlaying);

	/**
	 * @return the Kit object of this player
	 */
	public Kit getKit();

	/**
	 * Updates the player with his kit items.
	 */
	public void updateKit();

	/**
	 * @return the amount of money the player has
	 */
	public int getMoney();

	/**
	 * @param amount
	 *            the amount of money to set for the player
	 */
	public void setMoney(int amount);

	/**
	 * @return the color of this player
	 */
	public ChatColor getColor();

	/**
	 * Sets the color of this player to specified one. It will be used as a team
	 * color to control glow and name color.
	 * 
	 * @param color
	 *            the color to set
	 */
	public void setColor(ChatColor color);

	/**
	 * Update Scoreboard teams with these players' colors.
	 * 
	 * @param map
	 *            map containing player names with their colors
	 */
	public void updateColors(Map<String, ChatColor> map);

	/**
	 * @return a mutable list of SidebarLines this player has.
	 */
	public List<SidebarLine> getLines();

	/**
	 * Makes the player leave the game back to the lobby.
	 */
	public void exitGame();

}
