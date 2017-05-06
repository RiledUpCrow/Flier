/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.activator;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.UsableItem;

/**
 * Checks if the player has a UsableItem.
 *
 * @author Jakub Sapalski
 */
public class ItemActivator extends DefaultActivator {
	
	private UsableItem item;

	public ItemActivator(ConfigurationSection section) throws LoadingException {
		super(section);
		item = Flier.getInstance().getItem(loader.loadString("item"));
	}

	@Override
	public boolean isActive(InGamePlayer player, UsableItem item) {
		UsableItem found = player.getKit().getItems().stream()
				.filter(i -> i.isSimilar(this.item))
				.findFirst()
				.orElse(null);
		return found != null && found.getAmount() > 0;
	}

}
