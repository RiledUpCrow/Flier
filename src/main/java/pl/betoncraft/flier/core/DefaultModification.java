/**
 * Copyright (c) 2017 Jakub Sapalski
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
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
