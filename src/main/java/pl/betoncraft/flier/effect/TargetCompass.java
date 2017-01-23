/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.effect;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.Effect;
import pl.betoncraft.flier.api.InGamePlayer;
import pl.betoncraft.flier.api.Game.Attitude;
import pl.betoncraft.flier.core.ValueLoader;
import pl.betoncraft.flier.exception.LoadingException;

/**
 * Targets hostile players.
 *
 * @author Jakub Sapalski
 */
public class TargetCompass implements Effect {
	
	private final Attitude target;
	
	public TargetCompass(ConfigurationSection section) throws LoadingException {
		this.target = ValueLoader.loadEnum(section, "target", Attitude.class);
	}

	@Override
	public void apply(InGamePlayer data) {
		if (data.isPlaying()) {
			InGamePlayer nearest = null;
			double distance = 0;
			for (InGamePlayer d : data.getLobby().getGame().getPlayers().values()) {
				if (data.getLobby().getGame().getAttitude(d, data) == target) {
					double dist = data.getPlayer().getLocation().distanceSquared(d.getPlayer().getLocation());
					if (distance == 0 || dist < distance) {
						distance = dist;
						nearest = d;
					}
				}
			}
			if (nearest != null) {
				data.getPlayer().setCompassTarget(nearest.getPlayer().getLocation());
			}
		}
	}
	
	@Override
	public boolean fast() {
		return false;
	}

}
