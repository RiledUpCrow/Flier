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

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.UsableItem;

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
		if (item == null || item.getMaxAmmo() == 0) {
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
