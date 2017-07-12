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
package pl.betoncraft.flier.action.attack;

import java.util.Optional;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.Owner;
import pl.betoncraft.flier.core.DefaultAttacker;

/**
 * Creates an explosion around the target. All players caught in the explosion's radius will be affected.
 *
 * @author Jakub Sapalski
 */
public class Explosion extends DefaultAttack {
	
	private final static String RADIUS = "radius";
	
	private final double radius;

	public Explosion(ConfigurationSection section, Optional<Owner> owner) throws LoadingException {
		super(section, owner);
		radius = loader.loadPositiveDouble(RADIUS);
	}

	@Override
	public boolean act(InGamePlayer target, InGamePlayer source) {
		System.out.println("Exploding at " + target.getName() + " location");
		double radius = modMan.modifyNumber(RADIUS, this.radius);
		target.getGame().getTargets().values().stream()
				.filter(t -> t.getLocation().distanceSquared(target.getLocation()) <= radius*radius)
				.forEach(t -> t.handleHit(new DefaultAttacker(this, owner.get().getPlayer(), target, owner.get().getItem())));
		return true;
	}

}
