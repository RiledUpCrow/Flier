/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.sidebar;

import org.bukkit.ChatColor;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.SidebarLine;
import pl.betoncraft.flier.api.core.UsableItem;

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
	private String lastString = String.format("M: %s0/0", ChatColor.BLACK);
	
	public Ammo(InGamePlayer player) {
		this.player = player;
	}

	@Override
	public String getText() {
		int slot = player.getPlayer().getInventory().getHeldItemSlot();
		UsableItem item = null;
		for (UsableItem i : player.getClazz().getItems()) {
			if (i.slot() == slot) {
				item = i;
				break;
			}
		}
		if (item == null || item.getMaxAmmo() == 0) {
			if (!inactive) {
				inactive = true;
				lastString = String.format("M: %s-/-", ChatColor.GRAY);
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
				lastString = String.format("M: %s%d/%d", color, ammo, maxAmmo);
			}
		}
		return lastString;
	}

}
