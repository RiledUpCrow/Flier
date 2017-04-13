/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api.core;

import java.util.List;

import org.bukkit.entity.Entity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.core.DefaultAttacker;
import pl.betoncraft.flier.util.DummyDamager;

/**
 * Groups together the Damager used in the attack, the author of the attack and
 * the weapon used.
 *
 * @author Jakub Sapalski
 */
public interface Attacker {

	/**
	 * @return the Damager used in the attack
	 */
	public Damager getDamager();

	/**
	 * @return the author of the attack, may be null if it comes from the
	 *         environment
	 */
	public InGamePlayer getShooter();

	/**
	 * @return the weapon used in the attack, may be null if the Damager wasn't
	 *         launched from an item
	 */
	public UsableItem getWeapon();

	/**
	 * Reads the Attacker from the projectile. It will return null if the
	 * projectile source is not a Damager.
	 * 
	 * @param entity
	 *            projectile which was launched by Damager
	 * @return Attacker or null
	 */
	static Attacker getAttacker(Entity entity) {
		List<MetadataValue> listD = entity.getMetadata("flier-damager");
		List<MetadataValue> listA = entity.getMetadata("flier-attacker");
		List<MetadataValue> listW = entity.getMetadata("flier-weapon");
		if (!listD.isEmpty() && !listA.isEmpty()) {
			Object d = listD.get(0).value();
			Object a = listA.get(0).value();
			Object w = listW == null || listW.isEmpty() ? null : listW.get(0).value();
			if (d instanceof Damager && a instanceof InGamePlayer && (w == null || w instanceof UsableItem)) {
				return new DefaultAttacker((Damager) d, (InGamePlayer) a, w == null ? null : (UsableItem) w);
			} else {
				return new DefaultAttacker(DummyDamager.DUMMY, null, null);
			}
		}
		return null;
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
	static void saveAttacker(Entity entity, Attacker attacker) {
		entity.setMetadata("flier-damager", new FixedMetadataValue(Flier.getInstance(), attacker.getDamager()));
		entity.setMetadata("flier-attacker", new FixedMetadataValue(Flier.getInstance(), attacker.getShooter()));
		entity.setMetadata("flier-weapon", new FixedMetadataValue(Flier.getInstance(), attacker.getWeapon()));
	}

}