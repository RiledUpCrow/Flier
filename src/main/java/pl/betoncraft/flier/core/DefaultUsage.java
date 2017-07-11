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

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Action;
import pl.betoncraft.flier.api.content.Activator;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.Usage;
import pl.betoncraft.flier.util.Position;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * Default implementation of Usage.
 *
 * @author Jakub Sapalski
 */
public class DefaultUsage implements Usage {
	
	protected String id;
	protected ValueLoader loader;
	
	protected int cooldown;
	protected int ammoUse;
	protected final Usage.Where where;
	protected List<Activator> activators = new ArrayList<>();
	protected List<Action> actions = new ArrayList<>();
	
	public DefaultUsage(ConfigurationSection section) throws LoadingException {
		id = section.getName();
		loader = new ValueLoader(section);
		cooldown = loader.loadNonNegativeInt("cooldown", 0);
		ammoUse = loader.loadInt("ammo_use", 0);
		where = loader.loadEnum("where", Usage.Where.EVERYWHERE, Usage.Where.class);
		Flier flier = Flier.getInstance();
		for (String activator : section.getStringList("activators")) {
			activators.add(flier.getActivator(activator));
		}
		for (String action : section.getStringList("actions")) {
			actions.add(flier.getAction(action));
		}
	}

	@Override
	public String getID() {
		return id;
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

	@Override
	public int getAmmoUse() {
		return ammoUse;
	}
	
	@Override
	public Usage.Where where() {
		return where;
	}

	@Override
	public boolean canUse(InGamePlayer player) {
		return Position.check(player.getPlayer(), where);
	}

	@Override
	public boolean equals(Object usage) {
		if (usage instanceof DefaultUsage) {
			DefaultUsage def = (DefaultUsage) usage;
			boolean actionsMatch = true;
			boolean activatorsMatch = true;
			if (def.actions.size() == actions.size()) for (int i = 0; i < actions.size(); i++) {
				if (!actions.get(i).equals(def.actions.get(i))) {
					actionsMatch = false;
					break;
				}
			}
			if (def.activators.size() == activators.size()) for (int i = 0; i < activators.size(); i++) {
				if (!activators.get(i).equals(def.activators.get(i))) {
					activatorsMatch = false;
					break;
				}
			}
			return def.cooldown == cooldown &&
					def.ammoUse == ammoUse &&
					actionsMatch &&
					activatorsMatch;
					
		}
		return false;
	}

}
