/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.item;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.UsableItem;
import pl.betoncraft.flier.core.PlayerData;

/**
 * A simple item without any additional mechanics.
 *
 * @author Jakub Sapalski
 */
public class VanillaItem extends DefaultItem implements UsableItem {
	
	private int slot = 8;

	public VanillaItem(ConfigurationSection section) {
		super(section);
		slot = section.getInt("slot", slot);
	}

	@Override
	public boolean use(PlayerData player) {
		return true;
	}

	@Override
	public void cooldown(PlayerData player) {}

	@Override
	public boolean isConsumable() {
		return false;
	}

	@Override
	public boolean onlyAir() {
		return false;
	}

	@Override
	public int slot() {
		return slot;
	}

}
