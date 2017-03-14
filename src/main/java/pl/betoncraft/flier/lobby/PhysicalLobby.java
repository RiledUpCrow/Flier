/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.lobby;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.core.defaults.DefaultLobby;
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
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onClick(PlayerInteractEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		if (!event.hasBlock()) {
			return;
		}

		Player player = event.getPlayer();
		UUID uuid = player.getUniqueId();
		Block block = event.getClickedBlock();
		boolean inside = players.contains(uuid);

		if (inside) {
			event.setCancelled(true);
			// quitting
			if (block.equals(leave)) {
				removePlayer(player);
				return;
			}
			// joining the game
			if (block.equals(start)) {
				currentGame.addPlayer(player);
				return;
			}
		} else {
			// joining
			if (join.contains(block)) {
				event.setCancelled(true);
				addPlayer(player);
				return;
			}
		}
	}

}
