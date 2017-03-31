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
 * A sidebar line showing time left to Game's end.
 *
 * @author Jakub Sapalski
 */
public class Time implements SidebarLine {
	
	private InGamePlayer player;
	private int lastMinutes, lastSeconds;
	private String lastString;
	private String translated;
	
	public Time(InGamePlayer player) {
		this.player = player;
		translated = LangManager.getMessage(player, "time");
	}

	@Override
	public String getText() {
		int t = player.getGame().getTimeLeft() / 20;
		if (t < 1) {
			t = 0;
		}
		int minutes = (int) Math.floor(t / 60.0);
		int seconds = t % 60;
		if (lastString == null || minutes != lastMinutes || seconds != lastSeconds) {
			lastString = format(translated, ChatColor.GOLD, minutes, seconds);
			lastMinutes = minutes;
			lastSeconds = seconds;
		}
		return lastString;
	}
	
	private String format(String string, Object color, Object minutes, Object seconds) {
		return string
				.replace("{color}", color.toString())
				.replace("{min}", minutes.toString())
				.replace("{s}", String.format("%02d", seconds));
	}

}
