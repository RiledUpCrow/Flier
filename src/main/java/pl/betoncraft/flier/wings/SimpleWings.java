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
