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

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.Item;

/**
 * Represents wings - elytra item with statistics.
 *
 * @author Jakub Sapalski
 */
public interface Wings extends Item {

	/**
	 * Calculates modified player's velocity vector. Use this method to
	 * apply your wings' flight model.
	 * 
	 * @param data the Player whose modified velocity must be calculated
	 * @return new velocity Vector
	 */
	public Vector applyFlightModifications(InGamePlayer data);

	/**
	 * @return maximum health of the wings
	 */
	double getMaxHealth();

	/**
	 * @return the regeneration rate of the wings
	 */
	public double getRegeneration();

	/**
	 * @return the amount of health these Wings currently have
	 */
	public double getHealth();

	/**
	 * @param amount
	 *            of health to add to these Wings; must be positive
	 * @return whenever the Wings were fixed or were already at full HP
	 */
	public boolean addHealth(double amount);

	/**
	 * @param amount
	 *            of health to remove from these Wings; must be positive
	 * @return whenever the wings were damaged (true) or were already destroyed
	 *         (false)
	 */
	public boolean removeHealth(double amount);

	/**
	 * @return whenever the wings are disabled
	 */
	public boolean areDisabled();

	/**
	 * @param disabled
	 *            whenever the wings should be disabled
	 */
	public void setDisabled(boolean disabled);

	/**
	 * Refills the Wings, making them as new.
	 */
	public void refill();

}
