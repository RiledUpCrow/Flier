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

import pl.betoncraft.flier.Flier;
import pl.betoncraft.flier.api.InGamePlayer;
import pl.betoncraft.flier.core.defaults.DefaultLobby;
import pl.betoncraft.flier.exception.LoadingException;
import pl.betoncraft.flier.util.Utils;

/**
 * Physical lobby with fixed classes selected by clicking on blocks.
 *
 * @author Jakub Sapalski
 */
public class PhysicalLobby extends DefaultLobby {

	private List<Block> join = new ArrayList<>();
	private Block start;
	private Block leave;
	private Map<Block, ItemBlock> blocks = new HashMap<>();

	private List<UUID> blocked = new LinkedList<>();

	public PhysicalLobby(ConfigurationSection section) throws LoadingException {
		super(section);
		for (String loc : section.getStringList("join")) {
			join.add(Utils.parseLocation(loc).getBlock());
		}
		start = loader.loadLocation("start").getBlock();
		leave = loader.loadLocation("leave").getBlock();
		ConfigurationSection blocksSection = section.getConfigurationSection("blocks");
		if (blocksSection != null) for (String i : blocksSection.getKeys(false)) {
			ConfigurationSection blockSection = blocksSection.getConfigurationSection(i);
			try {
				ItemBlock itemBlock = new ItemBlock(blockSection);
				blocks.put(itemBlock.block, itemBlock);
			} catch (LoadingException e) {
				throw (LoadingException) new LoadingException(String.format("Error in '%s' block.", i)).initCause(e);
			}
		}
	}

	private class ItemBlock {

		private Block block;
		private CostlySet set;

		private ItemBlock(ConfigurationSection section) throws LoadingException {
			block = Utils.parseLocation(section.getString("block")).getBlock();
			set = items.get(section.getString("item"));
		}

	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onClick(PlayerInteractEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
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
			if (data != null) {
				if (data.isPlaying()) {
					return;
				}
				if (block.equals(start)) {
					currentGame.startPlayer(data);
				} else {
					handleItems(data, blocks.get(block).set);
				}
			}
		}
	}

}
