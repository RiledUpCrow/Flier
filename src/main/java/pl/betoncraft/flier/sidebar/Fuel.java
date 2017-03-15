/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.sidebar;

import org.bukkit.ChatColor;

import pl.betoncraft.flier.api.content.Engine;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.PlayerClass;
import pl.betoncraft.flier.api.core.SidebarLine;
import pl.betoncraft.flier.util.LangManager;

/**
 * A sidebar line showing fuel.
 *
 * @author Jakub Sapalski
 */
public class Fuel implements SidebarLine {
	
	private InGamePlayer player;
	private double lastValue = 0;
	private String lastString;
	private String translated;
	
	public Fuel(InGamePlayer player) {
		this.player = player;
		this.translated = LangManager.getMessage(player, "fuel");
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
			lastString = format(translated, color, String.format("%.1f", f));
			lastValue = f;
		}
		return lastString;
	}
	
	private String format(String string, Object color, Object fuel) {
		return string
				.replace("{color}", color.toString())
				.replace("{fuel}", fuel.toString());
	}

}
