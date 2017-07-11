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
