/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api.content;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.UsableItem;

/**
 * Represents a condition, under which a Usage can be activated.
 *
 * @author Jakub Sapalski
 */
public interface Activator {

	/**
	 * @return whenever the Activator is active for this player
	 */
	public boolean isActive(InGamePlayer player, UsableItem item);

}
