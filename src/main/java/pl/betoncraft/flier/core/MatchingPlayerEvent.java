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
package pl.betoncraft.flier.core;

import pl.betoncraft.flier.api.content.Engine;
import pl.betoncraft.flier.api.content.Wings;
import pl.betoncraft.flier.api.core.InGamePlayer;

/**
 * Represents a MatchingEvent with a player involved.
 *
 * @author Jakub Sapalski
 */
public class MatchingPlayerEvent extends MatchingEvent {

	protected static final String WINGS_HEALTH_RATIO = "wings_health_ratio";
	protected static final String WINGS_HEALTH = "wings_health";
	protected static final String WINGS = "wings";
	protected static final String FUEL_RATIO = "fuel_ratio";
	protected static final String FUEL = "fuel";
	protected static final String ENGINE = "engine";
	protected static final String MONEY = "money";
	protected static final String COLOR = "color";
	protected static final String CLASS = "class";

	protected InGamePlayer player;

	/**
	 * Creates new MatchingEvent with the specified player. The player can't
	 * be null. For events without any particular player use the other constructor.
	 * 
	 * @param player
	 *            the player involved in this event
	 */
	public MatchingPlayerEvent(InGamePlayer player) {
		super(player.getGame());
		this.player = player;
		parsePlayer(player, "");
	}
	
	protected void parsePlayer(InGamePlayer player, String prefix) {
		String className = player.getKit().getClassName();
		setString(prefix + CLASS, className == null ? "" : className);
		setString(prefix + COLOR, player.getColor().name());
		setNumber(prefix + MONEY, (double) player.getMoney());
		Engine engine = player.getKit().getEngine();
		setString(prefix + ENGINE, engine.getID());
		setNumber(prefix + FUEL, engine.getFuel());
		setNumber(prefix + FUEL_RATIO, engine.getFuel() / engine.getMaxFuel());
		Wings wings = player.getKit().getWings();
		setString(prefix + WINGS, wings.getID());
		setNumber(prefix + WINGS_HEALTH, wings.getHealth());
		setNumber(prefix + WINGS_HEALTH_RATIO, wings.getHealth() / wings.getMaxHealth());
	}

	/**
	 * @return the player involved in this event or null if there was no
	 *         particular player
	 */
	public InGamePlayer getPlayer() {
		return player;
	}

}
