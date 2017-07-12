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
package pl.betoncraft.flier.activator;

import java.util.Optional;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.content.Wings;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.Owner;

/**
 * Activates if wings health is in range.
 *
 * @author Jakub Sapalski
 */
public class WingsHealthActivator extends DefaultActivator {
	
	private static final String NUMBER_TYPE = "number_type";
	private static final String MAX = "max";
	private static final String MIN = "min";

	private double min;
	private double max;
	private Type type;
	
	private enum Type {
		ABSOLUTE, PERCENTAGE
	}
	
	public WingsHealthActivator(ConfigurationSection section, Optional<Owner> owner) throws LoadingException {
		super(section, owner);
		min = loader.loadNonNegativeDouble(MIN);
		max = loader.loadNonNegativeDouble(MAX, min);
		type = loader.loadEnum(NUMBER_TYPE, Type.ABSOLUTE, Type.class);
	}

	@Override
	public boolean isActive(InGamePlayer player, InGamePlayer source) {
		Wings wings = player.getKit().getWings();
		double health = wings.getHealth();
		double maxHealth = wings.getMaxHealth();
		switch (modMan.modifyEnum(NUMBER_TYPE, type)) {
		case ABSOLUTE:
			return health >= modMan.modifyNumber(MIN, min) && health <= modMan.modifyNumber(MAX, max);
		case PERCENTAGE:
			double percentage = health / maxHealth * 100;
			return percentage >= modMan.modifyNumber(MIN, min) && percentage <= modMan.modifyNumber(MAX, max);
		}
		return false;
	}

}
