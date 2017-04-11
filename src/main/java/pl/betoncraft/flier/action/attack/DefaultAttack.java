/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.action.attack;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.action.DefaultAction;
import pl.betoncraft.flier.api.content.Attack;
import pl.betoncraft.flier.api.core.LoadingException;

/**
 * A default Weapon implementation.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultAttack extends DefaultAction implements Attack {

	private static final String EXPLODING = "exploding";
	private static final String FRIENDLY_FIRE = "friendly_fire";
	private static final String SUICIDAL = "suicidal";
	private static final String MIDAIR_PHYSICAL_DAMAGE = "midair_physical_damage";
	private static final String WINGS_OFF = "wings_off";
	private static final String PHYSICAL_DAMAGE = "physical_damage";
	private static final String DAMAGE = "damage";
	private static final String NO_DAMAGE_TICKS = "no_damage_ticks";

	protected final double damage;
	protected final double physicalDamage;
	protected final int noDamageTicks;
	protected final boolean wingsOff;
	protected final boolean midAirPhysicalDamage;
	protected final boolean suicidal;
	protected final boolean friendlyFire;
	protected final boolean isExploding;
	
	public DefaultAttack(ConfigurationSection section) throws LoadingException {
		super(section);
		damage = loader.loadDouble(DAMAGE);
		physicalDamage = loader.loadDouble(PHYSICAL_DAMAGE);
		noDamageTicks = loader.loadPositiveInt(NO_DAMAGE_TICKS, 20);
		wingsOff = loader.loadBoolean(WINGS_OFF, false);
		midAirPhysicalDamage = loader.loadBoolean(MIDAIR_PHYSICAL_DAMAGE, false);
		suicidal = loader.loadBoolean(SUICIDAL, false);
		friendlyFire = loader.loadBoolean(FRIENDLY_FIRE, true);
		isExploding = loader.loadBoolean(EXPLODING, false);
	}
	
	@Override
	public double getDamage() {
		return modMan.modifyNumber(DAMAGE, damage);
	}

	@Override
	public double getPhysical() {
		return modMan.modifyNumber(PHYSICAL_DAMAGE, physicalDamage);
	}
	
	@Override
	public int getNoDamageTicks() {
		return noDamageTicks;
	}
	
	@Override
	public boolean wingsOff() {
		return modMan.modifyBoolean(WINGS_OFF, wingsOff);
	}
	
	@Override
	public boolean midAirPhysicalDamage() {
		return modMan.modifyBoolean(MIDAIR_PHYSICAL_DAMAGE, midAirPhysicalDamage);
	}
	
	@Override
	public boolean friendlyFire() {
		return modMan.modifyBoolean(FRIENDLY_FIRE, friendlyFire);
	}

	@Override
	public boolean suicidal() {
		return modMan.modifyBoolean(SUICIDAL, suicidal);
	}
	
	@Override
	public boolean isExploding() {
		return modMan.modifyBoolean(EXPLODING, isExploding);
	}

}
