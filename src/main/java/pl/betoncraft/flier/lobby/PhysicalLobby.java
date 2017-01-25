/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.lobby;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import net.md_5.bungee.api.ChatColor;
import pl.betoncraft.flier.Flier;
import pl.betoncraft.flier.api.Game;
import pl.betoncraft.flier.api.InGamePlayer;
import pl.betoncraft.flier.api.Lobby;
import pl.betoncraft.flier.api.PlayerClass;
import pl.betoncraft.flier.api.PlayerClass.RespawnAction;
import pl.betoncraft.flier.core.DefaultClass;
import pl.betoncraft.flier.core.DefaultPlayer;
import pl.betoncraft.flier.core.DefaultSet;
import pl.betoncraft.flier.exception.LoadingException;
import pl.betoncraft.flier.util.Utils;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * Physical lobby with fixed classes selected by clicking on blocks.
 *
 * @author Jakub Sapalski
 */
public class PhysicalLobby implements Lobby, Listener {
	
	private PlayerClass defClass;
	private RespawnAction respawnAction = RespawnAction.RESET;
	private Map<UUID, InGamePlayer> players = new HashMap<>();

	private Map<String, Game> games = new HashMap<>();
	private Game currentGame;

	private List<Block> join = new ArrayList<>();
	private Location spawn;
	private Block start;
	private Block leave;
	private Map<Block, ItemBlock> blocks = new HashMap<>();
	private Map<InGamePlayer, List<ItemBlock>> unlocked = new HashMap<>();

	private List<UUID> blocked = new LinkedList<>();

	public PhysicalLobby(ConfigurationSection section) throws LoadingException {
		respawnAction = ValueLoader.loadEnum(section, "respawn_action", RespawnAction.class);
		spawn = ValueLoader.loadLocation(section, "spawn");
		for (String loc : section.getStringList("join")) {
			join.add(Utils.parseLocation(loc).getBlock());
		}
		start = ValueLoader.loadLocation(section, "start").getBlock();
		leave = ValueLoader.loadLocation(section, "leave").getBlock();
		try {
			ConfigurationSection playerClass = section.getConfigurationSection("default_class");
			if (playerClass == null) {
				throw new LoadingException("Player class is not defined.");
			}
			defClass = new DefaultClass(playerClass);
		} catch (LoadingException e) {
			throw (LoadingException) new LoadingException("Error in player class.").initCause(e);
		}
		ConfigurationSection itemsSection = section.getConfigurationSection("items");
		if (itemsSection != null) for (String i : itemsSection.getKeys(false)) {
			ConfigurationSection itemSection = itemsSection.getConfigurationSection(i);
			try {
				blocks.put(ValueLoader.loadLocation(itemSection, "block").getBlock(), new ItemBlock(itemSection));
			} catch (LoadingException e) {
				throw (LoadingException) new LoadingException(String.format("Error in '%s' item set.", i)).initCause(e);
			}
		}
		List<String> gameNames = section.getStringList("games");
		for (String gameName : gameNames) {
			try {
				Game game = Flier.getInstance().getGame(gameName); // TODO game starting/stopping
				games.put(gameName, game);
			} catch (LoadingException e) {
				throw (LoadingException) new LoadingException(String.format("Error in '%s' game.", gameName))
						.initCause(e);
			}
		}
		if (games.isEmpty()) {
			throw new LoadingException("Game list is empty.");
		}
		currentGame = games.get(gameNames.get(0));
		Bukkit.getPluginManager().registerEvents(this, Flier.getInstance());
	}

	private class ItemBlock extends DefaultSet {

		private int buyCost = 0;
		private int unlockCost = 0;

		private ItemBlock(ConfigurationSection section) throws LoadingException {
			super(section);
			buyCost = section.getInt("buy_cost", buyCost);
			unlockCost = section.getInt("unlock_cost", unlockCost);
		}

		private int getBuyCost() {
			return buyCost;
		}

		private int getUnlockCost() {
			return unlockCost;
		}

	}

	@Override
	public void addPlayer(Player player) {
		UUID uuid = player.getUniqueId();
		if (players.containsKey(uuid)) {
			return;
		}
		InGamePlayer data = new DefaultPlayer(player, this, (DefaultClass) defClass.replicate());
		players.put(uuid, data);
		player.teleport(spawn);
		currentGame.addPlayer(data);
	}

	@Override
	public void removePlayer(Player player) {
		UUID uuid = player.getUniqueId();
		InGamePlayer data = players.remove(uuid);
		if (data != null) {
			data.exitLobby();
		}
	}
	
	@Override
	public void respawnPlayer(InGamePlayer player) {
		PlayerClass clazz = player.getClazz();
		switch (respawnAction) {
		case LOAD:
			clazz.load();
			break;
		case SAVE:
			clazz.save();
			clazz.load();
			break;
		case RESET:
			clazz.reset();
			break;
		case NOTHING:
			break;
		}
		player.updateClass();
		player.getPlayer().setVelocity(new Vector());
		if (player.getPlayer().getLocation().distanceSquared(spawn) > 1) {
			player.getPlayer().teleport(spawn);
		}
	}

	@Override
	public void stop() {
		for (Player player : players.values().stream().map(data -> data.getPlayer()).collect(Collectors.toList())) {
			removePlayer(player);
		}
		// no need to stop the game, it's not running without players
		HandlerList.unregisterAll(this);
	}

	@Override
	public void setGame(Game game) {
		currentGame.stop();
		currentGame = game;
		for (InGamePlayer player : players.values()) {
			currentGame.addPlayer(player);
		}
		// no need to start the game, it's running if there were players
	}

	@Override
	public Game getGame() {
		return currentGame;
	}
	
	@Override
	public Map<String, Game> getGames() {
		return Collections.unmodifiableMap(games);
	}
	
	@Override
	public Location getSpawn() {
		return spawn;
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
				if (block.equals(start)) {
					currentGame.startPlayer(data);
				} else {
					handleItems(data, block);
				}
			}
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		InGamePlayer player = players.remove(event.getPlayer().getUniqueId());
		if (player != null) {
			player.exitLobby();
		}
	}

	private void handleItems(InGamePlayer player, Block block) {
		PlayerClass c = player.getClazz();
		ItemBlock b = blocks.get(block);
		if (b != null) {
			List<ItemBlock> ul = unlocked.get(player);
			if (ul == null) {
				ul = new LinkedList<>();
				unlocked.put(player, ul);
			}
			if (!ul.contains(b)) {
				if (b.getUnlockCost() <= player.getMoney()) {
					ul.add(b);
					player.setMoney(player.getMoney() - b.getUnlockCost());
					if (b.getUnlockCost() != 0) {
						player.getPlayer().sendMessage(ChatColor.GREEN + "Unlocked!");
					}
				} else {
					player.getPlayer().sendMessage(ChatColor.RED + "Not enough money to unlock.");
					return;
				}
			}
			if (b.getBuyCost() <= player.getMoney()) {
				if (b.apply(c)) {
					player.setMoney(player.getMoney() - b.getBuyCost());
					player.updateClass();
					player.getPlayer().sendMessage(ChatColor.GREEN + "Class updated!");
				} else {
					player.getPlayer().sendMessage(ChatColor.RED + "You can't use this right now.");
				}
			} else {
				player.getPlayer().sendMessage(ChatColor.RED + "Not enough money.");
			}
		}
	}

}
