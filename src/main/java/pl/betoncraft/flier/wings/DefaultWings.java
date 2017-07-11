/**
 * Copyright (c) 2017 Jakub Sapalski
 * 
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 * 
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 * 
 */
package pl.betoncraft.flier.wings;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.content.Wings;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.Modification;
import pl.betoncraft.flier.core.DefaultItem;

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
