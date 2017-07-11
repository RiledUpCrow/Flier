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
package pl.betoncraft.flier.core;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import pl.betoncraft.flier.api.content.Game;

/**
 * Base class for all Events which can be matched for an Effect.
 *
 * @author Jakub Sapalski
 */
public abstract class MatchingEvent extends Event {
	
	protected final Game game;

	private final Map<String, Double> numbers = new HashMap<>();
	private final Map<String, String> strings = new HashMap<>();
	private final Map<String, Boolean> booleans = new HashMap<>();

	private static HandlerList handlerList = new HandlerList();
	
	public MatchingEvent(Game game) {
		this.game = game;
	}
	
	protected void setNumber(String name, double number) {
		numbers.put(name, number);
	}
	
	protected void setString(String name, String string) {
		strings.put(name, string.trim().toLowerCase().replace(' ', '_'));
	}
	
	protected void setBool(String name, boolean bool) {
		booleans.put(name, bool);
	}

	/**
	 * Gets the number with the specified name. It will return null if there are
	 * no numbers with this name.
	 * 
	 * @param name
	 *            name of the number
	 * @return the requested number or null
	 */
	public Double getNumber(String name) {
		return numbers.get(name);
	}

	/**
	 * Gets the String with the specified name. It will return null if there are
	 * no Strings with this name.
	 * 
	 * @param name
	 *            name of the String
	 * @return requested String or null
	 */
	public String getString(String name) {
		return strings.get(name);
	}

	/**
	 * Gets the boolean with the specified name. It will return null if there
	 * are no booleans with this name.
	 * 
	 * @param name
	 *            name of the boolean
	 * @return requested boolean or null
	 */
	public Boolean getBool(String name) {
		return booleans.get(name);
	}
	
	/**
	 * @return the Game in which this MatchingEvent was called.
	 */
	public Game getGame() {
		return game;
	}

	@Override
	public HandlerList getHandlers() {
		return handlerList ;
	}

	public static HandlerList getHandlerList() {
		return handlerList ;
	}

}
