/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api.content;

import java.util.List;

import org.bukkit.Location;

import pl.betoncraft.flier.api.core.InGamePlayer;

/**
 * Represents a collectable Entity on the Game map.
 *
 * @author Jakub Sapalski
 */
public interface Bonus {

	/**
	 * @return the ID of this Bonus
	 */
	public String getID();
	
	/**
	 * @return the name of this Bonus' Location
	 */
	public String getLocationName();

	/**
	 * @return the Location of a Bonus
	 */
	public Location getLocation();
	
	/**
	 * @param location the Location of a Bonus
	 */
	public void setLocation(Location location);

	/**
	 * @return the minimum distance to activate this Bonus
	 */
	public double getDistance();

	/**
	 * @return true if the Bonus should be removed after using
	 */
	public boolean isConsumable();

	/**
	 * @return true if the bonus is available for use
	 */
	public boolean isAvailable();

	/**
	 * @return the cooldown time
	 */
	public int getCooldown();

	/**
	 * @return in how many ticks should this Bonus be respawned; it will return
	 *         -1 if it shouldn't respawn at all
	 */
	public int getRespawn();

	/**
	 * Called in game fast tick to allow Bonus updating (for example rotating
	 * the entity).
	 */
	public void update();

	/**
	 * Starts the bonus, for example spawning the entity.
	 */
	public void start();

	/**
	 * Cleans up the bonus, for example removing the entity.
	 */
	public void stop();

	/**
	 * @return the list of all actions in this Bonus
	 */
	public List<Action> getActions();

	/**
	 * @param player
	 *            applies the Bonus to the player
	 */
	public void apply(InGamePlayer player);

}
