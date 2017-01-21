/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.exception;

/**
 * Thrown when an object is not defined.
 *
 * @author Jakub Sapalski
 */
public class ObjectUndefinedException extends Exception {

	private static final long serialVersionUID = -6894877599352091110L;
	private final String message;
	
	public ObjectUndefinedException(String message) {
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return message;
	}

}
