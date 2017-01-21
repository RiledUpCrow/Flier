/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.item;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.InGamePlayer;
import pl.betoncraft.flier.api.UsableItem;
import pl.betoncraft.flier.exception.LoadingException;

/**
 * Default implementation of UsableItem.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultUsableItem extends DefaultItem implements UsableItem {
	
	protected int cooldown = 40;
	protected boolean consumable = false;
	protected boolean onlyAir = false;

	protected final Map<UUID, Long> cooldownData = new HashMap<>();

	public DefaultUsableItem(ConfigurationSection section) throws LoadingException {
		super(section);
		cooldown = section.getInt("cooldown", cooldown);
		consumable = section.getBoolean("consumable", consumable);
		onlyAir = section.getBoolean("only_air", onlyAir);
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
	public boolean onlyAir() {
		return onlyAir;
	}
}
