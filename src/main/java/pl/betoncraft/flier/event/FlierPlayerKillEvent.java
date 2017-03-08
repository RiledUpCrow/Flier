/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.event;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.core.MatchingTwoPlayersEvent;

/**
 * Fired when the player is killed by another player in Game.
 *
 * @author Jakub Sapalski
 */
public class FlierPlayerKillEvent extends MatchingTwoPlayersEvent {
	
	public enum Type {
		SHOT_DOWN, KILLED
	}

	public FlierPlayerKillEvent(InGamePlayer killed, InGamePlayer killer, Type type) {
		super(killed, killer, "killer_", "killed_");
		setBool("suicide", killed.equals(killer));
		setBool("shot_down", type == Type.SHOT_DOWN);
		setBool("killed", type == Type.KILLED);
	}

}
