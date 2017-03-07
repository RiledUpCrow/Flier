/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core;

import pl.betoncraft.flier.api.core.InGamePlayer;

/**
 * Represents a MatchingEvent with a player involved.
 *
 * @author Jakub Sapalski
 */
public class MatchingPlayerEvent extends MatchingEvent {

	protected final InGamePlayer player;

	/**
	 * Creates new MatchingEvent with the specified player. The player can't
	 * be null. For events without any particular player use the other constructor.
	 * 
	 * @param player
	 *            the player involved in this event
	 */
	public MatchingPlayerEvent(InGamePlayer player) {
		super(player.getLobby().getGame());
		this.player = player;
		strings.put("class", player.getClazz().getName());
		strings.put("color", player.getColor().name());
	}

	/**
	 * @return the player involved in this event or null if there was no
	 *         particular player
	 */
	public InGamePlayer getPlayer() {
		return player;
	}

}
