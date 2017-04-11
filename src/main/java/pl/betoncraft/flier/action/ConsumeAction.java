/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.action;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.UsableItem;

/**
 * Consumes a UsableItem from the player.
 *
 * @author Jakub Sapalski
 */
public class ConsumeAction extends DefaultAction {
	
	private UsableItem item;

	public ConsumeAction(ConfigurationSection section) throws LoadingException {
		super(section);
		item = Flier.getInstance().getItem(loader.loadString("item"));
	}

	@Override
	public boolean act(InGamePlayer player) {
		player.consumeItem(item);
		return true;
	}

}
