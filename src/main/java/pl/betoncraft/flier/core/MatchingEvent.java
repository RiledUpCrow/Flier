/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
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
