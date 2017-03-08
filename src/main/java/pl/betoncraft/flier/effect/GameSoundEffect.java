/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.effect;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;

/**
 * Plays a sound to every player in the Game.
 *
 * @author Jakub Sapalski
 */
public class GameSoundEffect extends SoundEffect {

	public GameSoundEffect(ConfigurationSection section) throws LoadingException {
		super(section);
	}

	@Override
	public void fire(InGamePlayer player) {
		player.getLobby().getGame().getPlayers().values().forEach(
				p -> p.getPlayer().getWorld().playSound(p.getPlayer().getLocation(), sound, volume, pitch));
	}

}
