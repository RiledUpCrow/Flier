/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.util;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import pl.betoncraft.flier.api.Damager;
import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.InGamePlayer;
import pl.betoncraft.flier.api.LoadingException;
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
	public static String formatPlayer(InGamePlayer player) {
		PlayerClass clazz = player.getClazz();
		String name = player.getPlayer().getName();
		return player.getColor() + name + ChatColor.WHITE + " (" + ChatColor.AQUA + clazz.getName() + ChatColor.WHITE + ")";
	}

	/**
	 * @param string
	 * @throws LoadingException 
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

	/**
	 * Adds Damager to metadata of the projectile, so Flier can handle it once
	 * it hits someone.
	 * 
	 * @param entity
	 *            projectile which was launched by Damager
	 * @param damager
	 *            Damager which is the source of that projectile
	 */
	public static void saveDamager(Entity entity, Damager damager, InGamePlayer attacker) {
		entity.setMetadata("flier-damager", new FixedMetadataValue(Flier.getInstance(), damager));
		entity.setMetadata("flier-attacker", new FixedMetadataValue(Flier.getInstance(), attacker));
	}

	/**
	 * Reads the Damager from the projectile. It will return null if the
	 * projectile source is not a Damager.
	 * 
	 * @param entity
	 *            projectile which was launched by Damager
	 * @return Attacker or null
	 */
	public static Attacker getDamager(Entity entity) {
		List<MetadataValue> listD = entity.getMetadata("flier-damager");
		List<MetadataValue> listA = entity.getMetadata("flier-attacker");
		if (!listD.isEmpty() && !listA.isEmpty()) {
			Object d = listD.get(0).value();
			Object a = listA.get(0).value();
			if (d instanceof Damager && a instanceof InGamePlayer) {
				return new Attacker((Damager) d, (InGamePlayer) a);
			} else {
				return new Attacker(DummyDamager.DUMMY, null);
			}
		}
		return null;
	}
	
	/**
	 * Represents an Entity which is a Damager and was launched by InGamePlayer.
	 *
	 * @author Jakub Sapalski
	 */
	public static class Attacker {
	
		private Damager damager;
		private InGamePlayer attacker;
	
		public Attacker(Damager damager, InGamePlayer attacker) {
			this.damager = damager;
			this.attacker = attacker;
		}
	
		public Damager getDamager() {
			return damager;
		}
	
		public InGamePlayer getAttacker() {
			return attacker;
		}
	}

}
