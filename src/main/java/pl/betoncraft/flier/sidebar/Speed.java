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
import pl.betoncraft.flier.util.LangManager;

/**
 * A sidebar line showing player's speed.
 *
 * @author Jakub Sapalski
 */
public class Speed implements SidebarLine {
	
	private InGamePlayer player;
	private double lastValue = 0;
	private String lastString;
	private String translated;
	
	public Speed(InGamePlayer player) {
		this.player = player;
		translated = LangManager.getMessage(player, "speed");
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
			lastString = format(translated, color, String.format("%.1f", s));
			lastValue = s;
		}
		return lastString;
	}
	
	private String format(String string, Object color, Object speed) {
		return string
				.replace("{color}", color.toString())
				.replace("{speed}", speed.toString());
	}

}
