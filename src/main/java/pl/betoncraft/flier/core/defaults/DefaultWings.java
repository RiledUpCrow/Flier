/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core.defaults;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.content.Wings;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.Modification;

/**
 * Default Wings implementation.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultWings extends DefaultItem implements Wings {

	private static final String REGENERATION = "regeneration";
	private static final String MAX_HEALTH = "max_health";

	protected final double maxHealth;
	protected final double regeneration;

	protected double health;
	protected boolean disabled;

	public DefaultWings(ConfigurationSection section) throws LoadingException {
		super(section);
		maxHealth = loader.loadPositiveDouble(MAX_HEALTH);
		regeneration = loader.loadNonNegativeDouble(REGENERATION);
		health = maxHealth;
	}

	@Override
	public double getMaxHealth() {
		return modMan.modifyNumber(MAX_HEALTH, maxHealth);
	}

	@Override
	public double getRegeneration() {
		return modMan.modifyNumber(REGENERATION, regeneration);
	}

	@Override
	public double getHealth() {
		double max = getMaxHealth();
		if (max != 0 && health > max) {
			health = max;
		}
		return health;
	}

	@Override
	public boolean addHealth(double amount) {
		double maxHealth = getMaxHealth();
		if (health >= maxHealth) {
			return false;
		}
		if (health + amount > maxHealth) {
			health = maxHealth;
		} else {
			health += amount;
		}
		return true;
	}

	@Override
	public boolean removeHealth(double amount) {
		if (health == 0) {
			return false;
		}
		if (health <= amount) {
			health = 0;
		} else {
			health -= amount;
		}
		return true;
	}

	@Override
	public boolean areDisabled() {
		return disabled;
	}

	@Override
	public void setDisabled(boolean disabled) {
		this.disabled = disabled;
	}
	
	@Override
	public void refill() {
		health = getMaxHealth();
		disabled = false;
	}
	
	@Override
	public void addModification(Modification mod) {
		modMan.addModification(mod);
	}
	
	@Override
	public void removeModification(Modification mod) {
		modMan.removeModification(mod);
	}
	
	@Override
	public void clearModifications() {
		modMan.clear();
	}

}
