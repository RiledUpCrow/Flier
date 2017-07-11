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

import org.bukkit.event.Cancellable;

import pl.betoncraft.flier.api.core.Attacker;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.core.MatchingTwoPlayersEvent;

/**
 * Called when the player hits someone with a Damager.
 *
 * @author Jakub Sapalski
 */
public class FlierPlayerHitEvent extends MatchingTwoPlayersEvent implements Cancellable {

	private Attacker attacker;
	private boolean cancel = false;

	/**
	 * Creates new MatchingTwoPlayersEvent with details about hitting the player.
	 * 
	 * @param player
	 */
	public FlierPlayerHitEvent(InGamePlayer target, Attacker attacker) {
		super(target, attacker.getCreator(), "shooter_", "target_");
		this.attacker = attacker;
		setBool("self_hit", attacker.getCreator().equals(target));
	}

	/**
	 * @return the damager which hit the other player
	 */
	public Attacker getAttacker() {
		return attacker;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

}
