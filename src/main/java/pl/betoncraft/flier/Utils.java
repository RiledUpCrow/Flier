/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier;

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

}
