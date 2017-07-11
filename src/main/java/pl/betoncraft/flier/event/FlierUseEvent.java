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
		setString("item", item.getID());
		setNumber("ammo", (double) item.getAmmo());
		setNumber("amount", (double) item.getAmount());
		setString("usage", usage.getID());
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
