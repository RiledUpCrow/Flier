/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.action.attack;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;

import pl.betoncraft.flier.api.InGamePlayer;
import pl.betoncraft.flier.api.LoadingException;
import pl.betoncraft.flier.core.defaults.DefaultAttack;
import pl.betoncraft.flier.util.Utils;

/**
 * Spawns a TNT which explodes immediately.
 *
 * @author Jakub Sapalski
 */
public class AutoDestruction extends DefaultAttack {
	
	private final float yield;

	public AutoDestruction(ConfigurationSection section) throws LoadingException {
		super(section);
		yield = (float) loader.loadPositiveDouble("power");
	}

	@Override
	public boolean act(InGamePlayer player) {
		TNTPrimed tnt = (TNTPrimed) player.getPlayer().getWorld().spawnEntity(
				player.getPlayer().getLocation(), EntityType.PRIMED_TNT);
		Utils.saveDamager(tnt, this, player);
		tnt.setIsIncendiary(false);
		tnt.setYield(yield);
		tnt.setGravity(false);
		tnt.setFuseTicks(0);
		return true;
	}

}
