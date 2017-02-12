/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core.defaults;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.Attack;
import pl.betoncraft.flier.exception.LoadingException;

/**
 * A default Weapon implementation.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultAttack extends DefaultAction implements Attack {

	protected final boolean suicidal;
	protected final boolean friendlyFire;
	protected final double damage;
	protected final boolean wingsOff;
	protected final double physicalDamage;
	protected final boolean isExploding;
	
	public DefaultAttack(ConfigurationSection section) throws LoadingException {
		super(section);
		suicidal = loader.loadBoolean("suicidal", false);
		friendlyFire = loader.loadBoolean("friendly_fire", true);
		damage = loader.loadDouble("damage");
		wingsOff = loader.loadBoolean("wings_off", false);
		physicalDamage = loader.loadDouble("physical_damage");
		isExploding = loader.loadBoolean("exploding", false);
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
