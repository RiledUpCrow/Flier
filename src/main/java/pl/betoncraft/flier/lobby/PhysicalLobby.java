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

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.flier.api.Flier;
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
	private Map<String, Location> start = new HashMap<>();
	private Block leave;

	private final List<UUID> blocked = new LinkedList<>();

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
		ConfigurationSection startSection = section.getConfigurationSection("start");
		if (startSection != null) {
			ValueLoader startLoader = new ValueLoader(startSection);
			for (String key : startSection.getKeys(false)) {
				if (!gameSets.containsKey(key)) {
					throw new LoadingException(String.format("Start location points to non-existing game '%s'.", key));
				}
				start.put(key, startLoader.loadLocation(key));
			}
		}
		leave = loader.loadLocation("leave").getBlock();
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onClick(PlayerInteractEvent event) {
		if (event.isCancelled()) {
			return;
		}
		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		boolean inside = players.contains(uuid);

		if (inside) {
			event.setCancelled(true);
			if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
				return;
			}
			if (!event.hasBlock()) {
				return;
			}
			Block block = event.getClickedBlock();
			// this prevents double clicks on next tick
			if (blocked.contains(event.getPlayer().getUniqueId())) {
				return;
			}
			// quitting
			if (block.equals(leave)) {
				removePlayer(player);
				block(player.getUniqueId());
				return;
			}
			// joining the game
			start.entrySet().stream()
					.filter(e -> e.getValue().equals(block.getLocation()))
					.findFirst()
					.ifPresent(e -> {
						joinGame(player, e.getKey());
						block(player.getUniqueId());
					});
		} else {
			// joining
			if (event.hasBlock() && join.contains(event.getClickedBlock())) {
				event.setCancelled(true);
				// this prevents double clicks on next tick
				if (blocked.contains(event.getPlayer().getUniqueId())) {
					return;
				}
				addPlayer(player);
				block(player.getUniqueId());
				return;
			}
		}
	}
	
	private void block(UUID uuid) {
		blocked.add(uuid);
		new BukkitRunnable() {
			@Override
			public void run() {
				blocked.remove(uuid);
			}
		}.runTaskLater(Flier.getInstance(), 5);
	}

}
