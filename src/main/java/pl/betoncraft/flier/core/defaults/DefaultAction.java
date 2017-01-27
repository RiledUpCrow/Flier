/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core.defaults;

import pl.betoncraft.flier.api.Action;

/**
 * Default implementation of Action (only replication, actually).
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultAction implements Action {

	@Override
	public DefaultAction replicate() {
		return null; // TODO replicate
	}

}
