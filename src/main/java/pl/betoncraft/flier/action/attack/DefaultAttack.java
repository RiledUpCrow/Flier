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
	private static final String FINAL_HIT = "final";
	private static final String ATTACK_USAGES = "attack_usages";
	
	protected final int noDamageTicks;
	protected final boolean friendlyFire;
	protected final boolean suicidal;
	protected final boolean finalHit;
	protected List<Usage> subUsages = new ArrayList<>();
	
	public DefaultAttack(ConfigurationSection section) throws LoadingException {
		super(section, true, false);
		noDamageTicks = loader.loadPositiveInt(NO_DAMAGE_TICKS, 20);
		friendlyFire = loader.loadBoolean(FRIENDLY_FIRE, true);
		suicidal = loader.loadBoolean(SUICIDAL, false);
		finalHit = loader.loadBoolean(FINAL_HIT, true);
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
	
	@Override
	public boolean isFinalHit() {
		return modMan.modifyBoolean(FINAL_HIT, finalHit);
	}

}
