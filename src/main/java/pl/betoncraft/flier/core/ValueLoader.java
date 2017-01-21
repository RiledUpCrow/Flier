/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.Flier;
import pl.betoncraft.flier.api.Effect;
import pl.betoncraft.flier.api.Engine;
import pl.betoncraft.flier.api.Wings;
import pl.betoncraft.flier.exception.LoadingException;

/**
 * Contains functions used to load various values from the ConfigurationSection
 * and fail when they are missing.
 *
 * @author Jakub Sapalski
 */
public class ValueLoader {
	
	public static String loadString(ConfigurationSection section, String address) throws LoadingException {
		String value = section.getString(address, null);
		if (value == null) {
			throw new LoadingException(String.format("'%s' must be specified.", address));
		}
		return value;
	}
	
	public static double loadDouble(ConfigurationSection section, String address) throws LoadingException {
		if (!section.contains(address) || !(section.isDouble(address) || section.isInt(address))) {
			throw new LoadingException(String.format("'%s' must be specified decimal.", address));
		}
		return section.getDouble(address);
	}
	
	public static double loadPositiveDouble(ConfigurationSection section, String address) throws LoadingException {
		double value = loadDouble(section, address);
		if (value <= 0) {
			throw new LoadingException(String.format("'%s' must be a positive decimal.", address));
		}
		return value;
	}
	
	public static double loadNonNegativeDouble(ConfigurationSection section, String address) throws LoadingException {
		double value = loadDouble(section, address);
		if (value < 0) {
			throw new LoadingException(String.format("'%s' must not be a negative decimal.", address));
		}
		return value;
	}
	
	public static int loadInt(ConfigurationSection section, String address) throws LoadingException {
		if (!section.contains(address) || !section.isInt(address)) {
			throw new LoadingException(String.format("'%s' must be specified integer.", address));
		}
		return section.getInt(address);
	}
	
	public static int loadPositiveInt(ConfigurationSection section, String address) throws LoadingException {
		int value = loadInt(section, address);
		if (value <= 0) {
			throw new LoadingException(String.format("'%s' must be a positive integer.", address));
		}
		return value;
	}
	
	public static int loadNonNegativeInt(ConfigurationSection section, String address) throws LoadingException {
		int value = loadInt(section, address);
		if (value < 0) {
			throw new LoadingException(String.format("'%s' must not be a negative integer.", address));
		}
		return value;
	}
	
	public static boolean loadBoolean(ConfigurationSection section, String address) throws LoadingException {
		if (!section.contains(address) || !section.isBoolean(address)) {
			throw new LoadingException(String.format("'%s' must be either true or false.", address));
		}
		return section.getBoolean(address);
	}
	
	public static Location loadLocation(ConfigurationSection section, String address) throws LoadingException {
		try {
			String string = loadString(section, address);
			return Utils.parseLocation(string);
		} catch (LoadingException e) {
			throw (LoadingException) new LoadingException(String.format("Error in '%s' location.", address))
					.initCause(e);
		}
	}
	
	public static <T extends Enum<T>> T loadEnum(ConfigurationSection section, String address, Class<T> enumClass)
			throws LoadingException {
		try {
			String enumName = loadString(section, address);
			try {
				return Enum.valueOf(enumClass, enumName.toUpperCase().replace(' ', '_'));
			} catch (IllegalArgumentException e) {
				throw new LoadingException(String.format("%s '%s' does not exist.", enumClass.getSimpleName(), enumName));
			}
		} catch (LoadingException e) {
			throw (LoadingException) new LoadingException(String.format("Error in '%s' %s.", address,
					enumClass.getSimpleName())).initCause(e);
		}
	}
	
	public static Effect loadEffect(ConfigurationSection section, String address) throws LoadingException {
		String effectName = loadString(section, address);
		return Flier.getInstance().getEffect(effectName);
	}
	
	public static Engine loadEngine(ConfigurationSection section, String address) throws LoadingException {
		String engineName = loadString(section, address);
		return Flier.getInstance().getEngine(engineName);
	}
	
	public static Wings loadWings(ConfigurationSection section, String address) throws LoadingException {
		String wingsName = loadString(section, address);
		return Flier.getInstance().getWing(wingsName);
	}
	

}
