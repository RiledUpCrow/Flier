/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

/**
 * Various static utility methods.
 *
 * @author Jakub Sapalski
 */
public class Utils {
	
	/**
	 * Calculates the relative altitude of the location above solid ground.
	 * 
	 * @param loc
	 *            location which altitude needs to be calculated
	 * @return the altitude measured in blocks
	 */
	public static int getAltitude(Location loc, int max) {
		loc = loc.clone();
		int altitude = 0;
		if (loc.getBlock().getType() != Material.AIR) {
			return 0;
		}
		while (loc.add(0, -1, 0).getBlock().getType() == Material.AIR && altitude < max) {
			altitude++;
		}
		return altitude;
	}

	/**
	 * Parses the location string.
	 * 
	 * @param string
	 * @return optional location, which will be empty if the format was incorrect
	 */
	public static Location parseLocation(String string) {
		try {
			String[] parts = string.split(";");
			if (parts.length == 4) {
				return new Location(Bukkit.getWorld(parts[3]),
					Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
			} else if (parts.length == 6) {
				return new Location(Bukkit.getWorld(parts[3]),
					Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]),
					Float.parseFloat(parts[4]), Float.parseFloat(parts[5]));
			} else {
				return null	;
			}
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Capitalizes the first letter of the string.
	 * 
	 * @param string String to capitalize
	 * @return the capitalized string
	 */
	public static String capitalize(String string) {
		char[] chars = string.toCharArray();
		chars[0] = (chars[0] + "").toUpperCase().charAt(0);
		return new String(chars);
	}

}
