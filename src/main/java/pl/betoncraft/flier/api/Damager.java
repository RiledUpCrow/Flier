/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import pl.betoncraft.flier.Flier;
import pl.betoncraft.flier.util.DummyDamager;

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
	public class Attacker {

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
