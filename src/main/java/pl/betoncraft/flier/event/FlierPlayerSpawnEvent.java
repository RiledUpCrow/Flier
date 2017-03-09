/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.event;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.core.MatchingPlayerEvent;

/**
 * Fires when a player spawns in the game.
 *
 * @author Jakub Sapalski
 */
public class FlierPlayerSpawnEvent extends MatchingPlayerEvent {

	public FlierPlayerSpawnEvent(InGamePlayer player) {
		super(player);
	}

}
