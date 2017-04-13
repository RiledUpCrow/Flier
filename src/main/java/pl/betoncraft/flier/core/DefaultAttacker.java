/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core;

import pl.betoncraft.flier.api.core.Attacker;
import pl.betoncraft.flier.api.core.Damager;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.UsableItem;

/**
 * Represents an Entity which is a Damager and was launched by InGamePlayer.
 *
 * @author Jakub Sapalski
 */
public class DefaultAttacker implements Attacker {

	private Damager damager;
	private InGamePlayer attacker;
	private UsableItem weapon;

	public DefaultAttacker(Damager damager, InGamePlayer attacker, UsableItem weapon) {
		this.damager = damager;
		this.attacker = attacker;
		this.weapon = weapon;
	}

	@Override
	public Damager getDamager() {
		return damager;
	}

	@Override
	public InGamePlayer getShooter() {
		return attacker;
	}
	
	@Override
	public UsableItem getWeapon() {
		return weapon;
	}
}