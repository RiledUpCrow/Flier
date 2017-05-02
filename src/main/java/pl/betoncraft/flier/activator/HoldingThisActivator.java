/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.activator;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.UsableItem;

/**
 * Activates when the player is holding the item on which this activator is
 * defined.
 *
 * @author Jakub Sapalski
 */
public class HoldingThisActivator extends DefaultActivator {

	public HoldingThisActivator(ConfigurationSection section) {
		super(section);
	}

	@Override
	public boolean isActive(InGamePlayer player, UsableItem item) {
		return item != null && player.isHolding(item);
	}

}
