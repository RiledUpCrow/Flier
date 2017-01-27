/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api;

/**
 * Represents a one time action, as opposed to the repeating Effect.
 *
 * @author Jakub Sapalski
 */
public interface Action extends Replicable {
	
	/**
	 * Performs the action on specified player.
	 * 
	 * @param player the player on which the action needs to be performed
	 * @return whenever the action was performed or not
	 */
	public boolean act(InGamePlayer player);

}
