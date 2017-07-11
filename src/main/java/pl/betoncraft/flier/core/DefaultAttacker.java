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
	private InGamePlayer creator;
	private InGamePlayer source;
	private UsableItem weapon;

	public DefaultAttacker(Damager damager, InGamePlayer creator, InGamePlayer source, UsableItem weapon) {
		this.damager = damager;
		this.creator = creator;
		this.source = source;
		this.weapon = weapon;
	}

	@Override
	public Damager getDamager() {
		return damager;
	}

	@Override
	public InGamePlayer getCreator() {
		return creator;
	}

	@Override
	public InGamePlayer getSource() {
		return source;
	}
	
	@Override
	public UsableItem getWeapon() {
		return weapon;
	}
}