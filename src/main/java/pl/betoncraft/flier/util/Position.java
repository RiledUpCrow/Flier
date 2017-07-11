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
package pl.betoncraft.flier.util;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import pl.betoncraft.flier.api.core.Usage;

/**
 * Utility class for checking player's position
 *
 * @author Jakub Sapalski
 */
public class Position {
	
	private static final int AIR = 2;

	public static boolean check(Player player, Usage.Where position) {
		boolean ground = ((Entity) player.getPlayer()).isOnGround() ||
				Utils.getAltitude(player.getPlayer().getLocation(), AIR) < AIR;
		boolean air = !ground && player.getPlayer().isGliding() &&
				Utils.getAltitude(player.getPlayer().getLocation(), AIR) == AIR;
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
	
	public static Usage.Where get(Player player) {
		boolean ground = ((Entity) player.getPlayer()).isOnGround();
		boolean air = !ground && player.getPlayer().isGliding() && Utils.getAltitude(player.getPlayer().getLocation(), AIR) == AIR;
		if (ground) {
			return Usage.Where.GROUND;
		} else if (air) {
			return Usage.Where.AIR;
		} else {
			return Usage.Where.FALL;
		}
	}

}
