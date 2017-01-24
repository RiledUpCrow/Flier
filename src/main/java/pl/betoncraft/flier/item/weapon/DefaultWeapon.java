/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.item.weapon;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.Weapon;
import pl.betoncraft.flier.core.ValueLoader;
import pl.betoncraft.flier.exception.LoadingException;
import pl.betoncraft.flier.item.DefaultUsableItem;

/**
 * A default Weapon implementation.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultWeapon extends DefaultUsableItem implements Weapon {

	protected final boolean suicidal;
	protected final boolean friendlyFire;
	protected final double damage;
	protected final boolean wingsOff;
	protected final boolean killsOnGround;
	protected final double physicalDamage;
	protected final boolean isExploding;
	
	public DefaultWeapon(ConfigurationSection section) throws LoadingException {
		super(section);
		suicidal = ValueLoader.loadBoolean(section, "suicidal");
		friendlyFire = ValueLoader.loadBoolean(section, "friendly_fire");
		damage = ValueLoader.loadDouble(section, "damage");
		wingsOff = ValueLoader.loadBoolean(section, "wings_off");
		killsOnGround = ValueLoader.loadBoolean(section, "kills_on_ground");
		physicalDamage = ValueLoader.loadDouble(section, "physical_damage");
		isExploding = ValueLoader.loadBoolean(section, "exploding");
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
	
	@Override
	public boolean isExploding() {
		return isExploding;
	}

}
