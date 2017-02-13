/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.wings;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.InGamePlayer;
import pl.betoncraft.flier.core.defaults.DefaultWings;
import pl.betoncraft.flier.exception.LoadingException;
import pl.betoncraft.flier.util.Utils.ImmutableVector;

/**
 * Simple wings with scalable lifting force and air resistance.
 *
 * @author Jakub Sapalski
 */
public class SimpleWings extends DefaultWings {
	
	private final double aerodynamics;
	private final double liftingForce;
	private final double maxLift;
	
	public SimpleWings(ConfigurationSection section) throws LoadingException {
		super(section);
		aerodynamics = loader.loadDouble("aerodynamics", 0.0);
		liftingForce = loader.loadDouble("liftingforce", 0.0);
		maxLift = loader.loadDouble("maxLift");
	}
	
	@Override
	public ImmutableVector applyFlightModifications(InGamePlayer data) {
		ImmutableVector velocity = ImmutableVector.fromVector(data.getPlayer().getVelocity());
		double lift = (liftingForce * velocity.length() * velocity.length() * 0.5) - data.getWeight();
		lift = lift >= maxLift ? maxLift : lift;
		velocity = velocity.add(new ImmutableVector(0, lift, 0));
		double drag = velocity.length() * velocity.length() * 0.5 * aerodynamics;
		ImmutableVector airResistance = velocity.normalize().multiply(drag);
		return velocity.add(airResistance);
	}

}
