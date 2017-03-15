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
import pl.betoncraft.flier.util.LangManager;

/**
 * A sidebar line showing cooldown progress for currently held item.
 *
 * @author Jakub Sapalski
 */
public class Reload implements SidebarLine {
	
	private InGamePlayer player;
	private String translated;
	
	public Reload(InGamePlayer player) {
		this.player = player;
		translated = LangManager.getMessage(player, "reload");
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
		if (item == null || !item.getUsages().stream().filter(usage -> usage.canUse(player)).findAny().isPresent()) {
			return format(translated, ChatColor.GRAY, "---");
		}
		int ticks = item.getCooldown();
		if (ticks == 0) {
			return format(translated, ChatColor.GREEN, "***");
		}
		return format(translated, ChatColor.YELLOW, String.format("%.1fs", ticks / 20.0));
	}
	
	private String format(String string, Object color, Object status) {
		return string
				.replace("{color}", color.toString())
				.replace("{status}", status.toString());
	}

}
