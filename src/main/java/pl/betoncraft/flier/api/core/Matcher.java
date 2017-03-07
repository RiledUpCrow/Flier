/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api.core;

import java.util.List;

/**
 * Stores an object to match in the MatchingEvent.
 *
 * @author Jakub Sapalski
 */
public interface Matcher {
	
	public enum Type {
		STRING, NUMBER_EXACT, NUMBER_SECTION, BOOLEAN
	}
	
	/**
	 * @return the name of this Matcher
	 */
	public String getName();
	
	/**
	 * Specifies the type of this Matcher. Don't use methods for other types than specified here or there will be errors.
	 * 
	 * @return the type of objects this matcher matches against
	 */
	public Type getType();
	
	/**
	 * This works for the type {@link Type#STRING}.
	 * 
	 * @return the list of Strings to match against
	 */
	public List<String> getStrings();
	
	/**
	 * This works for the type {@link Type#NUMBER_EXACT}.
	 * 
	 * @return the exact number to match against
	 */
	public double exactNumber();
	
	/**
	 * This works for the type {@link Type#NUMBER_SECTION}.
	 * 
	 * @return the minimum number to match
	 */
	public double minNumber();
	
	/**
	 * This works for the type {@link Type#NUMBER_SECTION}.
	 * 
	 * @return the maximum number to match
	 */
	public double maxNumber();
	
	/**
	 * This works for the type {@link Type#BOOLEAN}.
	 * 
	 * @return the boolean to match
	 */
	public boolean bool();

}
