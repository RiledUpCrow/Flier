/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.event;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import pl.betoncraft.flier.api.content.Game;

/**
 * Called when the player joins a game.
 *
 * @author Jakub Sapalski
 */
public class FlierPlayerJoinGameEvent extends Event implements Cancellable {
	
	private Player player;
	private Game game;
	private boolean cancel = false;
	
	private static HandlerList handlerList = new HandlerList();
	
	public FlierPlayerJoinGameEvent(Player player, Game game) {
		this.player = player;
		this.game = game;
	}

	/**
	 * @return the Player who joined the Game
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return the Game joined by the Player
	 */
	public Game getGame() {
		return game;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}

	public static HandlerList getHandlerList() {
		return handlerList ;
	}

}
