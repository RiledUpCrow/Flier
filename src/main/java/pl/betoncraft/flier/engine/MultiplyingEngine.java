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
package pl.betoncraft.flier.engine;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

import pl.betoncraft.flier.api.core.Item;
import pl.betoncraft.flier.api.core.LoadingException;

/**
 * Engine which multiplies speed instead of adding a fixed acceleration.
 *
 * @author Jakub Sapalski
 */
public class MultiplyingEngine extends DefaultEngine {
	
	private static final String ACCELERATION = "acceleration";
	private static final String MIN_SPEED = "min_speed";
	private static final String MAX_SPEED = "max_speed";

	private final double maxSpeed;
	private final double minSpeed;
	private final double acceleration;
	
	public MultiplyingEngine(ConfigurationSection section) throws LoadingException {
		super(section);
		maxSpeed = loader.loadNonNegativeDouble(MAX_SPEED);
		minSpeed = loader.loadNonNegativeDouble(MIN_SPEED);
		acceleration = loader.loadNonNegativeDouble(ACCELERATION);
	}
	
	@Override
	public Vector launch(Vector velocity, Vector direction) {
		double speed = velocity.length();
		if (speed > modMan.modifyNumber(MAX_SPEED, maxSpeed)) {
			speed = 0;
		} else {
			double minSpeed = modMan.modifyNumber(MIN_SPEED, this.minSpeed);
			if (speed < minSpeed) {
				speed = minSpeed;
			}
		}
		return velocity.add(direction.multiply(speed * modMan.modifyNumber(ACCELERATION, acceleration)));
	}
	
	@Override
	public boolean isSimilar(Item key) {
		if (key instanceof MultiplyingEngine && super.isSimilar(key)) {
			MultiplyingEngine engine = (MultiplyingEngine) key;
			return engine.maxFuel == maxFuel &&
					engine.maxSpeed == maxSpeed &&
					engine.acceleration == acceleration;
		}
		return false;
	}

}
