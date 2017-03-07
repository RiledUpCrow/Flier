/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api.content;

import java.util.List;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.Matcher;
import pl.betoncraft.flier.util.EffectListener.EventType;

/**
 * Effect represents some non-game-changing action which is called when a
 * specified Bukkit event fires.
 *
 * @author Jakub Sapalski
 */
public interface Effect {
	
	/**
	 * @return the ID of this Effect
	 */
	public String getID();

	/**
	 * @return the type of MatchingEvent this Effect applies to
	 */
	public EventType getType();

	/**
	 * Fire an effect with optional player.
	 * 
	 * @param player
	 *            the player for whom the effect will fire or null if it should
	 *            just fire for no particular player
	 */
	public void fire(InGamePlayer player);
	
	/**
	 * @return the list of Matchers to match against a MatchingEvent
	 */
	public List<Matcher> getMatchers();

}
