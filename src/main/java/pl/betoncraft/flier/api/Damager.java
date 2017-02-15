/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api;

/**
 * Represents a source of projectiles with wing damage.
 *
 * @author Jakub Sapalski
 */
public interface Damager {

	/**
	 * Represents a result of player being hit by Damager. There can be multiple results.
	 *
	 * @author Jakub Sapalski
	 */
	public enum DamageResult {

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
		 * When a hit is accepted at all.
		 */
		HIT,
		

	}

	/**
	 * This damage is dealt to wings. If they don't have enough health, they
	 * should be destroyed.
	 * 
	 * @return amount of damage dealt to the wings
	 */
	public double getDamage();

	/**
	 * If this weapon does not kill instantly, it can inflict physical damage to
	 * player's health.
	 * 
	 * @return the amount of physical damage dealt to the player
	 */
	public double getPhysical();

	/**
	 * Some weapons can make the wings fall of into the player's inventory. It's
	 * possible to get them back on quickly and avoid death from falling.
	 * 
	 * @return whenever wings should fall off on hit
	 */
	public boolean wingsOff();
	
	/**
	 * Some weapons can deal physical damage even if the player is flying.
	 * The usual wing damage is also applied.
	 * 
	 * @return whenever this damager deals physical damage mid-air
	 */
	public boolean midAirPhysicalDamage();
	
	/**
	 * @return true if this damager should damage players in the same team in
	 *         case there are teams
	 */
	public boolean friendlyFire();
	
	/**
	 * @return true if this damager can damage the player who fired it
	 */
	public boolean suicidal();
	
	/**
	 * @return true if the damager can explode and deal damage that way
	 */
	public boolean isExploding();

}
