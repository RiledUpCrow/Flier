/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core.defaults;

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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

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
import pl.betoncraft.flier.util.ValueLoader;

/**
 * Default implementation of a Lobby.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultLobby implements Lobby, Listener {
	
	protected ValueLoader loader;

	protected RespawnAction respawnAction = RespawnAction.RESET;
	protected Map<String, Game> games = new HashMap<>();
	protected Game currentGame;
	protected Location spawn;
	protected Map<UUID, InGamePlayer> players = new HashMap<>();
	protected PlayerClass defClass;
	protected Map<String, CostlySet> items = new HashMap<>();
	protected Map<InGamePlayer, List<CostlySet>> unlocked = new HashMap<>();

	public DefaultLobby(ConfigurationSection section) throws LoadingException {
		loader = new ValueLoader(section);
		spawn = loader.loadLocation("spawn");
		respawnAction = loader.loadEnum("respawn_action", RespawnAction.class);
		ConfigurationSection itemsSection = section.getConfigurationSection("items");
		if (itemsSection != null) for (String i : itemsSection.getKeys(false)) {
			ConfigurationSection itemSection = itemsSection.getConfigurationSection(i);
			try {
				items.put(i, new CostlySet(itemSection));
			} catch (LoadingException e) {
				throw (LoadingException) new LoadingException(String.format("Error in '%s' item set.", i)).initCause(e);
			}
		}
		try {
			List<String> playerClass = section.getStringList("default_class");
			defClass = new DefaultClass(
					playerClass.stream().map(
							name -> items.entrySet().stream().filter(
									entry -> entry.getKey().equals(name)
							).findFirst().orElse(null).getValue()
					).collect(Collectors.toList())
			);
		} catch (NullPointerException | LoadingException e) {
			throw (LoadingException) new LoadingException("Error in player class.").initCause(e);
		}
		List<String> gameNames = section.getStringList("games");
		for (String gameName : gameNames) {
			try {
				Game game = Flier.getInstance().getGame(gameName);
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
	
	protected class CostlySet extends DefaultSet {
		
		private int buyCost = 0;
		private int unlockCost = 0;

		public CostlySet(ConfigurationSection section) throws LoadingException {
			super(section);
			buyCost = section.getInt("buy_cost", buyCost);
			unlockCost = section.getInt("unlock_cost", unlockCost);
		}

		public int getBuyCost() {
			return buyCost;
		}

		public int getUnlockCost() {
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
		player.getPlayer().teleport(spawn);
	}

	@Override
	public void setGame(Game game) {
		List<InGamePlayer> players = new ArrayList<>(currentGame.getPlayers().values());
		currentGame.stop();
		currentGame = game;
		for (InGamePlayer player : players) {
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
	
	@Override
	public void stop() {
		for (Player player : players.values().stream().map(data -> data.getPlayer()).collect(Collectors.toList())) {
			removePlayer(player);
		}
		// no need to stop the game, it's not running without players
		HandlerList.unregisterAll(this);
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		InGamePlayer player = players.remove(event.getPlayer().getUniqueId());
		if (player != null) {
			player.exitLobby();
		}
	}

	protected void handleItems(InGamePlayer player, CostlySet set) {
		PlayerClass c = player.getClazz();
		if (set != null) {
			List<CostlySet> ul = unlocked.get(player);
			if (ul == null) {
				ul = new LinkedList<>();
				unlocked.put(player, ul);
			}
			if (!ul.contains(set)) {
				if (set.getUnlockCost() <= player.getMoney()) {
					ul.add(set);
					player.setMoney(player.getMoney() - set.getUnlockCost());
					if (set.getUnlockCost() != 0) {
						player.getPlayer().sendMessage(ChatColor.GREEN + "Unlocked!");
					}
				} else {
					player.getPlayer().sendMessage(ChatColor.RED + "Not enough money to unlock.");
					return;
				}
			}
			if (set.getBuyCost() <= player.getMoney()) {
				if (set.apply(c)) {
					player.setMoney(player.getMoney() - set.getBuyCost());
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
