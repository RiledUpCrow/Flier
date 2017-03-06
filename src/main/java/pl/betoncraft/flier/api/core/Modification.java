/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api.core;

import java.util.List;

/**
 * Modification modifies one or more properties of an engine, wings, action or
 * activator.
 *
 * @author Jakub Sapalski
 */
public interface Modification {

	/**
	 * The target of the modification.
	 */
	public enum ModificationTarget {
		USABLE_ITEM, ENGINE, WINGS, ACTION, ACTIVATOR
	}

	/**
	 * @return the list of modifiers
	 */
	public List<Modifier> getModifiers();
	
	/**
	 * @return the target of the modification
	 */
	public ModificationTarget getTarget();
	
	/**
	 * @return the names of applicable objects
	 */
	public List<String> getNames();

}
