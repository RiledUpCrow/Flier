/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.sidebar;

import net.md_5.bungee.api.ChatColor;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.SidebarLine;
import pl.betoncraft.flier.util.LangManager;

/**
 * A sidebar line showing amount of money.
 *
 * @author Jakub Sapalski
 */
public class Money implements SidebarLine {
	
	private InGamePlayer player;
	private double lastValue = 0;
	private String lastString;
	private String translated;
	
	public Money(InGamePlayer player) {
		this.player = player;
		translated = LangManager.getMessage(player, "money");
	}

	@Override
	public String getText() {
		int m = player.getMoney();
		if (m < 1) {
			m = 0;
		}
		if (lastString == null || m != lastValue) {
			lastString = format(translated, ChatColor.LIGHT_PURPLE, m);
			lastValue = m;
		}
		return lastString;
	}
	
	private String format(String string, Object color, Object money) {
		return string
				.replace("{color}", color.toString())
				.replace("{money}", money.toString());
	}

}
