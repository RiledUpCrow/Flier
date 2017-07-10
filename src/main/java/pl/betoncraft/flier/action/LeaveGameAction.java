/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.action;

import java.util.Optional;

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
		super(section, false, false);
	}

	@Override
	public boolean act(Optional<InGamePlayer> creator, Optional<InGamePlayer> source,
			InGamePlayer target, Optional<UsableItem> item) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Flier.getInstance(),
				() -> target.getGame().getLobby().leaveGame(target.getPlayer()));
		return true;
	}

}
