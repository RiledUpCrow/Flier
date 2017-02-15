/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.action;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.Engine;
import pl.betoncraft.flier.api.InGamePlayer;
import pl.betoncraft.flier.api.LoadingException;
import pl.betoncraft.flier.core.defaults.DefaultAction;

/**
 * Adds fuel to the player's engine.
 *
 * @author Jakub Sapalski
 */
public class FuelAction extends DefaultAction {
	
	private double amount;

	public FuelAction(ConfigurationSection section) throws LoadingException {
		super(section);
		amount = loader.loadDouble("amount");
	}

	@Override
	public boolean act(InGamePlayer player) {
		Engine engine = player.getClazz().getCurrentEngine();
		if (amount >= 0) {
			return engine.addFuel(amount);
		} else {
			return engine.removeFuel(amount);
		}
	}

}
