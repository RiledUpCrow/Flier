/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.action;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.core.defaults.DefaultAction;

/**
 * Adds or removes money from player.
 *
 * @author Jakub Sapalski
 */
public class MoneyAction extends DefaultAction {

	private static final String MONEY = "money";

	private final int money;

	public MoneyAction(ConfigurationSection section) throws LoadingException {
		super(section);
		money = loader.loadInt(MONEY);
	}

	@Override
	public boolean act(InGamePlayer player) {
		player.setMoney(player.getMoney() + (int) modMan.modifyNumber(MONEY, money));
		return true;
	}

}
