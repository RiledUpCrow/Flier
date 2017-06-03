/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
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
	 * Performs the action on specified player.
	 * 
	 * @param source
	 *            the optional player who initiated the Action, for example the attacker.
	 * @param target
	 *            the player on which the action needs to be performed
	 * @param item
	 *            the optional item which was used in this action
	 * @return whenever the action was performed or not
	 */
	public boolean act(Optional<InGamePlayer> source, InGamePlayer target, Optional<UsableItem> item);

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
