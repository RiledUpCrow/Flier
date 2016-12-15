/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api;

/**
 * A piece of code which can be repeatedly run for an in-game player.
 *
 * @author Jakub Sapalski
 */
public interface Effect {
	
	/**
	 * Applies effect logics to the player every tick.
	 * 
	 * @param player the player to whom the effect should be apllied
	 */
	public void apply(InGamePlayer player);
	
	/**
	 * @return whenever this effect should be run in fast or slow tick
	 */
	public boolean fast();

}
