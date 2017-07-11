/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.action.attack;

import java.util.Optional;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.UsableItem;
import pl.betoncraft.flier.core.DefaultAttacker;

/**
 * Creates an explosion around the target. All players caught in the explosion's radius will be affected.
 *
 * @author Jakub Sapalski
 */
public class Explosion extends DefaultAttack {
	
	private final static String RADIUS = "radius";
	
	private final double radius;

	public Explosion(ConfigurationSection section) throws LoadingException {
		super(section);
		radius = loader.loadPositiveDouble(RADIUS);
	}

	@Override
	public boolean act(Optional<InGamePlayer> creator, Optional<InGamePlayer> source, InGamePlayer target,
			Optional<UsableItem> item) {
		System.out.println("Exploding at " + target.getName() + " location");
		double radius = modMan.modifyNumber(RADIUS, this.radius);
		target.getGame().getTargets().values().stream()
				.filter(t -> t.getLocation().distanceSquared(target.getLocation()) <= radius*radius)
				.forEach(t -> t.handleHit(new DefaultAttacker(this, creator.orElse(null), target, item.orElse(null))));
		return true;
	}

}
