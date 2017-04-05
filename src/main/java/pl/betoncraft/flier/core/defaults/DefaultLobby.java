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
import java.util.stream.IntStream;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.api.content.Lobby;
import pl.betoncraft.flier.api.core.Arena;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.event.FlierPlayerJoinGameEvent;
import pl.betoncraft.flier.event.FlierPlayerJoinLobbyEvent;
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
	protected String id;

	protected Map<String, Set<Game>> gameSets = new HashMap<>();
	protected Map<String, Arena> arenas = new HashMap<>();
	protected Location spawn;
	protected Set<UUID> players = new HashSet<>();
	protected Map<UUID, PlayerBackup> backups = new HashMap<>();
	protected int maxGames;
	
	protected String autoJoinGame;

	public DefaultLobby(ConfigurationSection section) throws LoadingException {
		id = section.getName();
		loader = new ValueLoader(section);
		spawn = loader.loadLocation("spawn");
		maxGames = loader.loadNonNegativeInt("max_games", 0);
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
		if (section.contains("autojoin", true)) {
			autoJoinGame = loader.loadString("autojoin");
			if (!gameSets.containsKey(autoJoinGame)) {
				throw new LoadingException(
						String.format("Automatic joining impossible because game '%s' is not on the list.", autoJoinGame));
			}
		}
		Bukkit.getPluginManager().registerEvents(this, Flier.getInstance());
	}
	
	@Override
	public String getID() {
		return id;
	}

	@Override
	public void addPlayer(Player player) {
		// can't join if already inside
		UUID uuid = player.getUniqueId();
		if (players.contains(uuid)) {
			return;
		}
		// call the event and stop joining if it was cancelled
		FlierPlayerJoinLobbyEvent event = new FlierPlayerJoinLobbyEvent(player, this);
		Bukkit.getPluginManager().callEvent(event);
		if (event.isCancelled()) {
			return;
		}
		// join the lobby
		players.add(uuid);
		PlayerBackup backup = new PlayerBackup(player);
		backup.save();
		backups.put(uuid, backup);
		if (autoJoinGame != null) {
			JoinResult result = joinGame(player, autoJoinGame);
			joinMessage(player, result);
			if (result == JoinResult.GAME_CREATED || result == JoinResult.GAME_JOINED) {
				return;
			}
		}
		player.teleport(spawn);
	}

	@Override
	public void removePlayer(Player player) {
		UUID uuid = player.getUniqueId();
		if (players.remove(uuid)) {
			leaveGame(player);
			backups.remove(uuid).load();
		}
	}
	
	@Override
	public JoinResult joinGame(Player player, String gameName) {
		if (gameSets.values().stream().anyMatch(
				set -> set.stream().anyMatch(
						game -> game.getPlayers().containsKey(player.getUniqueId())
				)
		)) {
			return JoinResult.ALREADY_IN_GAME;
		}
		Set<Game> games = gameSets.get(gameName);
		if (games == null) {
			return JoinResult.NO_SUCH_GAME;
		}
		Game game = null;
		for (Game g : games) {
			if (!g.isLocked() && (g.getMaxPlayers() == 0 || g.getPlayers().size() < g.getMaxPlayers())) {
				game = g;
				break;
			}
		}
		if (game != null) {
			if (!event(player, game)) {
				Flier.getInstance().getPlayers().put(player.getUniqueId(), game.addPlayer(player));
				return JoinResult.GAME_JOINED;
			} else {
				return JoinResult.BLOCKED;
			}
		} else {
			try {
				int amount = gameSets.values().stream().flatMapToInt(set -> IntStream.of(set.size())).sum();
				if (amount < maxGames || maxGames == 0) {
					game = Flier.getInstance().getGame(gameName);
					List<String> viable = game.getViableArenas();
					for (String arenaName : viable) {
						Arena arena = arenas.get(arenaName);
						if (!arena.isUsed()) {
							if (!event(player, game)) {
								arena.setUsed(true);
								games.add(game);
								game.setLobby(this);
								game.setArena(arena);
								Flier.getInstance().getPlayers().put(player.getUniqueId(), game.addPlayer(player));
								return JoinResult.GAME_CREATED;
							} else {
								return JoinResult.BLOCKED;
							}
						}
					}
				}
				return JoinResult.GAMES_FULL;
			} catch (LoadingException e) {
				// won't throw, it's checked
				return null;
			}
		}
	}
	
	private boolean event(Player player, Game game) {
		FlierPlayerJoinGameEvent event = new FlierPlayerJoinGameEvent(player, game);
		Bukkit.getPluginManager().callEvent(event);
		return event.isCancelled();
	}
	
	@Override
	public void leaveGame(Player player) {
		loop: for (Set<Game> set : gameSets.values()) {
			for (Iterator<Game> it = set.iterator(); it.hasNext();) {
				Game game = it.next();
				if (game.getPlayers().containsKey(player.getUniqueId())) {
					game.removePlayer(player);
					Flier.getInstance().getPlayers().remove(player.getUniqueId());
					if (game.getPlayers().isEmpty()) {
						endGame(game);
					}
					break loop;
				}
			}
		}
	}
	
	@Override
	public void endGame(Game game) {
		game.stop();
		game.getArena().setUsed(false);
		gameSets.get(game.getID()).remove(game);
	}
	
	public static void joinMessage(Player player, JoinResult result) {
		switch (result) {
		case ALREADY_IN_GAME:
			LangManager.sendMessage(player, "already_in_game");
			break;
		case GAME_CREATED:
			LangManager.sendMessage(player, "game_created");
			break;
		case GAME_JOINED:
			LangManager.sendMessage(player, "game_joined");
			break;
		case GAMES_FULL:
			LangManager.sendMessage(player, "games_full");
			break;
		case NO_SUCH_GAME:
			LangManager.sendMessage(player, "no_such_game");
			break;
		case BLOCKED:
			// no message
			break;
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
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		UUID uuid = event.getEntity().getUniqueId();
		if (players.contains(uuid) &&
				!gameSets.values().stream().anyMatch(
						set -> set.stream().anyMatch(
								game -> game.getPlayers().containsKey(uuid)
						)
				)) {
			event.setCancelled(true);
		}
	}

}
