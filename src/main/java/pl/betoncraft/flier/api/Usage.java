/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api;

import java.util.List;

/**
 * Represents a set of Activators which can activate a set of Actions.
 *
 * @author Jakub Sapalski
 */
public interface Usage {
	
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

}
