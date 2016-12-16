/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.sidebar;

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
		Engine engine = c == null ? null : c.getCurrentEngine();
		double f = engine == null ? 0 : 100 * player.getFuel() / engine.getMaxFuel();
		if (lastString == null || f != lastValue) {
			lastString = String.format("F: %.1f%%", f);
			lastValue = f;
		}
		return lastString;
	}

}
