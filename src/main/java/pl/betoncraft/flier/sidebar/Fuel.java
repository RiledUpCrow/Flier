/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.sidebar;

import org.bukkit.ChatColor;

import pl.betoncraft.flier.api.Engine;
import pl.betoncraft.flier.api.InGamePlayer;
import pl.betoncraft.flier.api.PlayerClass;
import pl.betoncraft.flier.api.SidebarLine;

/**
 * A sidebar line showing fuel.
 *
 * @author Jakub Sapalski
 */
public class Fuel implements SidebarLine {
	
	private InGamePlayer player;
	private double lastValue = 0;
	private String lastString;
	
	public Fuel(InGamePlayer player) {
		this.player = player;
	}

	@Override
	public String getText() {
		PlayerClass c = player.getClazz();
		Engine engine = c == null ? null : c.getEngine();
		double f = engine == null ? 0 : 100 * engine.getFuel() / engine.getMaxFuel();
		if (lastString == null || f != lastValue) {
			String color;
			if (f == 0) {
				color = ChatColor.BLACK.toString();
			} else if (f > 75) {
				color = ChatColor.GREEN.toString();
			} else if (f < 25) {
				color = ChatColor.RED.toString();
			} else {
				color = ChatColor.YELLOW.toString();
			}
			lastString = String.format("F: %s%.1f%%", color, f);
			lastValue = f;
		}
		return lastString;
	}

}
