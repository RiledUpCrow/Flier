/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.effect;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;

/**
 * Makes the player glow for a specified time. 
 *
 * @author Jakub Sapalski
 */
public class GlowingEffect extends DefaultEffect {
	
	private static final String TIME = "time";

	private final int time;
	private final Flier plugin;

	private BukkitRunnable canceler;

	public GlowingEffect(ConfigurationSection section) throws LoadingException {
		super(section);
		plugin = Flier.getInstance();
		time = loader.loadPositiveInt(TIME);
	}

	@Override
	public void fire(InGamePlayer player) {
		if (canceler != null) {
			canceler.cancel();
		}
		canceler = new BukkitRunnable() {
			@Override
			public void run() {
				if (player.getPlayer().isGlowing()) {
					player.getPlayer().setGlowing(false);
				}
				canceler = null;
			}
		};
		canceler.runTaskLater(plugin, time);
		if (!player.getPlayer().isGlowing()) {
			player.getPlayer().setGlowing(true);
		}
	}

}
