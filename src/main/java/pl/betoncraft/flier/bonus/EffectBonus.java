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
import pl.betoncraft.flier.exception.LoadingException;
import pl.betoncraft.flier.exception.ObjectUndefinedException;
import pl.betoncraft.flier.exception.TypeUndefinedException;

/**
 * A Bonus type which adds a specified effect.
 *
 * @author Jakub Sapalski
 */
public class EffectBonus extends DefaultBonus {
	
	private Effect effect;
	private int duration = 20 * 10;
	private Set<UUID> players = new HashSet<>();

	public EffectBonus(ConfigurationSection section) throws LoadingException {
		super(section);
		String effectName = section.getString("effect");
		try {
			effect = Flier.getInstance().getEffect(effectName);
		} catch (ObjectUndefinedException | TypeUndefinedException | LoadingException e) {
			throw (LoadingException) new LoadingException(String.format("Error in '%s' effect.", effectName))
					.initCause(e);
		}
		duration = section.getInt("duration", duration);
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
