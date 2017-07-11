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
