/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier;

import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;

/**
 * Represents a source of projectiles with wing damage.
 *
 * @author Jakub Sapalski
 */
public interface Damager {
	
	public enum DamageResult {
		INSTANT_KILL, WINGS_OFF, WINGS_DAMAGE, REGULAR_DAMAGE, NOTHING
	}
	
	public double getDamage();
	
	public boolean wingsOff();
	
	public boolean killsOnGround();
	
	public double getPhysical();
	
	public static void saveDamager(Entity entity, Damager damager) {
		entity.setMetadata("flier-damager", new FixedMetadataValue(Flier.getInstance(), damager));
	}
	
	public static Damager getDamager(Entity entity) {
		try {
			return (Damager) entity.getMetadata("flier-damager").get(0).value();
		} catch (NullPointerException | ArrayIndexOutOfBoundsException e) {
			// do not catch cast exception - it should not happen
			return null;
		}
	}

}
