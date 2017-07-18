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
package pl.betoncraft.flier.game;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.util.LangManager;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * Represents the waiting room which can hold players currently not in-game
 * and spawn them into the game.
 */
class WaitingRoom {
	
	/**
	 * Reason for making the player wait in the waiting room.
	 */
	protected enum WaitReason {
		NO_WAIT, MORE_PLAYERS, START_DELAY, RESPAWN_DELAY, ROUND, GAME_ENDS
	}

	private static final String WAITING_ROOM = "waiting_room";
	private static final String LOCKING = "locking";
	private static final String START_DELAY = "start_delay";
	private static final String RESPAWN_DELAY = "respawn_delay";
	private static final String MIN_PLAYERS = "min_players";

	protected final List<Integer> displayTimes = Arrays.asList(
			1, 2, 3, 4, 5, 10, 15, 30, 60, 90, 120, 180, 240, 300, 600).stream()
			.map(i -> i * 20).collect(Collectors.toList());
	
	protected final DefaultGame game;
	protected final int minPlayers;
	protected final int respawnDelay;
	protected final int startDelay;
	protected final boolean locking;
	protected final Location location;

	protected Set<InGamePlayer> waitingPlayers = new HashSet<>();
	protected WaitReason reason = WaitReason.NO_WAIT;
	protected int currentWaitingTime;
	protected BukkitRunnable ticker;
	protected boolean locked = false;
	protected boolean roundFinished = false;
	
	public WaitingRoom(DefaultGame game, ValueLoader loader) throws LoadingException {
		this.game = game;
		minPlayers = loader.loadPositiveInt(MIN_PLAYERS, 1);
		respawnDelay = loader.loadNonNegativeInt(RESPAWN_DELAY, 0);
		startDelay = loader.loadNonNegativeInt(START_DELAY, 0);
		locking = loader.loadBoolean(LOCKING, false);
		location = game.getArena().getLocationSet(loader.loadString(WAITING_ROOM)).getSingle();
		ticker = new BukkitRunnable() {
			public void run() {
				tick();
			};
		};
		ticker.runTaskTimer(Flier.getInstance(), 1, 1);
		currentWaitingTime = -1; // lower than 0 means the waiting room is idle
	}
	
	public void finishRound() {
		roundFinished = true;
	}
	
	/**
	 * @return whenever the waiting room is locked for new players
	 */
	public boolean isLocked() {
		return locked;
	}
	
	/**
	 * Adds this player to the waiting room.
	 * 
	 * @param player the player to add
	 * @return the type of waiting the player experiences
	 */
	public WaitReason addPlayer(InGamePlayer player) {
		if (waitingPlayers.contains(player)) {
			throw new IllegalStateException("Cannot add player to the waiting room twice!");
		}
		waitingPlayers.add(player);
		// if the game has ended just move the player
		if (reason == WaitReason.GAME_ENDS) {
			player.getPlayer().teleport(location);
			return WaitReason.GAME_ENDS;
		}
		if (!game.isRunning()) {
			// the game hasn't started yet
			if (waitingPlayers.size() >= minPlayers) {
				// minimum player amount has been reached
				if (startDelay == 0) {
					// no start delay, start immediately
					reason = WaitReason.NO_WAIT;
				} else {
					// start delay should be applied
					if (currentWaitingTime <= 0) {
						// create start delay
						currentWaitingTime = startDelay;
					}
					reason = WaitReason.START_DELAY;
				} 
			} else {
				// not enough players yet
				reason = WaitReason.MORE_PLAYERS;
			}
		} else {
			// the game has already started
			if (game.hasRounds() && !roundFinished) {
				// the round still in progress
				reason = WaitReason.ROUND;
				currentWaitingTime = respawnDelay;
			} else if (respawnDelay == 0) {
				// no respawn delay, start immediately
				reason = WaitReason.NO_WAIT; // players teleported, no need to put them in the waiting room
			} else {
				if (currentWaitingTime <= 0) {
					// create respawn delay
					currentWaitingTime = respawnDelay;
				}
				reason = WaitReason.RESPAWN_DELAY;
			}
		}
		// after the player has been added to the list and waiting time was set
		// we need to teleport them to the waiting room or immediately to the game
		if (reason == WaitReason.NO_WAIT) {
			startPlayers();
		} else {
			player.getPlayer().teleport(location);
		}
		return reason;
	}
	
	/**
	 * Removes the player from the waiting room. It does not teleport him in
	 * any way.
	 * 
	 * @param player the player to remove
	 */
	public void removePlayer(InGamePlayer player) {
		// it's assumed the teleporting of the player was already done
		waitingPlayers.remove(player);
	}
	
	/**
	 * Starts the game for all waiting players.
	 */
	public void startPlayers() {
		if (!game.isRunning()) {
			// start the game in case it's not running
			game.start();
		}
		// run the regular respawn routine
		for (InGamePlayer player : game.getPlayersForRespawn(new HashSet<>(waitingPlayers))) {
			game.handleRespawn(player);
			waitingPlayers.remove(player);
		}
		roundFinished = false;
	}
	
	/**
	 * Called every tick so waiting room can decrease the counters.
	 */
	public void tick() {
		boolean shouldCountdown = reason != WaitReason.GAME_ENDS
				&& (!game.isRunning() || !game.hasRounds() || roundFinished);
		if (shouldCountdown) {
			// start the game if the waiting time is exactly 0
			// lower means the game was already started and the waiting room is idle
			if (currentWaitingTime == 0) {
				startPlayers();
			} else {
				// display countdown on specific seconds
				if (displayTimes.contains(currentWaitingTime)) {
					if (reason == WaitReason.RESPAWN_DELAY ||
							reason == WaitReason.START_DELAY ||
							reason == WaitReason.ROUND) {
						waitingPlayers.forEach(
								data -> LangManager.sendMessage(data, "countdown",
										(double) currentWaitingTime / 20.0));
					}
				}
			}
			currentWaitingTime--;
		}
	}
	
}