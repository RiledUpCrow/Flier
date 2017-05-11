/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.event;

import org.bukkit.event.Cancellable;

import pl.betoncraft.flier.api.content.Game.Button;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.core.MatchingPlayerEvent;

/**
 * Called when the player clicks a Button in Game.
 *
 * @author Jakub Sapalski
 */
public class FlierClickButtonEvent extends MatchingPlayerEvent implements Cancellable {
	
	private static final String BUTTON = "button";
	
	public Button button;
	public boolean cancel = false;

	public FlierClickButtonEvent(InGamePlayer player, Button button) {
		super(player);
		this.button = button;
		setString(BUTTON, button.getID());
	}
	
	public Button getButton() {
		return button;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

}
