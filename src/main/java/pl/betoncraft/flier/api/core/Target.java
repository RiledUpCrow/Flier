/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api.core;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import pl.betoncraft.flier.api.content.Game;

/**
 * Represents a target which can be hit by Damager.
 *
 * @author Jakub Sapalski
 */
public interface Target {

	/**
	 * @return the player who last attacked this target
	 */
	public Attacker getAttacker();
	
	/**
	 * @param attacker the Attacker which last attacked this target
	 */
	public void setAttacker(Attacker attacker);

	/**
	 * Handles this Target being hit by an Attacker.
	 * 
	 * @param attacker
	 *            the Attacker used in the attack
	 * @return whenever the hit was successful
	 */
	public boolean handleHit(Attacker attacker);

	/**
	 * @return the Target's location
	 */
	public Location getLocation();
	
	/**
	 * @return the current velocity of the Target
	 */
	public Vector getVelocity();

	/**
	 * @return the the Game this player is in
	 */
	public Game getGame();

	/**
	 * Sets the amount of ticks when there can be no damage to the target
	 * 
	 * @param noDamageTicks
	 *            amount of ticks
	 */
	public void setNoDamageTicks(int noDamageTicks);

	/**
	 * Gets the amount of ticks when there can be no damage to the target
	 * 
	 * @return amount of ticks
	 */
	public int getNoDamageTicks();

	/**
	 * @return whenever this Target is available for hitting
	 */
	public boolean isTargetable();

	/**
	 * @return the name of this Target
	 */
	public String getName();

}
