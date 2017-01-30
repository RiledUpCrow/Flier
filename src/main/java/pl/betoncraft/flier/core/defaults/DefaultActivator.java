/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core.defaults;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.Activator;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * Default implementation of an Activator (only replication, actually).
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultActivator implements Activator {
	
	protected final String id;
	protected final ValueLoader loader;
	
	public DefaultActivator(ConfigurationSection section) {
		id = section.getName();
		loader = new ValueLoader(section);
	}

}
