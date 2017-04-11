/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.action;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;

/**
 * Modifies player's health.
 *
 * @author Jakub Sapalski
 */
public class HealthAction extends DefaultAction {

	private static final String AMOUNT = "amount";

	private double amount;

	public HealthAction(ConfigurationSection section) throws LoadingException {
		super(section);
		amount = loader.loadDouble(AMOUNT);
	}

	@Override
	public boolean act(InGamePlayer player) {
		if (amount < 0) {
			player.getPlayer().damage(amount);
			return true;
		}
		if (amount > 0) {
			double max = player.getPlayer().getMaxHealth();
			double newHealth = player.getPlayer().getHealth() + amount;
			if (newHealth > max) {
				newHealth = max;
			}
			player.getPlayer().setHealth(newHealth);
			return true;
		}
		return false;
	}

}
