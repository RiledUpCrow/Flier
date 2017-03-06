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

import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.Modification;
import pl.betoncraft.flier.api.core.Modifier;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * Default implementation of the Modification.
 *
 * @author Jakub Sapalski
 */
public class DefaultModification implements Modification {
	
	protected List<Modifier> modifiers = new ArrayList<>();
	protected ModificationTarget target;
	protected List<String> names;
	
	public DefaultModification(ConfigurationSection section) throws LoadingException {
		ValueLoader loader = new ValueLoader(section);
		target = loader.loadEnum("target", ModificationTarget.class);
		names = section.getStringList("names");
		ConfigurationSection mods = section.getConfigurationSection("modifiers");
		if (mods != null) for (String key : mods.getKeys(false)) {
			modifiers.add(new DefaultModifier(key, mods.get(key).toString()));
		}
	}

	@Override
	public List<Modifier> getModifiers() {
		return modifiers;
	}

	@Override
	public ModificationTarget getTarget() {
		return target;
	}

	@Override
	public List<String> getNames() {
		return names;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof DefaultModification) {
			DefaultModification mod = (DefaultModification) o;
			if (mod.modifiers.size() != modifiers.size() || mod.target != target || mod.names.size() != names.size() ||
					!mod.names.containsAll(names)) {
				return false;
			}
			for (int i = 0; i < modifiers.size(); i++) {
				if (!mod.modifiers.get(i).equals(modifiers.get(i))) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

}
