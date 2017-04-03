/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.util;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import pl.betoncraft.flier.api.Flier;

/**
 * Prevents double clicks after changing held item.
 * 
 * Minecraft has this "feature" that when you click with an item and the item is
 * then immediately changed to something else and you're still holding the mouse
 * button, it will click again. This is problematic when the click actually
 * causes your item to change, since you'll be clicking multiple times by
 * accident.
 *
 * @author Jakub Sapalski
 */
public class DoubleClickBlocker {

	public static Set<UUID> uuids = new HashSet<>();

	/**
	 * Blocks specified player for 5 ticks.
	 * 
	 * @param player
	 *            Player to block
	 */
	public static void block(Player player) {
		UUID uuid = player.getUniqueId();
		if (uuids.add(uuid)) {
			Bukkit.getScheduler().runTaskLater(Flier.getInstance(), () -> uuids.remove(uuid), 5);
		}
	}

	/**
	 * Checks if specified player is still blocked.
	 * 
	 * @param player
	 *            Player to check
	 * @return whenever this player is still blocked
	 */
	public static boolean isBlocked(Player player) {
		return uuids.contains(player.getUniqueId());
	}

}
