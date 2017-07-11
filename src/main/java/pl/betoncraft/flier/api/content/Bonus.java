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
package pl.betoncraft.flier.api.content;

import java.util.List;

import pl.betoncraft.flier.api.core.InGamePlayer;

/**
 * Represents a collectable Entity on the Game map.
 *
 * @author Jakub Sapalski
 */
public interface Bonus {

	/**
	 * @return the ID of this Bonus
	 */
	public String getID();

	/**
	 * @return true if the Bonus should be removed after using
	 */
	public boolean isConsumable();

	/**
	 * @return true if the bonus is available for use
	 */
	public boolean isAvailable();

	/**
	 * @return the cooldown time
	 */
	public int getCooldown();

	/**
	 * @return in how many ticks should this Bonus be respawned; it will return
	 *         -1 if it shouldn't respawn at all
	 */
	public int getRespawn();

	/**
	 * Starts the bonus, for example spawning the entity.
	 */
	public void start();

	/**
	 * Cleans up the bonus, for example removing the entity.
	 */
	public void stop();

	/**
	 * @return the list of all actions in this Bonus
	 */
	public List<Action> getActions();

	/**
	 * @param player
	 *            applies the Bonus to the player
	 */
	public void apply(InGamePlayer player);

}
