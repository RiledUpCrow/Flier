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
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.api.content.Lobby;
import pl.betoncraft.flier.api.core.Arena;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.NoArenaException;
import pl.betoncraft.flier.event.FlierGameEndEvent.GameEndCause;
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
	protected boolean open = false;

	protected Map<String, List<Game>> gameLists = new HashMap<>();
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
			try {
				Game game = flier.getGame(gameName, this);
				game.stop(GameEndCause.ABORTED);
			} catch (NoArenaException e) {
				throw new LoadingException(String.format(
						"Game '%s' does not have any viable arena to be played on.", gameName));
			}
			gameLists.put(gameName, new ArrayList<>());
		}
		if (gameLists.isEmpty()) {
			throw new LoadingException("Game list is empty.");
		}
		if (section.contains("autojoin", true)) {
			autoJoinGame = loader.loadString("autojoin");
			if (!gameLists.containsKey(autoJoinGame)) {
				throw new LoadingException(
						String.format("Automatic joining impossible because game '%s' is not on the list.",
								autoJoinGame));
			}
		}
		Bukkit.getPluginManager().registerEvents(this, Flier.getInstance());
		open = true;
	}
	
	@Override
	public String getID() {
		return id;
	}
	
	@Override
	public boolean isOpen() {
		return open;
	}
	
	@Override
	public void setOpen(boolean open) {
		this.open = open;
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
		
		// check if the player is already in some game
		if (gameLists.values().stream().anyMatch(
				set -> set.stream().anyMatch(
						game -> game.getPlayers().containsKey(player.getUniqueId())
				)
		)) {
			return JoinResult.ALREADY_IN_GAME;
		}
		
		// find the correct set of games
		List<Game> games = gameLists.get(gameName);
		if (games == null) {
			return JoinResult.NO_SUCH_GAME;
		}
		
		// search for open games
		Game game = null;
		for (Game g : games) {
			if (!g.isLocked() && (g.getMaxPlayers() == 0 || g.getPlayers().size() < g.getMaxPlayers())) {
				game = g;
				break;
			}
		}

		if (game != null) {
			// if the game exists, join it
			if (!event(player, game)) {
				game.addPlayer(player);
				return JoinResult.GAME_JOINED;
			} else {
				return JoinResult.BLOCKED;
			}
		} else {
			// create a new game if there's room for it
			try {
				int amount = gameLists.values().stream().flatMapToInt(set -> IntStream.of(set.size())).sum();
				if (amount < maxGames || maxGames == 0) {
					try {
						game = Flier.getInstance().getGame(gameName, this);
						if (!event(player, game)) {
							games.add(game);
							Flier.getInstance().getPlayers().put(player.getUniqueId(), game.addPlayer(player));
							return JoinResult.GAME_CREATED;
						} else {
							game.stop(GameEndCause.ABORTED);
							return JoinResult.BLOCKED;
						}
					} catch (NoArenaException e) {
						return JoinResult.GAMES_FULL;
					}
				}
				return JoinResult.GAMES_FULL;
			} catch (LoadingException e) {
				// won't throw, it's checked
				e.printStackTrace();
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
		loop: for (List<Game> set : gameLists.values()) {
			for (Iterator<Game> it = set.iterator(); it.hasNext();) {
				Game game = it.next();
				InGamePlayer data = game.getPlayers().get(player.getUniqueId());
				if (data != null) {
					game.removePlayer(player);
					if (game.getPlayers().isEmpty()) {
						endGame(game, GameEndCause.ABANDONED);
					} else {
						game.getPlayers().values().stream()
								.filter(
										inGame -> inGame.getAttacker() != null &&
										data.equals(inGame.getAttacker().getCreator())
								).forEach(inGame -> inGame.setAttacker(null));
					}
					break loop;
				}
			}
		}
	}
	
	@Override
	public void endGame(Game game, GameEndCause cause) {
		game.stop(cause);
		game.getArena().setUsed(false);
		gameLists.get(game.getID()).remove(game);
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
	public Map<String, List<Game>> getGames() {
		return Collections.unmodifiableMap(gameLists);
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
		// abort all games
		gameLists.values().forEach(list -> {
			List<Game> temp = new ArrayList<>(list);
			temp.forEach(game -> endGame(game, GameEndCause.ABORTED));
		});
		// move players out of the lobby
		for (Player player : players.stream().map(uuid -> Bukkit.getPlayer(uuid)).collect(Collectors.toList())) {
			removePlayer(player);
		}
		// unregister the listener
		HandlerList.unregisterAll(this);
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent event) {
		removePlayer(event.getPlayer());
	}
	
	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		UUID[] uuid = new UUID[2];
		uuid[0] = event.getEntity().getUniqueId();
		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) event;
			uuid[1] = entityEvent.getDamager().getUniqueId();
		}
		for (UUID u : uuid) {
			if (u != null && players.contains(u) &&
					!gameLists.values().stream().anyMatch(
							set -> set.stream().anyMatch(
									game -> game.getPlayers().containsKey(u)
							)
					)) {
				event.setCancelled(true);
				break;
			}
		}
	}
	
	@EventHandler
	public void onItemFrame(PlayerInteractEntityEvent event) {
		if (getPlayers().contains(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onInteract(PlayerInteractEvent event) {
		if (getPlayers().contains(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if (getPlayers().contains(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPickup(PlayerPickupItemEvent event) {
		if (getPlayers().contains(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onSwap(PlayerSwapHandItemsEvent event) {
		if (getPlayers().contains(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if (getPlayers().contains(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onBreak(BlockBreakEvent event) {
		if (getPlayers().contains(event.getPlayer().getUniqueId())) {
			event.setCancelled(true);
		}
	}
	
	@EventHandler
	public void onRegen(EntityRegainHealthEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (getPlayers().contains(player.getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onHunger(FoodLevelChangeEvent event) {
		if (getPlayers().contains(event.getEntity().getUniqueId())) {
			event.setCancelled(true);
		}
	}

}
