/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.activator;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.content.Wings;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.UsableItem;

/**
 * Activates if wings health is in range.
 *
 * @author Jakub Sapalski
 */
public class WingsHealthActivator extends DefaultActivator {
	
	private static final String NUMBER_TYPE = "number_type";
	private static final String MAX = "max";
	private static final String MIN = "min";

	private double min;
	private double max;
	private Type type;
	
	private enum Type {
		ABSOLUTE, PERCENTAGE
	}
	
	public WingsHealthActivator(ConfigurationSection section) throws LoadingException {
		super(section);
		min = loader.loadNonNegativeDouble(MIN);
		max = loader.loadNonNegativeDouble(MAX, min);
		type = loader.loadEnum(NUMBER_TYPE, Type.ABSOLUTE, Type.class);
	}

	@Override
	public boolean isActive(InGamePlayer player, UsableItem item) {
		Wings wings = player.getKit().getWings();
		double health = wings.getHealth();
		double maxHealth = wings.getMaxHealth();
		switch (modMan.modifyEnum(NUMBER_TYPE, type)) {
		case ABSOLUTE:
			return health >= modMan.modifyNumber(MIN, min) && health <= modMan.modifyNumber(MAX, max);
		case PERCENTAGE:
			double percentage = health / maxHealth * 100;
			return percentage >= modMan.modifyNumber(MIN, min) && percentage <= modMan.modifyNumber(MAX, max);
		}
		return false;
	}

}
