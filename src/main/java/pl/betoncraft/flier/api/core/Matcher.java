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
package pl.betoncraft.flier.api.core;

import java.util.List;

/**
 * Stores an object to match in the MatchingEvent.
 *
 * @author Jakub Sapalski
 */
public interface Matcher {

	/**
	 * Type of the Matcher.
	 */
	public enum Type {
		STRING, NUMBER_EXACT, NUMBER_SECTION, BOOLEAN
	}

	/**
	 * @return the name of this Matcher
	 */
	public String getName();

	/**
	 * Specifies the type of this Matcher. Don't use methods for other types
	 * than specified here or there will be errors.
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
