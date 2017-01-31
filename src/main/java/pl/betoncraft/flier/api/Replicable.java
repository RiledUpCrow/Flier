/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api;

/**
 * Represents an object which can be replicated, retaining all default values
 * but with clean modifiable values.
 *
 * @author Jakub Sapalski
 */
public interface Replicable {

	/**
	 * Replicates the object. All default values must be the same in both
	 * objects, and all modifiable values must be clean in the replicated
	 * object. This method must return "self" type.
	 * 
	 * @return replicated object of the same type.
	 */
	public Replicable replicate();

}
