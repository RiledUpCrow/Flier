/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.stats;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.core.DatabaseManager;
import pl.betoncraft.flier.event.FlierPlayerKillEvent;

/**
 * Writes various statistics into the database.
 *
 * @author Jakub Sapalski
 */
public class StatisticWriter implements Listener {
	
	public StatisticWriter(DatabaseManager dbManager) {
		Bukkit.getPluginManager().registerEvents(this, Flier.getInstance());
	}
	
	@EventHandler
	public void onKill(FlierPlayerKillEvent event) {
		boolean original = event.isSwitched();
		event.setSwitched(false);
		Flier.getInstance().getDatabaseManager().saveKill(
				event.getGame(),
				event.getPlayer(),
				event.getOther(),
				Optional.ofNullable(event.getPlayer().getAttacker()).map(attacker -> attacker.getWeapon()).orElse(null));
		event.setSwitched(original);
	}

}
