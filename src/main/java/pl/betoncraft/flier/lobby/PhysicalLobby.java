/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.lobby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.core.defaults.DefaultLobby;
import pl.betoncraft.flier.util.Utils;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * Physical lobby with fixed classes selected by clicking on blocks.
 *
 * @author Jakub Sapalski
 */
public class PhysicalLobby extends DefaultLobby {

	private List<Block> join = new ArrayList<>();
	private Block start;
	private Block leave;
	private Map<Block, Button> blocks = new HashMap<>();

	private List<UUID> blocked = new LinkedList<>();

	public PhysicalLobby(ConfigurationSection section) throws LoadingException {
		super(section);
		int index = 0;
		for (String loc : section.getStringList("join")) {
			index++;
			try {
				join.add(Utils.parseLocation(loc).getBlock());
			} catch (LoadingException e) {
				throw (LoadingException) new LoadingException(String.format("Error in %s join location", index)).initCause(e);
			}
		}
		start = loader.loadLocation("start").getBlock();
		leave = loader.loadLocation("leave").getBlock();
		ConfigurationSection buttonsSection = section.getConfigurationSection("buttons");
		if (buttonsSection != null) for (String i : buttonsSection.getKeys(false)) {
			ConfigurationSection blockSection = buttonsSection.getConfigurationSection(i);
			try {
				ValueLoader loader = new ValueLoader(blockSection);
				Block block = loader.loadLocation("block").getBlock();
				blocks.put(block, buttons.get(i));
			} catch (LoadingException e) {
				throw (LoadingException) new LoadingException(String.format("Error in '%s' button.", i)).initCause(e);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onClick(PlayerInteractEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		if (block == null) {
			return;
		}
		// this prevents double clicks on next tick
		if (blocked.contains(player.getUniqueId())) {
			return;
		} else {
			blocked.add(player.getUniqueId());
			new BukkitRunnable() {
				@Override
				public void run() {
					blocked.remove(player.getUniqueId());
				}
			}.runTaskLater(Flier.getInstance(), 5);
		}
		// handle the click
		if (join.contains(block)) {
			addPlayer(player);
		} else if (block.equals(leave)) {
			removePlayer(player);
		} else {
			InGamePlayer data = players.get(player.getUniqueId());
			if (data == null) {
				return;
			}
			if (data.isPlaying()) {
				return;
			}
			if (block.equals(start)) {
				currentGame.startPlayer(data);
			} else {
				Button button = blocks.get(block);
				if (button == null) {
					return;
				}
				handleItems(data, button, event.getAction() == Action.LEFT_CLICK_BLOCK);
			}
		}
		event.setCancelled(true);
	}

}
