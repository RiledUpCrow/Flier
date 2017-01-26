/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.InGamePlayer;
import pl.betoncraft.flier.api.UsableItem;
import pl.betoncraft.flier.exception.LoadingException;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * Default implementation of UsableItem.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultUsableItem extends DefaultItem implements UsableItem {
	
	protected final int cooldown;
	protected final boolean consumable;
	protected final Where where;

	protected final Map<UUID, Long> cooldownData = new HashMap<>();

	public DefaultUsableItem(ConfigurationSection section) throws LoadingException {
		super(section);
		cooldown = ValueLoader.loadNonNegativeInt(section, "cooldown");
		consumable = ValueLoader.loadBoolean(section, "consumable");
		where = ValueLoader.loadEnum(section, "where", Where.class);
	}
	
	@Override
	public boolean cooldown(InGamePlayer data) {
		UUID player = data.getPlayer().getUniqueId();
		Long c = cooldownData.get(player);
		if (c == null || System.currentTimeMillis() >= c) {
			cooldownData.put(player, System.currentTimeMillis() + 50*cooldown);
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
}
