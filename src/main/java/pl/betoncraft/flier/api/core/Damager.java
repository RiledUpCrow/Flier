/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api.core;

import java.util.List;

/**
 * Represents a container for Actions applicable on a player.
 *
 * @author Jakub Sapalski
 */
public interface Damager {

	/**
	 * @return The list containing all further usages this Damager invokes. It's
	 *         not guaranteed that returned list will be mutable.
	 */
	public List<Usage> getSubUsages();

	/**
	 * @return the amount of ticks when the player is immune to damage after the
	 *         hit
	 */
	public int getNoDamageTicks();
	
	/**
	 * @return whenever this Damager can cause friendly fire
	 */
	public boolean causesFriendlyFire();
	
	/**
	 * @return whenever this Damager can damage the player who created it
	 */
	public boolean isSuicidal();

}
