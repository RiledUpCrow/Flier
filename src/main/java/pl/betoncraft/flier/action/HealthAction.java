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

	private double amount;

	public HealthAction(ConfigurationSection section) throws LoadingException {
		super(section, false, false);
		amount = loader.loadDouble(AMOUNT);
	}

	@Override
	public boolean act(Optional<InGamePlayer> source, InGamePlayer target, Optional<UsableItem> item) {
		if (amount < 0) {
			target.getPlayer().damage(amount);
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
