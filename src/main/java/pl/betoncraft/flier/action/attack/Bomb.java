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

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.util.Utils;

/**
 * Spawns a TNT which explodes immediately.
 *
 * @author Jakub Sapalski
 */
public class Bomb extends DefaultAttack {

	private static final String POWER = "power";
	private static final String FUSE = "fuse";

	private final float yield;
	private final int fuse;

	public Bomb(ConfigurationSection section) throws LoadingException {
		super(section);
		yield = (float) loader.loadPositiveDouble(POWER);
		fuse = loader.loadNonNegativeInt(FUSE, 80);
	}

	@Override
	public boolean act(InGamePlayer player) {
		TNTPrimed tnt = (TNTPrimed) player.getPlayer().getWorld().spawnEntity(
				player.getPlayer().getLocation(), EntityType.PRIMED_TNT);
		Utils.saveDamager(tnt, this, player);
		tnt.setIsIncendiary(false);
		tnt.setVelocity(player.getPlayer().getVelocity());
		tnt.setYield((float) modMan.modifyNumber(POWER, yield));
		tnt.setFuseTicks((int) modMan.modifyNumber(FUSE, fuse));
		return true;
	}

}
