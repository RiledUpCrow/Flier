/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier;

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
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import pl.betoncraft.flier.api.PlayerClass;
import pl.betoncraft.flier.api.Team;

/**
 * Simple lobby with team/class choosing and game starting.
 *
 * @author Jakub Sapalski
 */
public class SimpleLobby implements Listener {
	
	private SimpleGame game;
	private List<Block> join;
	private Location spawn;
	private Block start;
	private Block leave;
	private Map<Block, Team> team = new HashMap<>();
	private Map<Block, PlayerClass> clazz = new HashMap<>();

	public SimpleLobby(ConfigurationSection section, SimpleGame game) {
		this.game = game;
		spawn = Utils.parseLocation(section.getString("spawn"));
		join = section.getStringList("join").stream().map(e -> Utils.parseLocation(e).getBlock()).collect(Collectors.toList());
		start = Utils.parseLocation(section.getString("start")).getBlock();
		leave = Utils.parseLocation(section.getString("leave")).getBlock();
		Flier f = Flier.getInstance();
		ConfigurationSection tSection = section.getConfigurationSection("choice.team");
		for (String t : tSection.getKeys(false)) {
			team.put(Utils.parseLocation(tSection.getString(t)).getBlock(), game.getTeam(t));
		}
		ConfigurationSection cSection = section.getConfigurationSection("choice.class");
		for (String c : cSection.getKeys(false)) {
			clazz.put(Utils.parseLocation(cSection.getString(c)).getBlock(), f.getClass(c));
		}
		Bukkit.getPluginManager().registerEvents(this, Flier.getInstance());
	}

	public Location getSpawn() {
		return spawn;
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
			Team t = team.get(block);
			PlayerClass c = clazz.get(block);
			if (t != null) {
				game.setTeam(player, t);
			} else if (c != null) {
				game.setClass(player, c);
			}
		}
	}

}
