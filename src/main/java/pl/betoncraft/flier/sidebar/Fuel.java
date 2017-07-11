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

import pl.betoncraft.flier.api.content.Engine;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.Kit;
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
		Kit c = player.getKit();
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
