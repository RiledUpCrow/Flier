/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.util;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.Action;
import pl.betoncraft.flier.api.Activator;
import pl.betoncraft.flier.api.Engine;
import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.LoadingException;
import pl.betoncraft.flier.api.Wings;

/**
 * Contains functions used to load various values from the ConfigurationSection
 * and fail when they are missing.
 *
 * @author Jakub Sapalski
 */
public class ValueLoader {
	
	private ConfigurationSection section;
	
	public ValueLoader(ConfigurationSection section) {
		this.section = section;
		if (section == null) {
			throw new NullPointerException("ConfigurationSection is null");
		}
	}
	
	public String loadString(String address) throws LoadingException {
		String value = section.getString(address, null);
		if (value == null) {
			throw new LoadingException(String.format("'%s' must be specified.", address));
		}
		return value;
	}
	
	public String loadString(String address, String def) {
		return section.getString(address, def);
	}
	
	private Object get(String address, Object def) throws LoadingException {
		if (section.contains(address)) {
			return section.get(address);
		} else if (def == null) {
			throw new LoadingException(String.format("'%s' must be specified.", address));
		} else {
			return def;
		}
	}
	
	public double loadDouble(String address, Double def) throws LoadingException {
		Object obj = get(address, def);
		if (obj instanceof Number) {
			return ((Number) obj).doubleValue();
		} else {
			throw new LoadingException(String.format("'%s' must be a decimal.", address));
		}
	}
	
	public double loadDouble(String address) throws LoadingException {
		return loadDouble(address, null);
	}
	
	public double loadPositiveDouble(String address, Double def) throws LoadingException {
		double value = loadDouble(address, def);
		if (value <= 0) {
			throw new LoadingException(String.format("'%s' must be a positive decimal.", address));
		}
		return value;
	}
	
	public double loadPositiveDouble(String address) throws LoadingException {
		return loadPositiveDouble(address, null);
	}
	
	public double loadNonNegativeDouble(String address, Double def) throws LoadingException {
		double value = loadDouble(address, def);
		if (value < 0) {
			throw new LoadingException(String.format("'%s' must not be a negative decimal.", address));
		}
		return value;
	}
	
	public double loadNonNegativeDouble(String address) throws LoadingException {
		return loadNonNegativeDouble(address, null);
	}
	
	public int loadInt(String address, Integer def) throws LoadingException {
		Object obj = get(address, def);
		if (obj instanceof Number) {
			return ((Number) obj).intValue();
		} else {
			throw new LoadingException(String.format("'%s' must be an integer.", address));
		}
	}
	
	public int loadInt(String address) throws LoadingException {
		return loadInt(address, null);
	}
	
	public int loadPositiveInt(String address, Integer def) throws LoadingException {
		int value = loadInt(address, def);
		if (value <= 0) {
			throw new LoadingException(String.format("'%s' must be a positive integer.", address));
		}
		return value;
	}
	
	public int loadPositiveInt(String address) throws LoadingException {
		return loadPositiveInt(address, null);
	}
	
	public int loadNonNegativeInt(String address, Integer def) throws LoadingException {
		int value = loadInt(address, def);
		if (value < 0) {
			throw new LoadingException(String.format("'%s' must not be a negative integer.", address));
		}
		return value;
	}
	
	public int loadNonNegativeInt(String address) throws LoadingException {
		return loadNonNegativeInt(address, null);
	}
	
	public boolean loadBoolean(String address, Boolean def) throws LoadingException {
		Object obj = get(address, def);
		if (obj instanceof Boolean) {
			return (Boolean) obj;
		} else {
			throw new LoadingException(String.format("'%s' must be either `true` or `false`.", address));
		}
	}
	
	public boolean loadBoolean(String address) throws LoadingException {
		return loadBoolean(address, null);
	}
	
	public Location loadLocation(String address, Location def) throws LoadingException {
		Object obj = get(address, def);
		if (obj instanceof Location) {
			return (Location) obj;
		} else if (obj instanceof String) {
			try {
				return Utils.parseLocation((String) obj);
			} catch (LoadingException e) {
				throw (LoadingException) new LoadingException(String.format("Error in '%s' location.", address))
						.initCause(e);
			}
		} else {
			throw new LoadingException(String.format("'%s' must be a location.", address));
		}
	}
	
	public Location loadLocation(String address) throws LoadingException {
		return loadLocation(address, null);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Enum<T>> T loadEnum(String address, T def, Class<T> enumClass) throws LoadingException {
		Object obj = get(address, def);
		if (enumClass.isInstance(obj)) {
			return (T) obj;
		} else if (obj instanceof String) {
			try {
				return Enum.valueOf(enumClass, ((String) obj).toUpperCase().replace(' ', '_'));
			} catch (IllegalArgumentException e) {
				Exception detail = new LoadingException(String.format("%s '%s' does not exist.", enumClass.getSimpleName(), ((String) obj)));
				Exception error  = new LoadingException(String.format("'%s' must be a valid type.", address));
				throw (LoadingException) error.initCause(detail);
			}
		} else {
			throw new LoadingException(String.format("'%s' must be a valid type.", address, enumClass.getSimpleName()));
		}
	}
	
	public <T extends Enum<T>> T loadEnum(String address, Class<T> enumClass) throws LoadingException {
		return loadEnum(address, null, enumClass);
	}
	
	public Action loadAction(String address) throws LoadingException {
		return Flier.getInstance().getAction(loadString(address));
	}
	
	public Activator loadActivator(String address) throws LoadingException {
		return Flier.getInstance().getActivator(loadString(address));
	}
	
	public Engine loadEngine(String address) throws LoadingException {
		return Flier.getInstance().getEngine(loadString(address));
	}
	
	public Wings loadWings(String address) throws LoadingException {
		return Flier.getInstance().getWing(loadString(address));
	}

}
