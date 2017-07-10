/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
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
	public boolean act(Optional<InGamePlayer> source, InGamePlayer target, Optional<UsableItem> item) {
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
