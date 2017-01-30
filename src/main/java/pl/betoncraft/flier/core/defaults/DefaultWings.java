/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core.defaults;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.Flier;
import pl.betoncraft.flier.api.Wings;
import pl.betoncraft.flier.exception.LoadingException;

/**
 * Default Wings implementation.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultWings extends DefaultItem implements Wings {

	protected final double maxHealth;
	protected final double regeneration;

	protected double health;
	protected boolean disabled;

	public DefaultWings(ConfigurationSection section) throws LoadingException {
		super(section);
		super.item.setType(Material.ELYTRA);
		maxHealth = loader.loadPositiveDouble("max_health");
		regeneration = loader.loadNonNegativeDouble("regeneration");
		health = maxHealth;
	}

	@Override
	public double getMaxHealth() {
		return maxHealth;
	}

	@Override
	public double getRegeneration() {
		return regeneration;
	}

	@Override
	public double getHealth() {
		return health;
	}

	@Override
	public boolean addHealth(double amount) {
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
	public Wings replicate() {
		try {
			return Flier.getInstance().getWing(id);
		} catch (LoadingException e) {
			return null; // dead code
		}
	}

}
