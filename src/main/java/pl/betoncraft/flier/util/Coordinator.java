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
