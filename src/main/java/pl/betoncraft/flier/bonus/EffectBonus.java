/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.bonus;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.Flier;
import pl.betoncraft.flier.api.Effect;
import pl.betoncraft.flier.api.InGamePlayer;
import pl.betoncraft.flier.core.ValueLoader;
import pl.betoncraft.flier.exception.LoadingException;

/**
 * A Bonus type which adds a specified effect.
 *
 * @author Jakub Sapalski
 */
public class EffectBonus extends DefaultBonus {
	
	private final Effect effect;
	private final int duration;
	
	private Set<UUID> players = new HashSet<>();

	public EffectBonus(ConfigurationSection section) throws LoadingException {
		super(section);
		effect = ValueLoader.loadEffect(section, "effect");
		duration = ValueLoader.loadPositiveInt(section, "duration");
	}

	@Override
	protected boolean use(InGamePlayer player) {
		UUID uuid = player.getPlayer().getUniqueId();
		if (players.contains(uuid)) {
			return false;
		}
		players.add(uuid);
		player.addEffect(effect);
		Bukkit.getScheduler().scheduleSyncDelayedTask(Flier.getInstance(), () -> {
			player.removeEffect(effect);
			players.remove(uuid);
		}, duration);
		return true;
	}

}
