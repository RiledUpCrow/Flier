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
	
	protected ValueLoader loader;
	
	protected int cooldown;
	protected int ammoUse;
	protected final Usage.Where where;
	protected List<Activator> activators = new ArrayList<>();
	protected List<Action> actions = new ArrayList<>();
	
	public DefaultUsage(ConfigurationSection section) throws LoadingException {
		loader = new ValueLoader(section);
		cooldown = loader.loadNonNegativeInt("cooldown", 0);
		ammoUse = loader.loadInt("ammo_use", 0);
		where = loader.loadEnum("where", Usage.Where.EVERYWHERE, Usage.Where.class);
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
