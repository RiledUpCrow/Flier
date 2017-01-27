/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.activator;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.InGamePlayer;
import pl.betoncraft.flier.api.UsableItem;
import pl.betoncraft.flier.core.defaults.DefaultActivator;

/**
 * Activates when the player has right-clicked.
 *
 * @author Jakub Sapalski
 */
public class RightClickActivator extends DefaultActivator {

	public RightClickActivator(ConfigurationSection section) {
		super(section);
	}

	@Override
	public boolean isActive(InGamePlayer player, UsableItem item) {
		return player.didRightClick() && player.isHolding(item);
	}

}
