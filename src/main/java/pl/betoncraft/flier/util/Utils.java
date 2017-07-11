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

import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.Kit;
import pl.betoncraft.flier.api.core.Target;
import pl.betoncraft.flier.api.core.UsableItem;

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
	 * Capitalizes the first letter of the string.
	 * 
	 * @param string String to capitalize
	 * @return the capitalized string
	 */
	public static String capitalize(String string) {
		if (string.isEmpty()) {
			return string;
		}
		char[] chars = string.toCharArray();
		chars[0] = (chars[0] + "").toUpperCase().charAt(0);
		return new String(chars);
	}

	
	/**
	 * Formats the player name in team's color and appends class name. The
	 * String ends in white color.
	 * 
	 * @param player
	 *            InGamePlayer object containing player's information
	 * @param receiver
	 *            InGamePlayer who will receive this message (for translation
	 *            purposes)
	 * @return the formatted name
	 */
	public static String formatPlayer(InGamePlayer player, InGamePlayer receiver) {
		Kit kit = player.getKit();
		String className = kit.getClassName();
		if (className != null) {
			className = className.startsWith("$") ?
					LangManager.getMessage(receiver, kit.getClassName().substring(1)) :
					kit.getClassName();
		}
		String name = player.getPlayer().getName();
		return player.getColor() + name + (className == null ? "" :
						ChatColor.WHITE + " (" + ChatColor.AQUA + className + ChatColor.WHITE + ")");
	}
	
	public static String formatTarget(Target target, InGamePlayer receiver) {
		if (target instanceof InGamePlayer) {
			return formatPlayer((InGamePlayer) target, receiver);
		}
		return "TARGET"; // TODO target formatting
	}
	
	/**
	 * Formats the item into a readable string.
	 * 
	 * @param item
	 *            the item to format
	 * @param receiver
	 *            the receive of this message (for translation purposes)
	 * @return the formatted item
	 */
	public static String formatItem(UsableItem item, InGamePlayer receiver) {
		ItemStack stack = item.getItem(receiver);
		return ChatColor.WHITE + "[" + stack.getItemMeta().getDisplayName() + ChatColor.WHITE + "]";
	}

	/**
	 * Parses the Location from string.
	 * 
	 * @param string
	 *            the location in x;y;z;world;yaw;pitch format
	 * @throws LoadingException
	 *             when something's wrong while parsing
	 */
	public static Location parseLocation(String string) throws LoadingException {
		if (string == null) {
			throw new LoadingException("Location is not defined.");
		}
		String[] parts = string.split(";");
		if (parts.length >= 4) {
			World world = Bukkit.getWorld(parts[3]);
			if (world == null) {
				throw new LoadingException(String.format("World '%s' does not exist.", parts[3]));
			}
			double x, y, z;
			try {
				x = Double.parseDouble(parts[0]);
				y = Double.parseDouble(parts[1]);
				z = Double.parseDouble(parts[2]);
			} catch (NumberFormatException e) {
				throw new LoadingException("Cannot parse coordinates.");
			}
			float yaw = 0, pitch = 0;
			if (parts.length == 6) {
				try {
					yaw = Float.parseFloat(parts[4]);
					pitch = Float.parseFloat(parts[5]);
				} catch (NumberFormatException e) {
					throw new LoadingException("Cannot parse head rotation.");
				}
			}
			return new Location(world, x, y, z, yaw, pitch);
		} else {
			throw new LoadingException("Incorrect location format.");
		}
	}
	
	/**
	 * Clears all player's stuff.
	 * 
	 * @param player
	 */
	public static void clearPlayer(Player player) {
		player.getInventory().clear();
		player.setGameMode(GameMode.SURVIVAL);
		player.resetMaxHealth();
		player.setHealth(player.getMaxHealth());
		player.setExp(0);
		player.setLevel(0);
		player.setExhaustion(0);
		player.setFireTicks(0);
		player.setFallDistance(0);
		player.eject();
		player.setAllowFlight(false);
		player.setCanPickupItems(false);
		player.setCollidable(true);
		player.setFlying(false);
		player.setGliding(false);
		player.setVelocity(new Vector());
		player.setFoodLevel(20);
		player.setGlowing(false);
		player.setGravity(true);
		player.setInvulnerable(false);
		player.setSaturation(20);
		for (PotionEffectType type : player.getActivePotionEffects().stream()
				.map(effect -> effect.getType()).collect(Collectors.toList())) {
			player.removePotionEffect(type);
		}
	}

}
