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

import java.util.List;
import java.util.stream.Collectors;

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
		this.list = list.stream().map(s -> s.trim().toLowerCase().replace(' ', '_')).collect(Collectors.toList());
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
