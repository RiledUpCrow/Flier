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
package pl.betoncraft.flier.event;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.core.MatchingTwoPlayersEvent;

/**
 * Fired when the player is killed by another player in Game.
 *
 * @author Jakub Sapalski
 */
public class FlierPlayerKillEvent extends MatchingTwoPlayersEvent {

	public enum KillType {
		SHOT_DOWN,
		KILLED;
	}
	
	private KillType type;

	public FlierPlayerKillEvent(InGamePlayer killed, InGamePlayer killer, KillType type) {
		super(killed, killer, "killer_", "killed_");
		this.type = type;
		setBool("suicide", killed.equals(killer));
		setBool("shot_down", type == KillType.SHOT_DOWN);
		setBool("killed", type == KillType.KILLED);
	}
	
	public KillType getType() {
		return type;
	}

}
