/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.action.attack;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.action.DefaultAction;
import pl.betoncraft.flier.api.content.Attack;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.Usage;
import pl.betoncraft.flier.core.DefaultUsage;

/**
 * A default Weapon implementation.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultAttack extends DefaultAction implements Attack {

	private static final String NO_DAMAGE_TICKS = "no_damage_ticks";
	private static final String FRIENDLY_FIRE = "friendly_fire";
	private static final String SUICIDAL = "suicidal";
	private static final String ATTACK_USAGES = "attack_usages";
	
	protected final int noDamageTicks;
	protected final boolean friendlyFire;
	protected final boolean suicidal;
	protected List<Usage> subUsages = new ArrayList<>();
	
	public DefaultAttack(ConfigurationSection section) throws LoadingException {
		super(section, true, false);
		noDamageTicks = loader.loadPositiveInt(NO_DAMAGE_TICKS, 20);
		friendlyFire = loader.loadBoolean(FRIENDLY_FIRE, true);
		suicidal = loader.loadBoolean(SUICIDAL, false);
		ConfigurationSection attackUsages = section.getConfigurationSection(ATTACK_USAGES);
		if (attackUsages != null) for (String usageName : attackUsages.getKeys(false)) {
			try {
				subUsages.add(new DefaultUsage(attackUsages.getConfigurationSection(usageName)));
			} catch (LoadingException e) {
				throw (LoadingException) new LoadingException(
						String.format("Error in '%s' attack usage.", usageName)).initCause(e);
			}
		}
	}
	
	@Override
	public List<Usage> getSubUsages() {
		return subUsages;
	}
	
	@Override
	public int getNoDamageTicks() {
		return (int) modMan.modifyNumber(NO_DAMAGE_TICKS, noDamageTicks);
	}
	
	@Override
	public boolean causesFriendlyFire() {
		return modMan.modifyBoolean(FRIENDLY_FIRE, friendlyFire);
	}
	
	@Override
	public boolean isSuicidal() {
		return modMan.modifyBoolean(SUICIDAL, suicidal);
	}

}
