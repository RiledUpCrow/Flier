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
