/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.wings;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.Item;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.util.ImmutableVector;

/**
 * Simple wings with scalable lifting force and air resistance.
 *
 * @author Jakub Sapalski
 */
public class SimpleWings extends DefaultWings {
	
	private static final String MAX_LIFT = "max_lift";
	private static final String LIFTINGFORCE = "liftingforce";
	private static final String AERODYNAMICS = "aerodynamics";

	private final double aerodynamics;
	private final double liftingForce;
	private final double maxLift;
	
	public SimpleWings(ConfigurationSection section) throws LoadingException {
		super(section);
		aerodynamics = loader.loadDouble(AERODYNAMICS, 0.0);
		liftingForce = loader.loadDouble(LIFTINGFORCE, 0.0);
		maxLift = loader.loadDouble(MAX_LIFT, 0.0);
	}
	
	@Override
	public Vector applyFlightModifications(InGamePlayer data) {
		ImmutableVector velocity = ImmutableVector.fromVector(data.getPlayer().getVelocity());
		double lift = (modMan.modifyNumber(LIFTINGFORCE, liftingForce) * velocity.length() * velocity.length() * 0.5)
				- data.getWeight();
		double maxLift = modMan.modifyNumber(MAX_LIFT, this.maxLift);
		lift = lift >= maxLift ? maxLift : lift;
		velocity = velocity.add(new ImmutableVector(0, lift, 0));
		double drag = velocity.length() * velocity.length() * 0.5 * modMan.modifyNumber(AERODYNAMICS, aerodynamics);
		ImmutableVector airResistance = velocity.normalize().multiply(drag);
		return velocity.add(airResistance).toVector();
	}
	
	@Override
	public boolean isSimilar(Item key) {
		if (key instanceof SimpleWings && super.isSimilar(key)) {
			SimpleWings wings = (SimpleWings) key;
			return wings.aerodynamics == aerodynamics &&
					wings.liftingForce == liftingForce &&
					wings.maxLift == maxLift;
		}
		return false;
	}

}
