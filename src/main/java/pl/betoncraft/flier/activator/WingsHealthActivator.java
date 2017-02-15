/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.activator;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.InGamePlayer;
import pl.betoncraft.flier.api.LoadingException;
import pl.betoncraft.flier.api.UsableItem;
import pl.betoncraft.flier.api.Wings;
import pl.betoncraft.flier.core.defaults.DefaultActivator;

/**
 * Activates if wings health is in range.
 *
 * @author Jakub Sapalski
 */
public class WingsHealthActivator extends DefaultActivator {
	
	private double min;
	private double max;
	private Type type;
	
	private enum Type {
		ABSOLUTE, PERCENTAGE
	}
	
	public WingsHealthActivator(ConfigurationSection section) throws LoadingException {
		super(section);
		min = loader.loadNonNegativeDouble("min");
		max = loader.loadNonNegativeDouble("max", min);
		type = loader.loadEnum("number_type", Type.ABSOLUTE, Type.class);
	}

	@Override
	public boolean isActive(InGamePlayer player, UsableItem item) {
		Wings wings = player.getClazz().getCurrentWings();
		double health = wings.getHealth();
		double maxHealth = wings.getMaxHealth();
		switch (type) {
		case ABSOLUTE:
			return health >= min && health <= max;
		case PERCENTAGE:
			double percentage = health / maxHealth * 100;
			return percentage >= min && percentage <= max;
		}
		return false;
	}

}
