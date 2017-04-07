/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.bonus;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.core.defaults.DefaultBonus;

/**
 * A Bonus without physical manifestation, activated by proximity.
 *
 * @author Jakub Sapalski
 */
public class ProximityBonus extends DefaultBonus {

	protected Location location;
	protected final double distance;
	protected final String locationName;
	protected BukkitRunnable checker;

	public ProximityBonus(ConfigurationSection section) throws LoadingException {
		super(section);
		distance = Math.pow(loader.loadPositiveDouble("distance"), 2);
		locationName = loader.loadString("location");
	}
	
	public void check() {
		for (InGamePlayer player : game.getPlayers().values()) {
			if (player != null &&
					player.isPlaying() &&
					player.getPlayer().getLocation().distanceSquared(location) <= distance) {
				apply(player);
			}
		}
	}
	
	@Override
	public void setGame(Game game) throws LoadingException {
		super.setGame(game);
		location = game.getArena().getLocation(locationName);
	}
	
	@Override
	public void release() {
		super.release();
		checker = new BukkitRunnable() {
			@Override
			public void run() {
				check();
			}
		};
		checker.runTaskTimer(Flier.getInstance(), 1, 1);
	}
	
	@Override
	public void block() {
		super.block();
		checker.cancel();
	}

}
