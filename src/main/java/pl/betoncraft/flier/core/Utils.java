/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

import pl.betoncraft.flier.api.PlayerClass;

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

	
	/**
	 * Formats the player name in team's color and appends class name.
	 * The String ends in white color.
	 * 
	 * @param player InGamePlayer object containing player's information
	 * @return the formatted name
	 */
	public static String formatPlayer(PlayerData player) {
		PlayerClass clazz = player.getClazz();
		String name = player.getPlayer().getName();
		return player.getColor() + name + ChatColor.WHITE + " (" + ChatColor.AQUA + clazz.getName() + ChatColor.WHITE + ")";
	}
	
	public static class ImmutableVector {
		private final double x, y, z;
		private Double length;
		public ImmutableVector(double x, double y, double z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}
		public static ImmutableVector fromVector(Vector vec) {
			return new ImmutableVector(vec.getX(), vec.getY(), vec.getZ());
		}
		public double getX() {
			return x;
		}
		public double getY() {
			return y;
		}
		public double getZ() {
			return z;
		}
		public ImmutableVector add(ImmutableVector vec) {
			return new ImmutableVector(x + vec.x, y + vec.y, z + vec.z);
		}
		public ImmutableVector subtract(ImmutableVector vec) {
			return new ImmutableVector(x - vec.x, y - vec.y, z - vec.z);
		}
		public ImmutableVector multiply(double m) {
			return new ImmutableVector(x*m, y*m, z*m);
		}
		public double length() {
			if (length == null) {
				length = Math.sqrt(x*x + y*y + z*z);
			}
			return length;
		}
		public ImmutableVector normalize() {
			length();
			return new ImmutableVector(x / length, y / length, z / length);
		}
		public Vector toVector() {
			return new Vector(x, y, z);
		}
		@Override
		public String toString() {
			return String.format("[%.3f,%.3f,%.3f]", x, y, z);
		}
	}

}
