/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.action;

import java.util.Optional;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.content.Game.Attitude;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.UsableItem;

/**
 * Targets other players with a compass.
 *
 * @author Jakub Sapalski
 */
public class TargetAction extends DefaultAction {
	
	private static final String TARGET = "target";

	private final Attitude target;
	
	public TargetAction(ConfigurationSection section) throws LoadingException {
		super(section, false, false);
		this.target = loader.loadEnum(TARGET, Attitude.HOSTILE, Attitude.class);
	}

	@Override
	public boolean act(Optional<InGamePlayer> creator, Optional<InGamePlayer> source,
			InGamePlayer player, Optional<UsableItem> item) {
		if (player.isPlaying()) {
			InGamePlayer nearest = null;
			double distance = 0;
			Attitude target = modMan.modifyEnum(TARGET, this.target);
			for (InGamePlayer data : player.getGame().getPlayers().values()) {
				if (player.getGame().getAttitude(data, player) == target) {
					double dist = player.getPlayer().getLocation().distanceSquared(data.getPlayer().getLocation());
					if (distance == 0 || dist < distance) {
						distance = dist;
						nearest = data;
					}
				}
			}
			if (nearest != null) {
				player.getPlayer().setCompassTarget(nearest.getPlayer().getLocation());
			} else {
				player.getPlayer().setCompassTarget(player.getGame().getCenter());
			}
		}
		return true;
	}

}
