/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.Team;

/**
 * A simple team loaded from the configuration.
 *
 * @author Jakub Sapalski
 */
public class DefaultTeam implements Team {
	
	private int score = 0;
	private String name;
	private Location spawn;
	private int index;
	private ChatColor color;
	
	public DefaultTeam(ConfigurationSection section, int index) {
		this.index = index;
		spawn = Utils.parseLocation(section.getString("location"));
		color = ChatColor.valueOf(section.getString("color", "white").toUpperCase().replace(' ', '_'));
		name = color + ChatColor.translateAlternateColorCodes('&', section.getString("name"));
	}
	
	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public int getScore() {
		return score;
	}

	@Override
	public Location getSpawn() {
		return spawn;
	}

	@Override
	public ChatColor getColor() {
		return color;
	}
	
	@Override
	public void setScore(int score) {
		this.score = score;
	}

	@Override
	public String getName() {
		return name;
	}

}
