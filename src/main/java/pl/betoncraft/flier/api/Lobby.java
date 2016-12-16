/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api;

import org.bukkit.Location;

/**
 * Represents a lobby.
 *
 * @author Jakub Sapalski
 */
public interface Lobby {
	
	/**
	 * @return the location of a lobby spawn.
	 */
	public Location getSpawn();
	
	/**
	 * Called when the game ends.
	 */
	public void stop();

	/**
	 * Sets the game for this lobby.
	 * 
	 * @param game
	 */
	void setGame(Game game);

	/**
	 * @return the game this lobby is meant for.
	 */
	Game getGame();

	/**
	 * @return the copy of default class
	 */
	public PlayerClass getDefaultClass();

}
