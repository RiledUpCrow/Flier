/**
 * Copyright (c) 2017 Jakub Sapalski
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */
package pl.betoncraft.flier.action;

import java.util.Optional;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.content.Game.Attitude;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.Owner;

/**
 * Targets other players with a compass.
 *
 * @author Jakub Sapalski
 */
public class TargetAction extends DefaultAction {
	
	private static final String TARGET = "target";

	private final Attitude target;
	
	public TargetAction(ConfigurationSection section, Optional<Owner> owner) throws LoadingException {
		super(section, owner);
		this.target = loader.loadEnum(TARGET, Attitude.HOSTILE, Attitude.class);
	}

	@Override
	public boolean act(InGamePlayer player, InGamePlayer source) {
		if (player.isPlaying()) {
			InGamePlayer nearest = null;
			double distance = Double.MAX_VALUE;
			Attitude target = modMan.modifyEnum(TARGET, this.target);
			for (InGamePlayer data : player.getGame().getPlayers().values()) {
				if (data.equals(player)) {
					continue;
				}
				if (player.getGame().getAttitude(data, player) == target) {
					double dist = player.getLocation().distanceSquared(data.getLocation());
					if (dist < distance) {
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
