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
import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.api.content.Lobby;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.PlayerClass;
import pl.betoncraft.flier.api.core.SetApplier;
import pl.betoncraft.flier.api.core.PlayerClass.AddResult;
import pl.betoncraft.flier.api.core.PlayerClass.RespawnAction;
import pl.betoncraft.flier.core.DefaultClass;
import pl.betoncraft.flier.core.DefaultPlayer;
import pl.betoncraft.flier.core.DefaultSetApplier;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * Default implementation of a Lobby.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultLobby implements Lobby, Listener {
	
	protected ValueLoader loader;

	protected RespawnAction respawnAction;
	protected Map<String, Game> games = new HashMap<>();
	protected Game currentGame;
	protected Location spawn;
	protected Map<UUID, InGamePlayer> players = new HashMap<>();
	protected PlayerClass defClass;
	protected Map<String, ConfigurationSection> items = new HashMap<>();
	protected Map<String, Button> buttons = new HashMap<>();
	protected Map<InGamePlayer, List<Button>> unlocked = new HashMap<>();

	public DefaultLobby(ConfigurationSection section) throws LoadingException {
		loader = new ValueLoader(section);
		spawn = loader.loadLocation("spawn");
		respawnAction = loader.loadEnum("respawn_action", RespawnAction.class);
		ConfigurationSection itemsSection = section.getConfigurationSection("items");
		if (itemsSection != null) for (String i : itemsSection.getKeys(false)) {
			ConfigurationSection itemSection = itemsSection.getConfigurationSection(i);
			if (itemSection == null) {
				throw new LoadingException(String.format("'%s' is not an item set.", i));
			}
			items.put(i, itemSection);
		}
		try {
			List<String> playerClass = section.getStringList("default_class");
			List<ConfigurationSection> sets = new ArrayList<>();
			for (String set : playerClass) {
				ConfigurationSection sec = items.get(set);
				if (sec == null) {
					throw new LoadingException(String.format("Item set '%s' is not defined.", set));
				}
				sets.add(sec);
			}
			defClass = new DefaultClass(sets, respawnAction);
		} catch (LoadingException e) {
			throw (LoadingException) new LoadingException("Error in default class.").initCause(e);
		}
		ConfigurationSection buttonsSection = section.getConfigurationSection("buttons");
		if (buttonsSection != null) for (String i : buttonsSection.getKeys(false)) {
			ConfigurationSection buttonSection = buttonsSection.getConfigurationSection(i);
			if (buttonSection == null) {
				throw new LoadingException(String.format("'%s' is not a button.", i));
			}
			try {
				buttons.put(i, new Button(buttonSection));
			} catch (LoadingException e) {
				throw (LoadingException) new LoadingException(String.format("Error in '%s' button.", i)).initCause(e);
			}
		}
		List<String> gameNames = section.getStringList("games");
		for (String gameName : gameNames) {
			Game game = Flier.getInstance().getGame(gameName);
			games.put(gameName, game);
		}
		if (games.isEmpty()) {
			throw new LoadingException("Game list is empty.");
		}
		currentGame = games.get(gameNames.get(0));
		Bukkit.getPluginManager().registerEvents(this, Flier.getInstance());
	}
	
	protected class Button {
		
		protected final int buyCost;
		protected final int sellCost;
		protected final int unlockCost;
		protected final SetApplier onBuy;
		protected final SetApplier onSell;
		protected final SetApplier onUnlock;

		public Button(ConfigurationSection section) throws LoadingException {
			ValueLoader loader = new ValueLoader(section);
			buyCost = loader.loadInt("buy_cost", 0);
			sellCost = loader.loadInt("sell_cost", 0);
			unlockCost = loader.loadNonNegativeInt("unlock_cost", 0);
			try {
				ConfigurationSection buySection = section.getConfigurationSection("on_buy");
				onBuy = buySection == null ? null : new DefaultSetApplier(buySection, items);
			} catch (LoadingException e) {
				throw (LoadingException) new LoadingException("Error in 'on_buy' section.").initCause(e);
			}
			try {
				ConfigurationSection sellSection = section.getConfigurationSection("on_sell");
				onSell = sellSection == null ? null : new DefaultSetApplier(sellSection, items);
			} catch (LoadingException e) {
				throw (LoadingException) new LoadingException("Error in 'on_sell' section.").initCause(e);
			}
			try {
				ConfigurationSection unlockSection = section.getConfigurationSection("on_unlock");
				onUnlock = unlockSection == null ? null : new DefaultSetApplier(unlockSection, items);
			} catch (LoadingException e) {
				throw (LoadingException) new LoadingException("Error in 'on_unlock' section.").initCause(e);
			}
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
		clazz.onRespawn();
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

	protected void handleItems(InGamePlayer player, Button button, boolean buy) {
		if (button != null) {
			List<Button> ul = unlocked.computeIfAbsent(player, k -> new LinkedList<>());
			boolean unlocked = button.unlockCost == 0 || ul.contains(button);
			if (!unlocked) {
				SetApplier applier = button.onUnlock;
				if (button.unlockCost <= player.getMoney()) {
					Runnable run = () -> {
						ul.add(button);
						player.setMoney(player.getMoney() - button.unlockCost);
						player.updateClass();
					};
					String message;
					if (applier == null) {
						run.run();
						message = ChatColor.GREEN + "Unlocked!";
					} else {
						AddResult result = applier.isSaving() ? player.getClazz().addStored(applier) :
							player.getClazz().addCurrent(applier);
						switch (result) {
						case ADDED:
						case FILLED:
						case REPLACED:
						case REMOVED:
							run.run();
							message = ChatColor.GREEN + "Unlocked!";
							break;
						default:
							message = ChatColor.RED + "You can't use this right now.";
						}
						player.getPlayer().sendMessage(message);
					}
				} else {
					player.getPlayer().sendMessage(ChatColor.RED + "Not enough money to unlock this.");
				}
			} else {
				int cost;
				SetApplier applier;
				if (buy) {
					cost = button.buyCost;
					applier = button.onBuy;
				} else {
					cost = button.sellCost;
					applier = button.onSell;
				}
				if (applier != null) {
					if (cost <= player.getMoney()) {
						AddResult result = applier.isSaving() ? player.getClazz().addStored(applier) :
							player.getClazz().addCurrent(applier);
						Runnable run = () -> {
							player.setMoney(player.getMoney() - cost);
							player.updateClass();
						};
						String message = null;
						switch (result) {
						case ADDED:
							run.run();
							message = ChatColor.GREEN + "Items added!";
							break;
						case FILLED:
							run.run();
							message = ChatColor.GREEN + "Items refilled!";
							break;
						case REMOVED:
							run.run();
							message = ChatColor.GREEN + "Items removed!";
							break;
						case REPLACED:
							run.run();
							message = ChatColor.GREEN + "Items replaced!";
							break;
						case ALREADY_EMPTIED:
							// no running, items were not added
							message = ChatColor.RED + "You can't sell more of these items!";
							break;
						case ALREADY_MAXED:
							// no running, items were not added
							message = ChatColor.RED + "You have reached a limit!";
							break;
						case SKIPPED:
							// no running, items were not added
							message = ChatColor.RED + "You already have another item in this category!";
							break;
						}
						player.getPlayer().sendMessage(message);
					} else {
						player.getPlayer().sendMessage(ChatColor.RED + "Not enough money to buy this.");
					}
				} else {
					player.getPlayer().sendMessage(ChatColor.RED + "You can't do this.");
				}
			}
		}
	}

}
