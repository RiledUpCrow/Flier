/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Represents a team in the game.
 *
 * @author Jakub Sapalski
 */
public class Team {
	
	private int score = 0;
	private String name;
	private Location spawn;
	private int index;
	private ChatColor color;
	
	public Team(ConfigurationSection section, int index) {
		this.index = index;
		String[] parts = section.getString("location").split(";");
		spawn = new Location(Bukkit.getWorld(parts[3]),
				Double.parseDouble(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]));
		color = ChatColor.valueOf(section.getString("color", "white").toUpperCase().replace(' ', '_'));
		name = color + ChatColor.translateAlternateColorCodes('&', section.getString("name"));
	}
	
	public int getIndex() {
		return index;
	}

	/**
	 * @return the score
	 */
	public int getScore() {
		return score;
	}
	
	public void setScore(int score) {
		this.score = score;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the spawn
	 */
	public Location getSpawn() {
		return spawn;
	}

	/**
	 * @return the color
	 */
	public ChatColor getColor() {
		return color;
	}

}
