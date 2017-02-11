/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.util;

import java.io.File;
import java.io.IOException;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import com.google.common.io.Files;

import pl.betoncraft.flier.Flier;

/**
 * Manages the backup of player's data.
 *
 * @author Jakub Sapalski
 */
public class PlayerBackup {
	
	private Player player;
	private File dataFile;
	private File backupFile;
	
	public PlayerBackup(Player player) {
		this.player = player;
		File dataDir = new File(player.getWorld().getWorldFolder(), "playerdata");
		if (!dataDir.isDirectory()) {
			dataDir.delete();
			dataDir.mkdirs();
		}
		dataFile = new File(dataDir, String.format("%s.dat", player.getUniqueId().toString()));
		File backupDir = new File(Flier.getInstance().getDataFolder(), "backup");
		if (!backupDir.isDirectory()) {
			backupDir.delete();
			backupDir.mkdirs();
		}
		backupFile = new File(backupDir, String.format("%s.dat", player.getUniqueId().toString()));
	}
	
	public boolean save() {
		player.saveData();
		try {
			Files.copy(dataFile, backupFile);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean load() {
		if (!backupFile.exists() || !dataFile.exists()) {
			return false;
		}
		try {
			Files.copy(backupFile, dataFile);
			backupFile.delete();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		player.loadData();
		player.teleport(player.getLocation()); // send teleportation packet explicitly
		GameMode gamemode = player.getGameMode();
		player.setGameMode(GameMode.ADVENTURE); // update it twice so the client displays correct gamemode
		player.setGameMode(gamemode);
		return true;
	}

}
