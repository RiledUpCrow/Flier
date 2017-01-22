/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api;

import org.bukkit.Location;

/**
 * Represents a collectable Entity on the Game map.
 *
 * @author Jakub Sapalski
 */
public interface Bonus extends Replicable {

	/**
	 * @return the location of a Bonus
	 */
	public Location getLocation();
	
	/**
	 * @return the minimum distance to activate this Bonus
	 */
	public double distance();
	
	/**
	 * @return true if the Bonus should be removed after using
	 */
	public boolean consumable();
	
	/**
	 * @return true if the bonus is available for use
	 */
	public boolean isAvailable();
	
	/**
	 * @return the cooldown time
	 */
	public int cooldown();
	
	/**
	 * @return in how many ticks should this Bonus be respawned;
	 *         it will return -1 if it shouldn't respawn at all
	 */
	public int respawn();
	
	/**
	 * Called in game fast tick to allow Bonus updating (for example rotating
	 * the entity).
	 */
	public void update();
	
	/**
	 * @param player applies the Bonus to the player
	 */
	public void apply(InGamePlayer player);

	/**
	 * Cleans up the bonus, for example removing the entity.
	 */
	public void cleanUp();

}
