/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.event;

import org.bukkit.event.Cancellable;

import pl.betoncraft.flier.api.content.Bonus;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.core.MatchingPlayerEvent;

/**
 * Fires when a player collects a Bonus.
 *
 * @author Jakub Sapalski
 */
public class FlierCollectBonusEvent extends MatchingPlayerEvent implements Cancellable {
	
	private static final String BONUS = "bonus";
	
	private Bonus bonus;
	private boolean cancel = false;

	public FlierCollectBonusEvent(InGamePlayer player, Bonus bonus) {
		super(player);
		this.bonus = bonus;
		parsePlayer(player, "");
		setString(BONUS, bonus.getID());
	}
	
	public Bonus getBonus() {
		return bonus;
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
