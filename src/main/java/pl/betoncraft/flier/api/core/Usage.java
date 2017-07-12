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
package pl.betoncraft.flier.api.core;

import java.util.List;

import pl.betoncraft.flier.api.content.Action;
import pl.betoncraft.flier.api.content.Activator;

/**
 * Represents a set of Activators which can activate a set of Actions.
 *
 * @author Jakub Sapalski
 */
public interface Usage extends Named {

	/**
	 * Represents a position where the player can be.
	 *
	 * @author Jakub Sapalski
	 */
	public enum Where {
		GROUND, AIR, FALL, NO_GROUND, NO_AIR, NO_FALL, EVERYWHERE
	}

	/**
	 * @return the list of Activators in this Usage
	 */
	public List<Activator> getActivators();

	/**
	 * @return the list of Actions in this Usage
	 */
	public List<Action> getActions();

	/**
	 * @return the cooldown time this Usage takes
	 */
	public int getCooldown();

	/**
	 * @return the amount of ammo this Usage uses per use
	 */
	public int getAmmoUse();

	/**
	 * @return where this item can be used
	 */
	public Where where();

	/**
	 * Checks if the player is in a correct position to use this item.
	 * 
	 * @param player
	 *            player to check
	 * @return whenever it's possible to use this item in player's position
	 */
	public boolean canUse(InGamePlayer player);

}
