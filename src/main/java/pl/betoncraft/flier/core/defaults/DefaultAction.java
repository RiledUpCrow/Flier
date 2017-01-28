/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core.defaults;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.Flier;
import pl.betoncraft.flier.api.Action;
import pl.betoncraft.flier.exception.LoadingException;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * Default implementation of Action (only replication, actually).
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultAction implements Action {
	
	protected final String id;
	protected final ValueLoader loader;
	
	public DefaultAction(ConfigurationSection section) {
		id = section.getName();
		loader = new ValueLoader(section);
	}

	@Override
	public Action replicate() {
		try {
			return Flier.getInstance().getAction(id);
		} catch (LoadingException e) {
			return null; // dead code
		}
	}

}
