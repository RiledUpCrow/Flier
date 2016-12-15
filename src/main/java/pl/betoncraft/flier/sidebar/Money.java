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
 * A sidebar line showing amount of money.
 *
 * @author Jakub Sapalski
 */
public class Money implements SidebarLine {
	
	private InGamePlayer player;
	private double lastValue = 0;
	private String lastString;
	
	public Money(InGamePlayer player) {
		this.player = player;
	}

	@Override
	public String getText() {
		int m = player.getMoney();
		if (m < 1) {
			m = 0;
		}
		if (lastString == null || m != lastValue) {
			lastString = String.format("M: %d$", m);
			lastValue = m;
		}
		return lastString;
	}

}
