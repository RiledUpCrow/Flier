/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.item;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import pl.betoncraft.flier.Flier;
import pl.betoncraft.flier.api.InGamePlayer;
import pl.betoncraft.flier.core.ValueLoader;
import pl.betoncraft.flier.exception.LoadingException;

/**
 * Launches players in the direction of looking.
 *
 * @author Jakub Sapalski
 */
public class Launcher extends DefaultUsableItem {
	
	private final double speed;

	public Launcher(ConfigurationSection section) throws LoadingException {
		super(section);
		speed = ValueLoader.loadPositiveDouble(section, "speed");
	}

	@Override
	public boolean use(InGamePlayer player) {
		Vector vel = player.getPlayer().getLocation().getDirection().multiply(speed);
		player.getPlayer().setVelocity(vel);
		if (!player.getPlayer().isGliding()) {
			new BukkitRunnable() {
				@Override
				public void run() {
					player.getPlayer().setGliding(true);
					player.getPlayer().setVelocity(vel);
				}
			}.runTask(Flier.getInstance());
		}
		return true;
	}

}
