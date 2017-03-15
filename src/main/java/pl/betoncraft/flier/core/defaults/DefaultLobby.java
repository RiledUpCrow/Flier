/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core.defaults;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.api.content.Lobby;
import pl.betoncraft.flier.api.core.Arena;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.util.LangManager;
import pl.betoncraft.flier.util.PlayerBackup;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * Default implementation of a Lobby.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultLobby implements Lobby, Listener {
	
	protected ValueLoader loader;

	protected Map<String, Set<Game>> gameSets = new HashMap<>();
	protected Map<String, Arena> arenas = new HashMap<>();
	protected Location spawn;
	protected Set<UUID> players = new HashSet<>();
	protected Map<UUID, PlayerBackup> backups = new HashMap<>();

	public DefaultLobby(ConfigurationSection section) throws LoadingException {
		loader = new ValueLoader(section);
		spawn = loader.loadLocation("spawn");
		List<String> gameNames = section.getStringList("games");
		Flier flier = Flier.getInstance();
		for (String arenaName : section.getStringList("arenas")) {
			arenas.put(arenaName, flier.getArena(arenaName));
		}
		for (String gameName : gameNames) {
			Game game = flier.getGame(gameName);
			for (String arenaName : game.getViableArenas()) {
				Arena arena = arenas.get(arenaName);
				if (arena == null) {
					throw new LoadingException(String.format("Game '%s' refers to non-existing arena '%s'.", gameName, arenaName));
				}
				game.setArena(arena);
			}
			gameSets.put(gameName, new HashSet<>());
		}
		if (gameSets.isEmpty()) {
			throw new LoadingException("Game list is empty.");
		}
		Bukkit.getPluginManager().registerEvents(this, Flier.getInstance());
	}

	@Override
	public void addPlayer(Player player) {
		UUID uuid = player.getUniqueId();
		if (players.contains(uuid)) {
			return;
		}
		players.add(uuid);
		PlayerBackup backup = new PlayerBackup(player);
		backup.save();
		backups.put(uuid, backup);
		player.teleport(spawn);
	}

	@Override
	public void removePlayer(Player player) {
		UUID uuid = player.getUniqueId();
		if (players.remove(uuid)) {
			loop: for (Set<Game> set : gameSets.values()) {
				for (Iterator<Game> it = set.iterator(); it.hasNext();) {
					Game game = it.next();
					if (game.getPlayers().containsKey(uuid)) {
						game.removePlayer(player);
						if (!game.isRunning()) {
							game.getArena().setUsed(false);
							it.remove();
						}
						break loop;
					}
				}
			}
			backups.remove(uuid).load();
		}
	}
	
	@Override
	public JoinResult joinGame(Player player, String gameName) {
		Set<Game> games = gameSets.get(gameName);
		if (games == null) {
			LangManager.sendMessage(player, "no_such_game");
			return JoinResult.NO_SUCH_GAME;
		}
		Game game = null;
		for (Game g : games) {
			if (g.getMaxPlayers() == 0 || g.getPlayers().size() < g.getMaxPlayers()) {
				game = g;
				break;
			}
		}
		if (game != null) {
			InGamePlayer data = game.addPlayer(player);
			LangManager.sendMessage(data, "game_joined");
			return JoinResult.GAME_JOINED;
		} else {
			try {
				game = Flier.getInstance().getGame(gameName);
				List<String> viable = game.getViableArenas();
				for (String arenaName : viable) {
					Arena arena = arenas.get(arenaName);
					if (!arena.isUsed()) {
						arena.setUsed(true);
						games.add(game);
						game.setLobby(this);
						game.setArena(arena);
						InGamePlayer data = game.addPlayer(player);
						LangManager.sendMessage(data, "game_created");
						return JoinResult.GAME_CREATED;
					}
				}
				LangManager.sendMessage(player, "games_full");
				return JoinResult.GAMES_FULL;
			} catch (LoadingException e) {
				// won't throw, it's checked
				return null;
			}
		}
	}

	@Override
	public Set<UUID> getPlayers() {
		return players;
	}
	
	@Override
	public Map<String, Set<Game>> getGames() {
		return Collections.unmodifiableMap(gameSets);
	}
	
	@Override
	public Map<String, Arena> getArenas() {
		return Collections.unmodifiableMap(arenas);
	}
	
	@Override
	public Location getSpawn() {
		return spawn;
	}
	
	@Override
	public void stop() {
		for (Player player : players.stream().map(uuid -> Bukkit.getPlayer(uuid)).collect(Collectors.toList())) {
			removePlayer(player);
		}
		// no need to stop the game, it's not running without players
		HandlerList.unregisterAll(this);
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		removePlayer(event.getPlayer());
	}

}
