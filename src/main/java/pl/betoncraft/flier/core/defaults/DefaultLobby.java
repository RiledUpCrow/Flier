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
import org.bukkit.scoreboard.Scoreboard;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.api.content.Lobby;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.util.PlayerBackup;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * Default implementation of a Lobby.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultLobby implements Lobby, Listener {
	
	protected ValueLoader loader;

	protected Map<String, Game> games = new HashMap<>();
	protected Game currentGame;
	protected Location spawn;
	protected Set<UUID> players = new HashSet<>();
	protected Map<UUID, PlayerBackup> backups = new HashMap<>();
	protected Map<UUID, Scoreboard> oldScoreboards = new HashMap<>();

	public DefaultLobby(ConfigurationSection section) throws LoadingException {
		loader = new ValueLoader(section);
		spawn = loader.loadLocation("spawn");
		List<String> gameNames = section.getStringList("games");
		for (String gameName : gameNames) {
			Game game = Flier.getInstance().getGame(gameName);
			game.setLobby(this);
			games.put(gameName, game);
		}
		if (games.isEmpty()) {
			throw new LoadingException("Game list is empty.");
		}
		currentGame = games.get(gameNames.get(0));
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
		oldScoreboards.put(uuid, player.getScoreboard());
		player.teleport(spawn);
	}

	@Override
	public void removePlayer(Player player) {
		UUID uuid = player.getUniqueId();
		if (players.remove(uuid)) {
			currentGame.removePlayer(player);
			player.setScoreboard(oldScoreboards.remove(uuid));
			player.getInventory().clear();
			backups.remove(uuid).load();
		}
	}

	@Override
	public Set<UUID> getPlayers() {
		return players;
	}

	@Override
	public void setGame(Game game) {
		currentGame.stop();
		currentGame = game;
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
