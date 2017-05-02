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
 * Activates once in "interval" times.
 *
 * @author Jakub Sapalski
 */
public class IntervalActivator extends DefaultActivator {
	
	private int counter = 0;
	private final int interval;

	public IntervalActivator(ConfigurationSection section) throws LoadingException {
		super(section);
		interval = loader.loadPositiveInt("interval");
		counter += loader.loadNonNegativeInt("offset", 0);
	}

	@Override
	public boolean isActive(InGamePlayer player, UsableItem item) {
		return counter++ % interval == 0;
	}

}
