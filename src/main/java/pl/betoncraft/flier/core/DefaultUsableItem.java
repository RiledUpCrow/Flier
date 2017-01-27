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
import pl.betoncraft.flier.api.InGamePlayer;
import pl.betoncraft.flier.api.Item;
import pl.betoncraft.flier.api.UsableItem;
import pl.betoncraft.flier.core.defaults.DefaultItem;
import pl.betoncraft.flier.exception.LoadingException;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * Default implementation of UsableItem.
 *
 * @author Jakub Sapalski
 */
public class DefaultUsableItem extends DefaultItem implements UsableItem {
	
	protected final int cooldown;
	protected final boolean consumable;
	protected final Where where;
	protected final List<Action> actions = new ArrayList<>();

	protected long time = 0;

	public DefaultUsableItem(ConfigurationSection section) throws LoadingException {
		super(section);
		cooldown = ValueLoader.loadNonNegativeInt(section, "cooldown");
		consumable = ValueLoader.loadBoolean(section, "consumable");
		where = ValueLoader.loadEnum(section, "where", Where.class);
		for (String id : section.getStringList("actions")) {
			actions.add(Flier.getInstance().getAction(id));
		}
	}
	
	@Override
	public boolean cooldown() {
		if (System.currentTimeMillis() >= time) {
			time = System.currentTimeMillis() + 50*cooldown;
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public boolean isConsumable() {
		return consumable;
	}
	
	@Override
	public Where where() {
		return where;
	}

	@Override
	public List<Action> getActions() {
		return actions;
	}

	@Override
	public boolean use(InGamePlayer player) {
		boolean acted = false;
		for (Action action : actions) {
			if (action.act(player)) {
				acted = true;
			}
		}
		return acted;
	}
	
	@Override
	public Item replicate() {
		try {
			return Flier.getInstance().getItem(id);
		} catch (LoadingException e) {
			return null; // dead code
		}
	}
}
