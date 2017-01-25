/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.bonus;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.InGamePlayer;
import pl.betoncraft.flier.api.Replicable;
import pl.betoncraft.flier.core.DefaultBonus;
import pl.betoncraft.flier.exception.LoadingException;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * Adds or removes money from player.
 *
 * @author Jakub Sapalski
 */
public class MoneyBonus extends DefaultBonus {
	
	private final int money;

	public MoneyBonus(ConfigurationSection section) throws LoadingException {
		super(section);
		money = ValueLoader.loadInt(section, "money");
	}

	@Override
	public Replicable replicate() {
		try {
			return new MoneyBonus(base);
		} catch (LoadingException e) {
			return null; // dead code
		}
	}

	@Override
	protected boolean use(InGamePlayer player) {
		player.setMoney(player.getMoney() + money);
		return true;
	}

}
