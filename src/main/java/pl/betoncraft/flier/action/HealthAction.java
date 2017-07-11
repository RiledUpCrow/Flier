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
package pl.betoncraft.flier.action;

import java.util.Optional;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.UsableItem;

/**
 * Modifies player's health.
 *
 * @author Jakub Sapalski
 */
public class HealthAction extends DefaultAction {

	private static final String AMOUNT = "amount";
	private static final String DISTANCE_SCALE = "distance_scale";
	private static final String MIN_AMOUNT = "min_amount";

	private double amount;
	private double distanceScale;
	private double minAmount;

	public HealthAction(ConfigurationSection section) throws LoadingException {
		super(section, false, false);
		amount = loader.loadDouble(AMOUNT);
		distanceScale = loader.loadNonNegativeDouble(DISTANCE_SCALE, 0d);
		minAmount = loader.loadNonNegativeDouble(MIN_AMOUNT, 0d);
	}

	@Override
	public boolean act(Optional<InGamePlayer> creator, Optional<InGamePlayer> source,
			InGamePlayer target, Optional<UsableItem> item) {
		double amount = modMan.modifyNumber(AMOUNT, this.amount);
		double distanceScale = modMan.modifyNumber(DISTANCE_SCALE, this.distanceScale);
		double minAmount = modMan.modifyNumber(MIN_AMOUNT, this.minAmount);
		// minimum amount cannot exceed amount
		// absolute values are taken since distance will decrease the damage
		if (Math.abs(minAmount) > Math.abs(amount)) {
			minAmount = amount;
		}
		// both amount and minimum amount must have the same sign
		if (Math.abs(amount - minAmount) > Math.abs(amount)) {
			minAmount = 0;
		}
		// calculate optional distance scaling
		if (distanceScale > 0 && source.isPresent()) {
			double currentDist = source.get().getLocation().distance(target.getLocation());
			amount -= (currentDist * (amount - minAmount) / distanceScale);
		}
		// use different methods, based on whenever we're damaging or healing the player
		if (amount < 0) {
			target.getPlayer().damage(-amount);
			return true;
		}
		if (amount > 0) {
			double max = target.getPlayer().getMaxHealth();
			double newHealth = target.getPlayer().getHealth() + amount;
			if (newHealth > max) {
				newHealth = max;
			}
			target.getPlayer().setHealth(newHealth);
			return true;
		}
		return false;
	}

}
