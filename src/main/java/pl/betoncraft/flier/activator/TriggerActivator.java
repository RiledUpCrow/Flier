/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.activator;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.UsableItem;

/**
 * Activates when the player has specified trigger on this tick.
 *
 * @author Jakub Sapalski
 */
public class TriggerActivator extends DefaultActivator {
	
	private String trigger;

	public TriggerActivator(ConfigurationSection section) throws LoadingException {
		super(section);
		trigger = loader.loadString("trigger");
	}

	@Override
	public boolean isActive(InGamePlayer player, UsableItem item) {
		return player.getTriggers().contains(trigger);
	}

}
