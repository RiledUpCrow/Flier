/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core;

import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.Modifier;

/**
 * Default implementation of Modifier.
 *
 * @author Jakub Sapalski
 */
public class DefaultModifier implements Modifier {
	
	protected String property;
	protected String value;
	
	public DefaultModifier(String property, String value) throws LoadingException {
		this.property = property;
		this.value = value;
	}

	@Override
	public String getProperty() {
		return property;
	}

	@Override
	public String getValue() {
		return value;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof DefaultModifier) {
			DefaultModifier mod = (DefaultModifier) o;
			return mod.property.equals(property) && mod.value.equals(value);
		}
		return false;
	}

}
