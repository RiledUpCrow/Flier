/**
 * Copyright (c) 2017 Jakub Sapalski
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
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
