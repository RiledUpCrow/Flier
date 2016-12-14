/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.sidebar;

import pl.betoncraft.flier.api.InGamePlayer;
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
		Wings wings = player.getClazz().getWings();
		double h = wings == null ? 0 : 100 * player.getHealth() / wings.getHealth();
		if (lastString == null || h != lastValue) {
			lastString = String.format("H: %.1f%%", h);
			lastValue = h;
		}
		return lastString;
	}

}
