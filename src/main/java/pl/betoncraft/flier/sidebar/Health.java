/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.sidebar;

import org.bukkit.ChatColor;

import pl.betoncraft.flier.api.InGamePlayer;
import pl.betoncraft.flier.api.PlayerClass;
import pl.betoncraft.flier.api.SidebarLine;
import pl.betoncraft.flier.api.Wings;

/**
 * A sidebar line showing wings health.
 *
 * @author Jakub Sapalski
 */
public class Health implements SidebarLine {
	 
	private InGamePlayer player;
	private double lastValue = 0;
	private String lastString;
	
	public Health(InGamePlayer player) {
		this.player = player;
	}

	@Override
	public String getText() {
		PlayerClass c = player.getClazz();
		Wings wings = c == null ? null : c.getWings();
		double h = wings == null ? 0 : 100 * wings.getHealth() / wings.getMaxHealth();
		if (lastString == null || h != lastValue) {
			String color;
			if (h == 0) {
				color = ChatColor.BLACK.toString();
			} else if (h > 75) {
				color = ChatColor.GREEN.toString();
			} else if (h < 25) {
				color = ChatColor.RED.toString();
			} else {
				color = ChatColor.YELLOW.toString();
			}
			lastString = String.format("H: %s%.1f%%", color, h);
			lastValue = h;
		}
		return lastString;
	}

}
