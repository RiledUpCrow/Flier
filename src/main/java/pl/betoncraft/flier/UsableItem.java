/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier;

import org.bukkit.inventory.ItemStack;

/**
 * Represents a weapon which can be fired by a player. 
 *
 * @author Jakub Sapalski
 */
public interface UsableItem {
	
	public ItemStack getItem();
	
	public void fire(PlayerData player);

	public void cooldown(PlayerData player);
	
	public boolean isConsumable();
	
	public boolean onlyAir();
	
	public int slot();

}
