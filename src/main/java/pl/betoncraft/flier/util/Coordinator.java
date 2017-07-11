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
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import net.md_5.bungee.api.ChatColor;
import pl.betoncraft.flier.api.Flier;

/**
 * Shows clicked block coordinates in chat.
 *
 * @author Jakub Sapalski
 */
public class Coordinator implements Listener {

	private static Set<UUID> set;

	public Coordinator() {
		set = new HashSet<>();
		Bukkit.getPluginManager().registerEvents(this, Flier.getInstance());
	}

	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		if (!event.hasBlock()) {
			return;
		}
		if (!set.contains(event.getPlayer().getUniqueId())) {
			return;
		}
		event.setCancelled(true);
		String blockType = event.getClickedBlock().getType().toString();
		Location loc = event.getClickedBlock().getLocation();
		String location = String.format("%d;%d;%d;%s",
				loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getWorld().getName());
		Flier.getInstance().getLogger().info(String.format("Coordinates for %s: %s", blockType, location));
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format(
				"tellraw %s [\"\",{\"text\":\"%sCoordinates for %s%s%s: %s%s\",\"clickEvent\":{\"action\":\"suggest"
				+ "_command\",\"value\":\"%s\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\","
				+ "\"extra\":[{\"text\":\"Click this to get block coordinates.\",\"color\":\"aqua\"}]}}}]",
				event.getPlayer().getName(), ChatColor.YELLOW, ChatColor.LIGHT_PURPLE, blockType, ChatColor.YELLOW,
				ChatColor.WHITE, location, location));
	}

	public static boolean isActive(UUID player) {
		return set.contains(player);
	}

	public static void addPlayer(UUID player) {
		set.add(player);
	}

	public static void removePlayer(UUID player) {
		set.remove(player);
	}

}
