/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.sidebar;

import pl.betoncraft.flier.api.InGamePlayer;
import pl.betoncraft.flier.api.SidebarLine;

/**
 * A sidebar line showing player's altitude.
 *
 * @author Jakub Sapalski
 */
public class Altitude implements SidebarLine {
	
	private InGamePlayer player;
	private double lastValue = 0;
	private String lastString;
	
	public Altitude(InGamePlayer player) {
		this.player = player;
	}

	@Override
	public String getText() {
		double a = player.getPlayer().getLocation().getY() - 64;
		if (lastString == null || a != lastValue) {
			lastString = String.format("A: %.1fm", a);
			lastValue = a;
		}
		return lastString;
	}

}
