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

import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.api.core.Damager.DamageResult;

/**
 * Represents a player who is in a Game.
 *
 * @author Jakub Sapalski
 */
public interface InGamePlayer {

	/**
	 * Stuff which needs to happen to the player to let him fly. Speeding up,
	 * taking fuel, regenerating stuff.
	 */
	public void fastTick();

	/**
	 * Stuff which helps the player fly, but is not critical to the game, like
	 * updating displayed statistics.
	 */
	public void slowTick();

	/**
	 * Called to indicate the player left clicked.
	 */
	public void leftClick();

	/**
	 * @return whenever the player left clicked;
	 */
	public boolean didLeftClick();

	/**
	 * Called to indicate the player right clicked.
	 */
	public void rightClick();

	/**
	 * @return whenever the player right clicked;
	 */
	public boolean didRightClick();

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
	 * Consumes the UsableItem, removing it both from the class and from the player's inventory.
	 * 
	 * @param item
	 */
	public void consumeItem(UsableItem item);

	/**
	 * @return the the Game this player is in
	 */
	public Game getGame();

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
	 * @return the PlayerClass object of this player
	 */
	public PlayerClass getClazz();

	/**
	 * Updates the player with his class items.
	 */
	public void updateClass();

	/**
	 * @return the player who last attacked this player
	 */
	public InGamePlayer getAttacker();

	/**
	 * @param attacker
	 *            the player who last attacked this player
	 */
	public void setAttacker(InGamePlayer attacker);

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

	/**
	 * Deals Damager's damage from the shooter to the player.
	 * 
	 * @param shooter
	 *            the player who shot the Damager; can be null or equal to
	 *            player
	 * @param damager
	 *            the Damager used for the attack; cannot be null
	 * @return list of damage results, never null; if it's empty the damage was
	 *         not dealt
	 */
	public List<DamageResult> damage(InGamePlayer shooter, Damager damager);

	/**
	 * Sets the amount of ticks when there can be no damage to the player
	 * 
	 * @param noDamageTicks amount of ticks
	 */
	public void setNoDamageTicks(int noDamageTicks);

}
