/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.event;

import org.bukkit.event.Cancellable;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.UsableItem;
import pl.betoncraft.flier.api.core.Usage;
import pl.betoncraft.flier.core.MatchingPlayerEvent;

/**
 * Called when the player uses an Usage of some UsableItem.
 *
 * @author Jakub Sapalski
 */
public class FlierUseEvent extends MatchingPlayerEvent implements Cancellable {

	private UsableItem item;
	private Usage usage;
	private boolean cancel = false;

	/**
	 * Creates new MatchingEvent with details about UsableItem use.
	 * 
	 * @param player
	 *            the player who used the item
	 * @param item
	 *            used item
	 * @param usage
	 *            exact usage
	 */
	public FlierUseEvent(InGamePlayer player, UsableItem item, Usage usage) {
		super(player);
		this.item = item;
		this.usage = usage;
		strings.put("item", item.getID());
		numbers.put("ammo", (double) item.getAmmo());
		numbers.put("amount", (double) item.getAmount());
		strings.put("usage", usage.getID());
	}

	/**
	 * @return the item which was used
	 */
	public UsableItem getItem() {
		return item;
	}

	/**
	 * @return the exact usage which was used
	 */
	public Usage getUsage() {
		return usage;
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
