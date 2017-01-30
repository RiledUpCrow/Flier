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

/**
 * Changes wings health.
 *
 * @author Jakub Sapalski
 */
public class WingsHealthAction extends DefaultAction {
	
	private double amount;

	public WingsHealthAction(ConfigurationSection section) throws LoadingException {
		super(section);
		amount = loader.loadDouble("amount");
	}

	@Override
	public boolean act(InGamePlayer player) {
		Wings wings = player.getClazz().getCurrentWings();
		if (amount >= 0) {
			return wings.addHealth(amount);
		} else {
			return wings.removeHealth(-amount);
		}
	}

}
