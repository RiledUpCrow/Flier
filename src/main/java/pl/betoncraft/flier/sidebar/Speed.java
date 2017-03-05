/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.sidebar;

import org.bukkit.ChatColor;
import org.bukkit.util.Vector;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.SidebarLine;

/**
 * A sidebar line showing player's speed.
 *
 * @author Jakub Sapalski
 */
public class Speed implements SidebarLine {
	
	private InGamePlayer player;
	private double lastValue = 0;
	private String lastString;
	
	public Speed(InGamePlayer player) {
		this.player = player;
	}

	@Override
	public String getText() {
		Vector vel = player.getPlayer().getVelocity();
		double s = vel.length() * 10;
		double vertical = vel.getY();
		if (s < 1) {
			s = 0;
		}
		if (lastString == null || s != lastValue) {
			String color;
			if (s == 0) {
				color = ChatColor.GRAY.toString();
			} else if (vertical > 0.5) {
				color = ChatColor.GREEN.toString();
			} else if (vertical < -0.5) {
				color = ChatColor.RED.toString();
			} else {
				color = ChatColor.YELLOW.toString();
			}
			lastString = String.format("S: %s%.1f~", color, s);
			lastValue = s;
		}
		return lastString;
	}

}
