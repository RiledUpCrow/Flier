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

import pl.betoncraft.flier.api.content.Lobby;

/**
 * Fired when a player joins the Lobby.
 *
 * @author Jakub Sapalski
 */
public class FlierPlayerJoinLobbyEvent extends Event implements Cancellable {
	
	private Player player;
	private Lobby lobby;
	private boolean cancel = false;

	private static HandlerList handlerList = new HandlerList();
	
	public FlierPlayerJoinLobbyEvent(Player player, Lobby lobby) {
		this.player = player;
		this.lobby = lobby;
	}

	/**
	 * @return the Player who tries to join the Lobby
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return the Lobby to which the player tries to join
	 */
	public Lobby getLobby() {
		return lobby;
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
		return handlerList ;
	}

	public static HandlerList getHandlerList() {
		return handlerList ;
	}

}
