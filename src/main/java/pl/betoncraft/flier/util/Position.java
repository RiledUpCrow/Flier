/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.util;

import org.bukkit.entity.Player;

/**
 * Utility class for checking player's position
 *
 * @author Jakub Sapalski
 */
public class Position {

	/**
	 * Represents a location where the player can be.
	 *
	 * @author Jakub Sapalski
	 */
	public enum Where {
		GROUND, AIR, FALL, NO_GROUND, NO_AIR, NO_FALL, EVERYWHERE
	}
	
	public static boolean check(Player player, Where position) {
		boolean air = player.getPlayer().isGliding();
		boolean ground = !air && Utils.getAltitude(player.getPlayer().getLocation(), 4) < 4;
		boolean fall = !ground && !air;
		switch (position) {
		case GROUND:	 return ground;
		case AIR:		 return air;
		case FALL:		 return fall;
		case NO_GROUND:	 return !ground;
		case NO_AIR:	 return !air;
		case NO_FALL:	 return !fall;
		case EVERYWHERE: return true;
		}
		return false;
	}
	
	public static Where get(Player player) {
		boolean air = player.getPlayer().isGliding();
		boolean ground = !air && Utils.getAltitude(player.getPlayer().getLocation(), 4) < 4;
		if (air) {
			return Where.AIR;
		} else if (ground) {
			return Where.GROUND;
		} else {
			return Where.FALL;
		}
	}

}
