/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.action;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.Usage.Where;
import pl.betoncraft.flier.util.Position;

/**
 * Kills the player. Due to DefaultGame mechanics this only works when the player is not falling.
 *
 * @author Jakub Sapalski
 */
public class SuicideAction extends DefaultAction {

	public SuicideAction(ConfigurationSection section) {
		super(section);
	}

	@Override
	public boolean act(InGamePlayer player) {
		if (Position.check(player.getPlayer(), Where.NO_FALL)) {
			player.getPlayer().damage(player.getPlayer().getHealth() * 100);
			return true;
		}
		return false;
	}

}
