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
package pl.betoncraft.flier.util;

import java.io.File;
import java.io.IOException;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import com.google.common.io.Files;

import pl.betoncraft.flier.api.Flier;

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
		Utils.clearPlayer(player);
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
