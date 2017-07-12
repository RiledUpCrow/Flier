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

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.Modification;
import pl.betoncraft.flier.api.core.Named;
import pl.betoncraft.flier.api.core.Owned;

/**
 * Represents an action which can happen for an InGamePlayer.
 *
 * @author Jakub Sapalski
 */
public interface Action extends Named, Owned {

	/**
	 * <p>
	 * Performs the action on specified player.
	 * </p>
	 * 
	 * <p>
	 * To understand the arguments here, let's imagine a UsableItem, which has a
	 * rocket-shooting action. The rocket will create an explosion upon impact,
	 * and that explosion will damage everyone around, with damage scaled with
	 * the distance from the center.
	 * </p>
	 * 
	 * <p>
	 * (This is done by a rocket action which fires an explosion action which
	 * fires a damage action.)
	 * </p>
	 * 
	 * <p>
	 * Now player A launches the rocket and hits player B, and player C gets
	 * caught in the blast radius. Running that last damage action will have
	 * player A as the owner ({@link #getOwner()}), player B as the source (2nd
	 * argument) and player C as the target (1st argument).
	 * </p>
	 * 
	 * @param source
	 *            the optional player who is the direct source of the Action
	 * @param target
	 *            the player on which the Action is performed
	 * @return whenever the action was performed or not
	 */
	public boolean act(InGamePlayer target, InGamePlayer source);
	
	/**
	 * Applies modifications to this Action. If the Action is an Attack, it will
	 * apply modifications recursively on all subActions.
	 * 
	 * @param mod
	 *            Modification to apply
	 */
	public void addModification(Modification mod);

	/**
	 * Removes modifications from this Action. If the Action is an Attack, it
	 * will remove modifications recursively on all subActions.
	 * 
	 * @param mod
	 *            Modification to remove
	 */
	public void removeModification(Modification mod);

}
