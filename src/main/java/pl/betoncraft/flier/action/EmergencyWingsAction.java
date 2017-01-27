/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.action;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.InGamePlayer;
import pl.betoncraft.flier.api.Wings;
import pl.betoncraft.flier.core.defaults.DefaultAction;
import pl.betoncraft.flier.exception.LoadingException;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * An item which can raise wings' health above 0, thus recreating them.
 *
 * @author Jakub Sapalski
 */
public class EmergencyWingsAction extends DefaultAction {
	
	private double amount;

	public EmergencyWingsAction(ConfigurationSection section) throws LoadingException {
		amount = ValueLoader.loadNonNegativeDouble(section, "amount");
	}

	@Override
	public boolean act(InGamePlayer player) {
		Wings wings = player.getClazz().getCurrentWings();
		if (wings.getHealth() == 0) {
			wings.addHealth(amount);
			return true;
		} else {
			// can't be used if wings have health (are not destroyed)
			return false;
		}
	}

}
