/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.action;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.content.Action;
import pl.betoncraft.flier.api.core.Modification;
import pl.betoncraft.flier.util.ModificationManager;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * Default implementation of Action (only replication, actually).
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultAction implements Action {
	
	protected final String id;
	protected final ValueLoader loader;
	protected final ModificationManager modMan;
	
	public DefaultAction(ConfigurationSection section) {
		id = section.getName();
		loader = new ValueLoader(section);
		modMan = new ModificationManager();
	}
	
	@Override
	public String getID() {
		return id;
	}
	
	@Override
	public void addModification(Modification mod) {
		modMan.addModification(mod);
	}
	
	@Override
	public void removeModification(Modification mod) {
		modMan.removeModification(mod);
	}

}
