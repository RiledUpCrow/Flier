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

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.SidebarLine;
import pl.betoncraft.flier.api.core.UsableItem;
import pl.betoncraft.flier.util.LangManager;

/**
 * A sidebar line showing ammunition of currently held UsableItem.
 *
 * @author Jakub Sapalski
 */
public class Ammo implements SidebarLine {
	
	private InGamePlayer player;
	private boolean inactive = false;
	private int lastAmmo = 0;
	private int lastMaxAmmo = 0;
	private String lastString;
	private String translated;
	
	public Ammo(InGamePlayer player) {
		this.player = player;
		this.translated = LangManager.getMessage(player, "ammo");
	}

	@Override
	public String getText() {
		int slot = player.getPlayer().getInventory().getHeldItemSlot();
		UsableItem item = null;
		for (UsableItem i : player.getKit().getItems()) {
			if (i.slot() == slot) {
				item = i;
				break;
			}
		}
		if (item == null || item.getMaxAmmo() == 0) {
			if (!inactive) {
				inactive = true;
				lastString = format(translated, ChatColor.GRAY, "-", "-");
			}
		} else {
			int ammo = item.getAmmo();
			int maxAmmo = item.getMaxAmmo();
			if (inactive || lastAmmo != ammo || lastMaxAmmo != maxAmmo) {
				lastAmmo = ammo;
				lastMaxAmmo = maxAmmo;
				String color;
				if (ammo == 0) {
					color = ChatColor.BLACK.toString();
				} else if (ammo > maxAmmo / 4.0 * 3.0) {
					color = ChatColor.GREEN.toString();
				} else if (ammo > maxAmmo / 4.0) {
					color = ChatColor.YELLOW.toString();
				} else {
					color = ChatColor.RED.toString();
				}
				inactive = false;
				lastString = format(translated, color, ammo, maxAmmo);
			}
		}
		return lastString;
	}
	
	private String format(String string, Object color, Object current, Object max) {
		return string
				.replace("{color}", color.toString())
				.replace("{current}", current.toString())
				.replace("{max}", max.toString());
	}

}
