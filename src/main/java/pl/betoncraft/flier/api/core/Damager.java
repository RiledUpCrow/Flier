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

/**
 * Represents a container for Actions applicable on a player.
 *
 * @author Jakub Sapalski
 */
public interface Damager {

	/**
	 * @return The list containing all further usages this Damager invokes. It's
	 *         not guaranteed that returned list will be mutable.
	 */
	public List<Usage> getSubUsages();

	/**
	 * @return the amount of ticks when the player is immune to damage after the
	 *         hit
	 */
	public int getNoDamageTicks();
	
	/**
	 * @return whenever this Damager can cause friendly fire
	 */
	public boolean causesFriendlyFire();
	
	/**
	 * @return whenever this Damager can damage the player who created it
	 */
	public boolean isSuicidal();
	
	/**
	 * @return whenever this Damager should be considered a final hit; there may
	 *         be more Damagers caused by this one, in which case this method
	 *         should return false
	 */
	public boolean isFinalHit();

}
