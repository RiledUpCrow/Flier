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

/**
 * Groups together the Damager used in the attack, the author of the attack and
 * the weapon used.
 *
 * @author Jakub Sapalski
 */
public interface Attacker {

	public static final String DAMAGER = "flier-damager";
	public static final String CREATOR = "flier-creator";
	public static final String SOURCE = "flier-source";
	public static final String WEAPON = "flier-weapon";

	/**
	 * @return the Damager used in the attack
	 */
	public Damager getDamager();

	/**
	 * @return the author of the attack, may be null if it comes from the
	 *         environment
	 */
	public InGamePlayer getCreator();
	
	/**
	 * @return the direct source of the attack, may be null if it comes from
	 *         the environment
	 */
	public InGamePlayer getSource();

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
	public static Attacker getAttacker(Entity entity) {
		List<MetadataValue> listD = entity.getMetadata(DAMAGER);
		List<MetadataValue> listC = entity.getMetadata(CREATOR);
		List<MetadataValue> listS = entity.getMetadata(SOURCE);
		List<MetadataValue> listW = entity.getMetadata(WEAPON);
		Damager      d = listD == null || listD.isEmpty() ? null : (Damager)      listD.get(0).value();
		InGamePlayer c = listC == null || listC.isEmpty() ? null : (InGamePlayer) listC.get(0).value();
		InGamePlayer s = listS == null || listS.isEmpty() ? null : (InGamePlayer) listS.get(0).value();
		UsableItem   w = listW == null || listW.isEmpty() ? null : (UsableItem)   listW.get(0).value();
		return new DefaultAttacker(d, c, s, w);
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
	public static void saveAttacker(Entity entity, Attacker attacker) {
		entity.setMetadata(DAMAGER, new FixedMetadataValue(Flier.getInstance(), attacker.getDamager()));
		entity.setMetadata(CREATOR, new FixedMetadataValue(Flier.getInstance(), attacker.getCreator()));
		entity.setMetadata(SOURCE, new FixedMetadataValue(Flier.getInstance(), attacker.getSource()));
		entity.setMetadata(WEAPON, new FixedMetadataValue(Flier.getInstance(), attacker.getWeapon()));
	}

}