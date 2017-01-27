/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.Flier;
import pl.betoncraft.flier.api.Action;
import pl.betoncraft.flier.api.Activator;
import pl.betoncraft.flier.api.Usage;
import pl.betoncraft.flier.exception.LoadingException;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * Default implementation of Usage.
 *
 * @author Jakub Sapalski
 */
public class DefaultUsage implements Usage {
	
	protected int cooldown;
	protected List<Activator> activators = new ArrayList<>();
	protected List<Action> actions = new ArrayList<>();
	
	public DefaultUsage(ConfigurationSection section) throws LoadingException {
		cooldown = ValueLoader.loadNonNegativeInt(section, "cooldown");
		for (String activator : section.getStringList("activators")) {
			activators.add(Flier.getInstance().getActivator(activator));
		}
		for (String action : section.getStringList("actions")) {
			actions.add(Flier.getInstance().getAction(action));
		}
	}

	@Override
	public List<Activator> getActivators() {
		return activators;
	}

	@Override
	public List<Action> getActions() {
		return actions;
	}

	@Override
	public int getCooldown() {
		return cooldown;
	}

}
