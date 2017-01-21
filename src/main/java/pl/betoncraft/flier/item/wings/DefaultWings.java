/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.item.wings;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.Wings;
import pl.betoncraft.flier.core.ValueLoader;
import pl.betoncraft.flier.exception.LoadingException;
import pl.betoncraft.flier.item.DefaultItem;

/**
 * Default Wings implementation.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultWings extends DefaultItem implements Wings {
	
	protected final double health;
	protected final double regeneration;

	public DefaultWings(ConfigurationSection section) throws LoadingException {
		super(section);
		super.item.setType(Material.ELYTRA);
		health = ValueLoader.loadPositiveDouble(section, "health");
		regeneration = ValueLoader.loadNonNegativeDouble(section, "regeneration");
	}
	
	@Override
	public double getHealth() {
		return health;
	}

	@Override
	public double getRegeneration() {
		return regeneration;
	}

}
