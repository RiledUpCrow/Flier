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

import org.bukkit.ChatColor;

import pl.betoncraft.flier.api.content.Wings;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.Kit;
import pl.betoncraft.flier.api.core.SidebarLine;
import pl.betoncraft.flier.util.LangManager;

/**
 * A sidebar line showing wings health.
 *
 * @author Jakub Sapalski
 */
public class Health implements SidebarLine {
	 
	private InGamePlayer player;
	private double lastValue = 0;
	private String lastString;
	private String translated;
	
	public Health(InGamePlayer player) {
		this.player = player;
		translated = LangManager.getMessage(player, "health");
	}

	@Override
	public String getText() {
		Kit c = player.getKit();
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
			lastString = format(translated, color, String.format("%.1f", h));
			lastValue = h;
		}
		return lastString;
	}
	
	private String format(String string, Object color, Object health) {
		return string
				.replace("{color}", color.toString())
				.replace("{health}", health.toString());
	}

}
