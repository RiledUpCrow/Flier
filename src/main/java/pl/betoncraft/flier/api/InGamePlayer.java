/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api;

import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import pl.betoncraft.flier.api.Damager.DamageResult;

/**
 * Represents a player who is in a Game.
 *
 * @author Jakub Sapalski
 */
public interface InGamePlayer {
	
	/**
	 * Stuff which needs to happen to the player to let him
	 * fly. Speeding up, taking fuel, regenerating stuff.
	 */
	public void fastTick();
	
	/**
	 * Stuff which helps the player fly, but is not critical
	 * to the game, like updating displayed statistics.
	 */
	public void slowTick();
	
	/**
	 * Called when the Game decides the player used an item.
	 * Validates if the use is correct and uses the item.
	 */
	public void use();
	
	/**
	 * Called when the Game decides the player was damaged by
	 * a Damager object, optionally fired by attacker.
	 * 
	 * @param attacker the player who fired the damager
	 * @param damager the Damager object (for example Weapon)
	 * @return the result of damage
	 */
	public DamageResult damage(InGamePlayer attacker, Damager damager);

	/**
	 * @return the instance of the Game this player plays
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
	 * @return whenever the player is currently playing
	 */
	public boolean isPlaying();
	
	/**
	 * @param isPlaying whenever the player is currently playing
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
	 * @param attacker the player who last attacked this player
	 */
	public void setAttacker(InGamePlayer attacker);
	
	/**
	 * @return the amount of money the player has
	 */
	public int getMoney();
	
	/**
	 * @param amount the amount of money to set for the player
	 */
	public void setMoney(int amount);
	
	/**
	 * @return the amount of fuel this player currently has
	 */
	public double getFuel();
	
	/**
	 * Adds the fuel to this player's reserves. It won't go above
	 * engine's maximum fuel amount.
	 * 
	 * @param amount amount of fuel to add
	 * @return true if anything was added
	 */
	public boolean addFuel(double amount);
	
	/**
	 * Removes specified amount of fuel from this player's reserves.
	 * It won't go below 0.
	 * 
	 * @param amount amount of fuel to remove
	 * @return true if the whole amount was removed, false if there
	 *         was not enough fuel to remove that amount
	 */
	public boolean removeFuel(double amount);
	
	/**
	 * @return the health level of this player's wings
	 */
	public double getHealth();
	
	/**
	 * Adds the health to this player's wings. It won't go above
	 * wings' maximum health level.
	 * 
	 * @param amount amount of health to add
	 * @return true if anything was added
	 */
	public boolean addHealth(double amount);
	
	/**
	 * Removes specified amount of health from this player's wings.
	 * It won't go below 0.
	 * 
	 * @param amount amount of health to remove
	 * @return true if the whole amount was removed, false if there
	 *         was not enough health to remove that amount
	 */
	public boolean removeHealth(double amount);
	
	/**
	 * @return the color of this player
	 */
	public ChatColor getColor();
	
	/**
	 * Sets the color of this player to specified one. It will be used
	 * as a team color to control glow and name color.
	 * 
	 * @param color the color to set
	 */
	public void setColor(ChatColor color);
	
	/**
	 * Update Scoreboard teams with these players' colors.
	 * 
	 * @param map map containing player names with their colors
	 */
	public void updateColors(Map<String, ChatColor> map);
	
	/**
	 * @return a mutable list of SidebarLines this player has.
	 */
	public List<SidebarLine> getLines();
	
	/**
	 * Moves the player out of the game, clears and restores everything
	 * related to him.
	 */
	public void clear();

}
