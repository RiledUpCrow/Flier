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
package pl.betoncraft.flier.action;

import java.util.Optional;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.content.Action;
import pl.betoncraft.flier.api.content.Attack;
import pl.betoncraft.flier.api.core.Modification;
import pl.betoncraft.flier.api.core.Modification.ModificationTarget;
import pl.betoncraft.flier.api.core.Owner;
import pl.betoncraft.flier.api.core.Usage;
import pl.betoncraft.flier.util.LangManager;
import pl.betoncraft.flier.util.ModificationManager;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * Default implementation of Action (only replication, actually).
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultAction implements Action {
	
	protected final String id;
	protected final String name;
	protected final ValueLoader loader;
	protected final ModificationManager modMan;
	protected final Optional<Owner> owner;
	
	public DefaultAction(ConfigurationSection section, Optional<Owner> owner) {
		id = section.getName();
		loader = new ValueLoader(section);
		modMan = new ModificationManager();
		name = loader.loadString("name", id);
		this.owner = owner;
	}
	
	@Override
	public String getID() {
		return id;
	}
	
	@Override
	public String getName(CommandSender player) {
		return name.startsWith("$") ? LangManager.getMessage(player, name.substring(1)) : name;
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
	public Optional<Owner> getOwner() {
		return owner;
	}

}
