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

import org.bukkit.util.Vector;

import pl.betoncraft.flier.api.core.Item;

/**
 * Represents an engine, which speeds players up.
 *
 * @author Jakub Sapalski
 */
public interface Engine extends Item {

	/**
	 * Calculates the Engine's acceleration vector from player's velocity and
	 * head direction vectors. Use it to implement your Engine's mechanics.
	 * 
	 * @param velocity
	 *            velocity Vector of the player
	 * @param direction
	 *            direction Vector where the player is looking
	 * @return the modified velocity Vector
	 */
	public Vector launch(Vector velocity, Vector direction);

	/**
	 * @return the maximum amount of stored fuel
	 */
	public double getMaxFuel();

	/**
	 * @return the fuel consumption per flight tick
	 */
	public double getConsumption();

	/**
	 * @return the fuel regeneration per flight tick
	 */
	public double getRegeneration();

	/**
	 * @return the amount of fuel currently in the Engine
	 */
	public double getFuel();

	/**
	 * @param amount
	 *            amount of fuel to add; must be positive
	 * @return whenever the fuel was successfully added; false means the tank is
	 *         full
	 */
	public boolean addFuel(double amount);

	/**
	 * @param amount
	 *            amount of fuel to remove; must be positive
	 * @return whenever the fuel was successfully removed; false means the tank
	 *         is empty
	 */
	public boolean removeFuel(double amount);

	/**
	 * Refills the Engine, making it as new.
	 */
	public void refill();

}
