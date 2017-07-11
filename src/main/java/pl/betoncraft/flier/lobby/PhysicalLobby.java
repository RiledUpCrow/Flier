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
package pl.betoncraft.flier.lobby;

import java.util.ArrayList;
import java.util.HashMap;
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

import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.util.DoubleClickBlocker;
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

		if (players.contains(uuid)) {
			event.setCancelled(true);
			if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_BLOCK) {
				return;
			}
			if (!event.hasBlock()) {
				return;
			}
			Block block = event.getClickedBlock();
			// this prevents double clicks on next tick
			if (DoubleClickBlocker.isBlocked(player)) {
				return;
			}
			// quitting
			if (block.equals(leave)) {
				removePlayer(player);
				DoubleClickBlocker.block(player);
				return;
			}
			// joining the game
			start.entrySet().stream()
					.filter(e -> e.getValue().equals(block.getLocation()))
					.findFirst()
					.ifPresent(e -> {
						JoinResult res = joinGame(player, e.getKey());
						DefaultLobby.joinMessage(player, res);
						DoubleClickBlocker.block(player);
					});
		} else {
			// joining
			if (event.hasBlock() && join.contains(event.getClickedBlock())) {
				event.setCancelled(true);
				// this prevents double clicks on next tick
				if (DoubleClickBlocker.isBlocked(player)) {
					return;
				}
				addPlayer(player);
				DoubleClickBlocker.block(player);
				return;
			}
		}
	}

}
