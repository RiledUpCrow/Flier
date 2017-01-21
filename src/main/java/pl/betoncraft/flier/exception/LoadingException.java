/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.exception;

/**
 * Thrown when there is a problem while loading an object.
 *
 * @author Jakub Sapalski
 */
public class LoadingException extends Exception {

	private static final long serialVersionUID = 78313283731892504L;
	private final String message;
	
	public LoadingException(String message) {
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return message;
	}

}
