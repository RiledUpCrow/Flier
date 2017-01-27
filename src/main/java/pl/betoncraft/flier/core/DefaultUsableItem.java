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
import pl.betoncraft.flier.api.InGamePlayer;
import pl.betoncraft.flier.api.Item;
import pl.betoncraft.flier.api.UsableItem;
import pl.betoncraft.flier.api.Usage;
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
	protected final List<Usage> usages = new ArrayList<>();

	protected long time = 0;

	public DefaultUsableItem(ConfigurationSection section) throws LoadingException {
		super(section);
		cooldown = ValueLoader.loadNonNegativeInt(section, "cooldown");
		consumable = ValueLoader.loadBoolean(section, "consumable");
		where = ValueLoader.loadEnum(section, "where", Where.class);
		ConfigurationSection usageSection = section.getConfigurationSection("usages");
		if (usageSection != null) for (String id : usageSection.getKeys(false)) {
			try {
				usages.add(new DefaultUsage(usageSection.getConfigurationSection(id)));
			} catch (LoadingException e) {
				throw (LoadingException) new LoadingException(String.format("Error in '%s' usage.", id)).initCause(e);
			}
		}
	}
	
	@Override
	public int getCooldown() {
		return cooldown;
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
	public List<Usage> getUsages() {
		return usages;
	}

	@Override
	public boolean use(InGamePlayer player) {
		boolean used = false;
		if (System.currentTimeMillis() >= time) {
			usages:
			for (Usage usage : usages) {
				for (Activator activator : usage.getActivators()) {
					if (!activator.isActive(player, this)) {
						continue usages;
					}
				}
				used = true;
				time = System.currentTimeMillis() + 50*cooldown;
				for (Action action : usage.getActions()) {
					action.act(player);
				}
			}
		}
		return used;
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
