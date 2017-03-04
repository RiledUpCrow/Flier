/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core.item;

import pl.betoncraft.flier.api.UsableItem;
import pl.betoncraft.flier.api.UsableItemStack;

public class DefaultUsableItemStack implements UsableItemStack {
	
	protected final UsableItem item;
	protected final int def;
	protected final int max;
	protected final int min;
	protected int amount;
	
	public DefaultUsableItemStack(UsableItem item, int amount, int max, int min) {
		this.item = item;
		this.def = amount;
		this.max = max;
		this.min = min;
		this.amount = this.def;
	}
	
	@Override
	public UsableItem getItem() {
		return item;
	}
	
	@Override
	public int getAmount() {
		return amount;
	}
	
	@Override
	public boolean setAmount(int amount) {
		if (amount < 0 || amount > max) {
			return false;
		} else {
			this.amount = amount;
			return true;
		}
	}
	
	@Override
	public int getMax() {
		return max;
	}
	
	@Override
	public int getMin() {
		return min;
	}

	@Override
	public int getDefaultAmount() {
		return def;
	}
	
	@Override
	public boolean isSimilar(UsableItemStack item) {
		return this.item.isSimilar(item.getItem());
	}
	
	@Override
	public UsableItemStack clone() {
		return new DefaultUsableItemStack(item, amount, max, min);
	}
	
}