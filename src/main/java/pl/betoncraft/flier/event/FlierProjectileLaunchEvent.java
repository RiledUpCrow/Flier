/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.event;

import pl.betoncraft.flier.api.content.Attack;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.core.MatchingPlayerEvent;

/**
 * Called when a Flier weapon launches a projectile. This includes ParticleGun
 * and ProjectileGun. Other Attacks are not included because they launch a
 * single projectile per use, the FlierUseEvent covers that.
 *
 * @author Jakub Sapalski
 */
public class FlierProjectileLaunchEvent extends MatchingPlayerEvent {
	
	private static final String ATTACK = "attack";
	
	private final Attack attack;

	public FlierProjectileLaunchEvent(InGamePlayer player, Attack attack) {
		super(player);
		this.attack = attack;
		setString(ATTACK, attack.getID());
	}
	
	public Attack getAttack() {
		return attack;
	}

}
