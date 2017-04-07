/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.bonus;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

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
public class ProximityBonus extends DefaultBonus implements Listener {

	protected Location location;
	protected final double distance;
	protected final String locationName;

	public ProximityBonus(ConfigurationSection section) throws LoadingException {
		super(section);
		distance = Math.pow(loader.loadPositiveDouble("distance"), 2);
		locationName = loader.loadString("location");
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onMove(PlayerMoveEvent event) {
		InGamePlayer player = game.getPlayers().get(event.getPlayer().getUniqueId());
		if (player != null && player.isPlaying() && event.getTo().distanceSquared(location) <= distance) {
			apply(player);
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
		Bukkit.getPluginManager().registerEvents(this, Flier.getInstance());
	}
	
	@Override
	public void block() {
		super.block();
		HandlerList.unregisterAll(this);
	}

}
