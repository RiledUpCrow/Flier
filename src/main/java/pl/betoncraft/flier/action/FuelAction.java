/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.action;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.content.Engine;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.UsableItem;

/**
 * Adds fuel to the player's engine.
 *
 * @author Jakub Sapalski
 */
public class FuelAction extends DefaultAction {

	private static final String AMOUNT = "amount";

	private double amount;

	public FuelAction(ConfigurationSection section) throws LoadingException {
		super(section);
		amount = loader.loadDouble(AMOUNT);
	}

	@Override
	public boolean act(InGamePlayer player, UsableItem item) {
		Engine engine = player.getKit().getEngine();
		double amount = modMan.modifyNumber(AMOUNT, this.amount);
		if (amount >= 0) {
			return engine.addFuel(amount);
		} else {
			return engine.removeFuel(amount);
		}
	}

}
