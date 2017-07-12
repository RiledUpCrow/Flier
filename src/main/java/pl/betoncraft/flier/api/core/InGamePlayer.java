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
	 * Adds a trigger to the list. Triggers are events that happen on this
	 * particular tick, for example left or right clicks.
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
	 * Takes the wings off from the player.
	 */
	public void takeWingsOff();

	/**
	 * Consumes the UsableItem, removing it both from the kit and from the
	 * player's inventory.
	 * 
	 * @param item
	 *            UsableItem to consume
	 */
	public void consumeItem(UsableItem item);

	/**
	 * @return the Bukkit Player object of this player
	 */
	public Player getPlayer();

	/**
	 * @return total weight of all items carried by the player
	 */
	public double getWeight();

	/**
	 * @return the language chosen by this player (won't change in the middle of
	 *         the Game, even if the player changes his language)
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
	 * Clears all stuff in the player. Called by the Game when removing this player.
	 */
	public void clearPlayer();

}
