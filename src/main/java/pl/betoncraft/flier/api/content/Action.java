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

import java.util.Optional;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.Modification;
import pl.betoncraft.flier.api.core.UsableItem;

/**
 * Represents a one time action, as opposed to the repeating Effect.
 *
 * @author Jakub Sapalski
 */
public interface Action {

	/**
	 * <p>Performs the action on specified player.</p>
	 * 
	 * <p>To understand the arguments here, let's imagine a UsableItem, which has a rocket-shooting action.
	 * The rocket will create an explosion upon impact, and that explosion will damage everyone around, with
	 * damage scaled with the distance from the center.</p>
	 * 
	 * <p>Now player A launches the rocket and hits player B, and player C gets caught in the blast radius.
	 * Running the last damage action will have player A as the creator (1st argument), player B as the source
	 * (2nd argument) and player C as the target (3rd argument).</p>
	 * 
	 * <p>Usually creator, source and target are the same players though.</p>
	 * 
	 * @param creator
	 *            the optional player who initiated the Action
	 * @param source
	 *            the optional player who is the direct source of the Action
	 * @param target
	 *            the player on which the Action is performed
	 * @param item
	 *            the optional UsableItem which generated this Action
	 * @return whenever the action was performed or not
	 */
	public boolean act(Optional<InGamePlayer> creator,
			Optional<InGamePlayer> source,
			InGamePlayer target,
			Optional<UsableItem> item);

	/**
	 * @return the ID of this Action, under which it is defined in actions.yml
	 */
	public String getID();
	
	/**
	 * Applies passed modification to this Action.
	 * 
	 * @param mod
	 */
	public void addModification(Modification mod);
	
	/**
	 * Removes passed modification from this Action.
	 * 
	 * @param mod
	 */
	public void removeModification(Modification mod);

	/**
	 * @return whenever this Action needs to be launched from an item to work correctly.
	 */
	public boolean needsItem();

	/**
	 * @return whenever this Action needs to have a source player to work correctly.
	 */
	public boolean needsSource();

}
