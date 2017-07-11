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
import org.bukkit.util.Vector;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.SidebarLine;
import pl.betoncraft.flier.util.LangManager;

/**
 * A sidebar line showing player's speed.
 *
 * @author Jakub Sapalski
 */
public class Speed implements SidebarLine {
	
	private InGamePlayer player;
	private double lastValue = 0;
	private String lastString;
	private String translated;
	
	public Speed(InGamePlayer player) {
		this.player = player;
		translated = LangManager.getMessage(player, "speed");
	}

	@Override
	public String getText() {
		Vector vel = player.getPlayer().getVelocity();
		double s = vel.length() * 10;
		double vertical = vel.getY();
		if (s < 1) {
			s = 0;
		}
		if (lastString == null || s != lastValue) {
			String color;
			if (s == 0) {
				color = ChatColor.GRAY.toString();
			} else if (vertical > 0.5) {
				color = ChatColor.GREEN.toString();
			} else if (vertical < -0.5) {
				color = ChatColor.RED.toString();
			} else {
				color = ChatColor.YELLOW.toString();
			}
			lastString = format(translated, color, String.format("%.1f", s));
			lastValue = s;
		}
		return lastString;
	}
	
	private String format(String string, Object color, Object speed) {
		return string
				.replace("{color}", color.toString())
				.replace("{speed}", speed.toString());
	}

}
