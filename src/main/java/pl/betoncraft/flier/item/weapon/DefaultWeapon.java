/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.item.weapon;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.Weapon;
import pl.betoncraft.flier.exception.LoadingException;
import pl.betoncraft.flier.item.DefaultUsableItem;

/**
 * A default Weapon implementation.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultWeapon extends DefaultUsableItem implements Weapon {

	protected boolean suicidal = false;
	protected boolean friendlyFire = true;

	protected double damage = 50;
	protected boolean wingsOff = true;
	protected boolean killsOnGround = true;
	protected double physicalDamage = 5;
	
	public DefaultWeapon(ConfigurationSection section) throws LoadingException {
		super(section);
		suicidal = section.getBoolean("suicidal", suicidal);
		friendlyFire = section.getBoolean("friendly_fire", friendlyFire);
		damage = section.getDouble("damage", damage);
		wingsOff = section.getBoolean("wings_off", wingsOff);
		killsOnGround = section.getBoolean("kills_on_ground", killsOnGround);
		physicalDamage = section.getDouble("physical_damage", physicalDamage);
	}
	
	@Override
	public double getDamage() {
		return damage;
	}
	
	@Override
	public boolean wingsOff() {
		return wingsOff;
	}
	
	@Override
	public boolean killsOnGround() {
		return killsOnGround;
	}

	@Override
	public double getPhysical() {
		return physicalDamage;
	}
	
	@Override
	public boolean friendlyFire() {
		return friendlyFire;
	}

	@Override
	public boolean suicidal() {
		return suicidal;
	}

}
