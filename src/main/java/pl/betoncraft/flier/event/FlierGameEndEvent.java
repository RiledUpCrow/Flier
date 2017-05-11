/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.event;

import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.core.MatchingEvent;

/**
 * Called when the Game ends.
 *
 * @author Jakub Sapalski
 */
public class FlierGameEndEvent extends MatchingEvent {
	
	public enum GameEndCause {
		FINISHED(0),
		ABANDONED(1),
		ABORTED(2);
		private int type;
		private GameEndCause(int type) {
			this.type = type;
		}
		/**
		 * @return the magic number used in database
		 */
		public int get() {
			return type;
		}
	}
	
	private static final String CAUSE = "cause";
	private GameEndCause cause;

	public FlierGameEndEvent(Game game, GameEndCause cause) {
		super(game);
		this.cause = cause;
		setString(CAUSE, cause.toString().toLowerCase());
	}
	
	public GameEndCause getCause() {
		return cause;
	}

}
