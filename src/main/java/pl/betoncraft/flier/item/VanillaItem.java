/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.item;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.exception.LoadingException;

/**
 * A simple item without any additional mechanics.
 *
 * @author Jakub Sapalski
 */
public class VanillaItem extends DefaultItem {

	public VanillaItem(ConfigurationSection section) throws LoadingException {
		super(section);
	}

}
