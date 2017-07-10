/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
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
