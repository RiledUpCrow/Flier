/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.action;

import java.util.Optional;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.UsableItem;
import pl.betoncraft.flier.api.core.Usage.Where;
import pl.betoncraft.flier.util.Position;

/**
 * Kills the player. Due to DefaultGame mechanics this only works when the player is not falling.
 *
 * @author Jakub Sapalski
 */
public class SuicideAction extends DefaultAction {

	public SuicideAction(ConfigurationSection section) {
		super(section, false, false);
	}

	@Override
	public boolean act(Optional<InGamePlayer> creator, Optional<InGamePlayer> source,
			InGamePlayer target, Optional<UsableItem> item) {
		if (Position.check(target.getPlayer(), Where.NO_FALL)) {
			target.getPlayer().damage(target.getPlayer().getHealth() * 100);
			return true;
		}
		return false;
	}

}
