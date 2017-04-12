/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.action;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.UsableItem;

/**
 * Removes the player from his current Game.
 *
 * @author Jakub Sapalski
 */
public class LeaveGameAction extends DefaultAction {

	public LeaveGameAction(ConfigurationSection section) {
		super(section);
	}

	@Override
	public boolean act(InGamePlayer player, UsableItem item) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Flier.getInstance(),
				() -> player.getGame().getLobby().leaveGame(player.getPlayer()));
		return true;
	}

}
