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

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.api.content.Lobby;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.PlayerClass;
import pl.betoncraft.flier.api.core.PlayerClass.RespawnAction;
import pl.betoncraft.flier.core.DefaultClass;
import pl.betoncraft.flier.core.DefaultPlayer;
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

	public DefaultLobby(ConfigurationSection section) throws LoadingException {
		loader = new ValueLoader(section);
		spawn = loader.loadLocation("spawn");
		respawnAction = loader.loadEnum("respawn_action", RespawnAction.class);
		try {
			defClass = new DefaultClass(section.getStringList("default_class"), respawnAction);
		} catch (LoadingException e) {
			throw (LoadingException) new LoadingException("Error in default class.").initCause(e);
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

}
