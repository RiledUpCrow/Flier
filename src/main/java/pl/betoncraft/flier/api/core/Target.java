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
	 * @param attacker
	 *            the Attacker which last attacked this target
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
