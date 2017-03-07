/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core;

import java.util.List;

import pl.betoncraft.flier.api.core.Matcher;

/**
 * Default implementation of the Matcher.
 *
 * @author Jakub Sapalski
 */
public class DefaultMatcher implements Matcher {
	
	private String name;
	private Type type;
	
	private List<String> list;
	private double exactNumber;
	private double minNumber, maxNumber;
	private boolean bool;
	
	public DefaultMatcher(String name, List<String> list) {
		this.name = name;
		type = Type.STRING;
		this.list = list;
	}
	
	public DefaultMatcher(String name, double number) {
		this.name = name;
		type = Type.NUMBER_EXACT;
		exactNumber = number;
	}
	
	public DefaultMatcher(String name, double min, double max) {
		this.name = name;
		type = Type.NUMBER_SECTION;
		minNumber = min;
		maxNumber = max;
	}
	
	public DefaultMatcher(String name, boolean bool) {
		this.name = name;
		type = Type.BOOLEAN;
		this.bool = bool;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Type getType() {
		return type;
	}

	@Override
	public List<String> getStrings() {
		return list;
	}

	@Override
	public double exactNumber() {
		return exactNumber;
	}

	@Override
	public double minNumber() {
		return minNumber;
	}

	@Override
	public double maxNumber() {
		return maxNumber;
	}

	@Override
	public boolean bool() {
		return bool;
	}

}
