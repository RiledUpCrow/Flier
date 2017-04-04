/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.activator;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.UsableItem;
import pl.betoncraft.flier.core.defaults.DefaultActivator;

/**
 * Activates when the item has correct amount of ammunition.
 *
 * @author Jakub Sapalski
 */
public class AmmoActivator extends DefaultActivator {
	
	private int amount;
	private Type type;
	private boolean percentage = false;
	
	private enum Type {
		LESS, EQUAL, MORE
	}

	public AmmoActivator(ConfigurationSection section) throws LoadingException {
		super(section);
		String string = loader.loadString("amount");
		if (string.startsWith("<")) {
			type = Type.LESS;
			string = string.substring(1);
		} else if (string.startsWith(">")) {
			type = Type.MORE;
			string = string.substring(1);
		} else {
			type = Type.EQUAL;
		}
		if (string.endsWith("%")) {
			percentage = true;
			string = string.substring(0, string.length() - 1);
		}
		try {
			amount = Integer.parseInt(string);
			if (amount < 0) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException e) {
			throw new LoadingException("Amount must be a non-negative integer.");
		}
	}

	@Override
	public boolean isActive(InGamePlayer player, UsableItem item) {
		if (item == null) {
			return false;
		}
		int a = percentage ? (int) ((double) item.getAmmo() / (double) item.getMaxAmmo() * 100.0) : item.getAmmo();
		switch (type) {
		case LESS:
			return a < amount;
		case EQUAL:
			return a == amount;
		case MORE:
			return a > amount;
		}
		return false;
	}

}
