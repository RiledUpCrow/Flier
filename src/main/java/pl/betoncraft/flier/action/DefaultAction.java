/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.action;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.content.Action;
import pl.betoncraft.flier.api.content.Attack;
import pl.betoncraft.flier.api.core.Modification;
import pl.betoncraft.flier.api.core.Modification.ModificationTarget;
import pl.betoncraft.flier.api.core.Usage;
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
	protected final boolean needsItem;
	protected final boolean needsSource;
	
	public DefaultAction(ConfigurationSection section, boolean needsItem, boolean needsSource) {
		id = section.getName();
		loader = new ValueLoader(section);
		modMan = new ModificationManager();
		this.needsItem = needsItem;
		this.needsSource = needsSource;
	}
	
	@Override
	public String getID() {
		return id;
	}
	
	@Override
	public void addModification(Modification mod) {
		if (mod.getTarget() == ModificationTarget.ACTION && mod.getNames().contains(id)) {
			modMan.addModification(mod);
		}
		if (this instanceof Attack) {
			Attack attack = (Attack) this;
			for (Usage usage : attack.getSubUsages()) {
				usage.getActions().forEach(action -> action.addModification(mod));
				usage.getActivators().forEach(activator -> activator.addModification(mod));
			}
		}
	}
	
	@Override
	public void removeModification(Modification mod) {
		if (mod.getTarget() == ModificationTarget.ACTION && mod.getNames().contains(id)) {
			modMan.removeModification(mod);
		}
		if (this instanceof Attack) {
			Attack attack = (Attack) this;
			for (Usage usage : attack.getSubUsages()) {
				usage.getActions().forEach(action -> action.removeModification(mod));
				usage.getActivators().forEach(activator -> activator.removeModification(mod));
			}
		}
	}

	@Override
	public boolean needsItem() {
		return needsItem;
	}

	@Override
	public boolean needsSource() {
		return needsSource;
	}

}
