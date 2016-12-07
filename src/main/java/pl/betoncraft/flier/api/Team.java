/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api;

import org.bukkit.ChatColor;
import org.bukkit.Location;

/**
 * Represents a team of players in a game.
 *
 * @author Jakub Sapalski
 */
public interface Team {
	
	/**
	 * @return index on the scoreboard
	 */
	public int getIndex();

	/**
	 * @return the score
	 */
	public int getScore();
	
	/**
	 * @param score number of points to which the score needs to be set
	 */
	public void setScore(int score);

	/**
	 * @return the name of this team
	 */
	public String getName();

	/**
	 * @return the spawn location for this team
	 */
	public Location getSpawn();

	/**
	 * @return the color of this team
	 */
	public ChatColor getColor();
}
