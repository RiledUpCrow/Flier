/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.effect;

import org.bukkit.Particle;
import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.core.defaults.DefaultEffect;

/**
 * Creates an explosion without damage at player's location.
 *
 * @author Jakub Sapalski
 */
public class ParticleEffect extends DefaultEffect {
	
	private static final String PARTICLE = "particle";
	private static final String AMOUNT = "amount";
	private static final String OFFSET = "offset";
	
	private Particle particle;
	private int amount;
	private float offset;

	public ParticleEffect(ConfigurationSection section) throws LoadingException {
		super(section);
		playerOnly();
		particle = loader.loadEnum(PARTICLE, Particle.class);
		amount = loader.loadPositiveInt(AMOUNT, 1);
		offset = (float) loader.loadNonNegativeDouble(OFFSET, 0.0);
	}

	@Override
	public void fire(InGamePlayer player) {
		player.getPlayer().getWorld().spawnParticle(particle, player.getPlayer().getLocation(), amount, offset, offset, offset);
	}

}
