/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.lobby;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import pl.betoncraft.flier.Flier;
import pl.betoncraft.flier.api.Engine;
import pl.betoncraft.flier.api.Game;
import pl.betoncraft.flier.api.Lobby;
import pl.betoncraft.flier.api.PlayerClass;
import pl.betoncraft.flier.api.UsableItem;
import pl.betoncraft.flier.api.Wings;
import pl.betoncraft.flier.core.Utils;

/**
 * Physical lobby with fixed classes selected by clicking on blocks.
 *
 * @author Jakub Sapalski
 */
public class FixedPhysicalLobby implements Lobby, Listener {
	
	private Game game;
	private List<Block> join;
	private Location spawn;
	private Block start;
	private Block leave;
	private Map<Block, PlayerClass> classes = new HashMap<>();

	public FixedPhysicalLobby(ConfigurationSection section) {
		spawn = Utils.parseLocation(section.getString("spawn"));
		join = section.getStringList("join").stream().map(e -> Utils.parseLocation(e).getBlock()).collect(Collectors.toList());
		start = Utils.parseLocation(section.getString("start")).getBlock();
		leave = Utils.parseLocation(section.getString("leave")).getBlock();
		ConfigurationSection classesRootSection = section.getConfigurationSection("classes");
		for (String c : classesRootSection.getKeys(false)) {
			ConfigurationSection classSection = classesRootSection.getConfigurationSection(c);
			classes.put(Utils.parseLocation(classSection.getString("block")).getBlock(), new FixedClass(classSection));
		}
		Bukkit.getPluginManager().registerEvents(this, Flier.getInstance());
	}
	
	private class FixedClass implements PlayerClass {
		
		private String name;
		private Engine engine;
		private Map<UsableItem, Integer> items = new HashMap<>();
		private Wings wings;
		
		public FixedClass(ConfigurationSection section) {
			name = Utils.capitalize(section.getName());
			engine = Flier.getInstance().getEngine(section.getString("engine", "default"));
			List<String> itemNames = section.getStringList("items");
			for (String item : itemNames) {
				int amount = 1;
				if (item.contains(" ")) {
					try {
						amount = Integer.parseInt(item.substring(item.indexOf(' ') + 1));
						item = item.substring(0, item.indexOf(' '));
					} catch (NumberFormatException e) {}
				}
				if (amount <= 0) {
					amount = 1;
				}
				UsableItem ui = Flier.getInstance().getItem(item);
				if (ui != null) {
					items.put(ui, amount);
				} else {}
			}
			wings = Flier.getInstance().getWings(section.getString("wings", "default"));
		}
		
		@Override
		public Engine getEngine() {
			return engine;
		}
		
		@Override
		public Map<UsableItem, Integer> getItems() {
			return items;
		}
		
		@Override
		public Wings getWings() {
			return wings;
		}

		@Override
		public String getName() {
			return name;
		}

	}
	
	@Override
	public void setGame(Game game) {
		this.game = game;
	}
	
	@Override
	public Game getGame() {
		return game;
	}

	@Override
	public Location getSpawn() {
		return spawn;
	}
	
	@Override
	public void stop() {
		HandlerList.unregisterAll(this);
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onClick(PlayerInteractEvent event) {
		if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
			return;
		}
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();
		if (block == null) {
			return;
		}
		if (join.contains(block)) {
			game.addPlayer(player);
		} else if (block.equals(start)) {
			game.startPlayer(player);
		} else if (block.equals(leave)) {
			game.removePlayer(player);
		} else {
			PlayerClass c = classes.get(block);
			if (c != null) {
				game.setClass(player, c);
			}
		}
	}

}
