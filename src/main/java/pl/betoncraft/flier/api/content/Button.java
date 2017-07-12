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
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.permissions.Permission;

import pl.betoncraft.flier.api.core.Named;
import pl.betoncraft.flier.api.core.SetApplier;

/**
 * The Button in a Game. It can be locked, and when it's unlocked it supports
 * two operations: buy and sell. All these three actions can have cost and
 * SetAppliers.
 */
public interface Button extends Named {

	/**
	 * @return the Locations of this Button
	 */
	public List<Location> getLocations();

	/**
	 * Sets the Locations at which this Button can be clicked. 
	 * 
	 * @param location
	 *            the Location of this Bonus
	 */
	public void setLocations(List<Location> location);

	/**
	 * @return the set of Button names required for unlocking this Button
	 */
	public Set<String> getRequirements();

	/**
	 * @return the set of permissions required to use this Button
	 */
	public Set<Permission> getPermissions();

	/**
	 * @return the cost to buy an ItemSet
	 */
	public int getBuyCost();

	/**
	 * @return the cost to sell an ItemSet
	 */
	public int getSellCost();

	/**
	 * @return the cost to unlock this Button
	 */
	public int getUnlockCost();

	/**
	 * @return the SetApplier for buying
	 */
	public SetApplier getOnBuy();

	/**
	 * @return the SetApplier for selling
	 */
	public SetApplier getOnSell();

	/**
	 * @return the SetApplier for unlocking
	 */
	public SetApplier getOnUnlock();
}