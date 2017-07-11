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
