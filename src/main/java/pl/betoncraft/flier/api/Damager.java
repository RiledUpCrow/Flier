/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api;

import org.bukkit.entity.Projectile;
import org.bukkit.metadata.FixedMetadataValue;

import pl.betoncraft.flier.Flier;

/**
 * Represents a source of projectiles with wing damage.
 *
 * @author Jakub Sapalski
 */
public interface Damager {

	/**
	 * Represents a result of player being hit by Damager.
	 *
	 * @author Jakub Sapalski
	 */
	public enum DamageResult {

		/**
		 * When the player is on ground and Damager has instant killing option
		 */
		INSTANT_KILL,

		/**
		 * When the player is gliding and Damager has "wings off" option
		 */
		WINGS_OFF,

		/**
		 * When the player is gliding and his wings can receive damage.
		 */
		WINGS_DAMAGE,

		/**
		 * When the player is on ground and was not instantly killed.
		 */
		REGULAR_DAMAGE,

		/**
		 * When the player is falling.
		 */
		NOTHING

	}

	/**
	 * This damage is dealt to wings. If they don't have enough health, they
	 * should be destroyed.
	 * 
	 * @return amount of damage dealt to the wings
	 */
	public double getDamage();

	/**
	 * Some weapons can make the wings fall of into the player's inventory. It's
	 * possible to get them back on quickly and avoid death from falling.
	 * 
	 * @return whenever wings should fall off on hit
	 */
	public boolean wingsOff();

	/**
	 * Some weapons are powerful enough to kill with one hit players who are on
	 * the ground.
	 * 
	 * @return whenever the player on ground should be instantly killed
	 */
	public boolean killsOnGround();

	/**
	 * If this weapon does not kill instantly, it can inflict physical damage to
	 * player's health.
	 * 
	 * @return the amount of physical damage dealt to the player
	 */
	public double getPhysical();

	/**
	 * Adds Damager to metadata of the projectile, so Flier can handle it once
	 * it hits someone.
	 * 
	 * @param projectile
	 *            projectile which was launched by Damager
	 * @param damager
	 *            Damager which is the source of that projectile
	 */
	public static void saveDamager(Projectile projectile, Damager damager) {
		projectile.setMetadata("flier-damager", new FixedMetadataValue(Flier.getInstance(), damager));
	}

	/**
	 * Reads the Damager from the projectile. It will return null if the
	 * projectile source is not a Damager.
	 * 
	 * @param projectile
	 *            projectile which was launched by Damager
	 * @return Damager or null
	 */
	public static Damager getDamager(Projectile projectile) {
		try {
			return (Damager) projectile.getMetadata("flier-damager").get(0).value();
		} catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
			// do not catch cast exception - it should not happen
			return null;
		}
	}

}
