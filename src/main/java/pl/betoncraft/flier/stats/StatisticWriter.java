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
				Optional.ofNullable(event.getPlayer().getAttacker()).map(attacker -> attacker.getWeapon()).orElse(null),
				event.getType());
		event.setSwitched(original);
	}

}
