/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.core.ConfigManager;

/**
 * Manages different configuration files.
 *
 * @author Jakub Sapalski
 */
public class DefaultConfigManager implements ConfigManager {

	private static final String LOBBIES = "lobbies.yml",
	                            GAMES = "games.yml",
	                            BONUSES = "bonuses.yml",
	                            ENGINES = "engines.yml",
	                            WINGS = "wings.yml",
	                            ITEMS = "items.yml",
	                            ACTIONS = "actions.yml",
	                            ACTIVATORS = "activators.yml",
	                            MODIFICATIONS = "modifications.yml",
	                            ITEM_SETS = "sets.yml",
	                            EFFECTS = "effects.yml",
	                            ARENAS = "arenas.yml";

	private Config lobbies;
	private Config games;
	private Config bonuses;
	private Config engines;
	private Config wings;
	private Config items;
	private Config actions;
	private Config activators;
	private Config modifications;
	private Config itemSets;
	private Config effects;
	private Config arenas;

	public DefaultConfigManager() {
		lobbies = new Config(LOBBIES);
		games = new Config(GAMES);
		bonuses = new Config(BONUSES);
		engines = new Config(ENGINES);
		wings = new Config(WINGS);
		items = new Config(ITEMS);
		actions = new Config(ACTIONS);
		activators = new Config(ACTIVATORS);
		modifications = new Config(MODIFICATIONS);
		itemSets = new Config(ITEM_SETS);
		effects = new Config(EFFECTS);
		arenas = new Config(ARENAS);
	}
	
	private class Config {
		
		File file;
		FileConfiguration config;
		
		Config(String name) {
			file = new File(Flier.getInstance().getDataFolder(), name);
			if (!file.exists()) {
				try {
					file.createNewFile();
					FileOutputStream out = new FileOutputStream(file);
					InputStream in = Flier.getInstance().getResource(name);
					byte[] buf = new byte[1024*1024];
					int len = 0;
					while ((len = in.read(buf)) > 0) {
						out.write(buf, 0, len);
					}
					in.close();
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			config = YamlConfiguration.loadConfiguration(file);
		}
		
	}
	
	@Override
	public FileConfiguration getLobbies() {
		return lobbies.config;
	}
	
	@Override
	public FileConfiguration getGames() {
		return games.config;
	}
	
	@Override
	public FileConfiguration getBonuses() {
		return bonuses.config;
	}
	
	@Override
	public FileConfiguration getEngines() {
		return engines.config;
	}
	
	@Override
	public FileConfiguration getWings() {
		return wings.config;
	}
	
	@Override
	public FileConfiguration getItems() {
		return items.config;
	}
	
	@Override
	public FileConfiguration getActions() {
		return actions.config;
	}
	
	@Override
	public FileConfiguration getActivators() {
		return activators.config;
	}

	@Override
	public FileConfiguration getModifications() {
		return modifications.config;
	}
	
	@Override
	public FileConfiguration getItemSets() {
		return itemSets.config;
	}

	@Override
	public FileConfiguration getEffects() {
		return effects.config;
	}

	@Override
	public FileConfiguration getArenas() {
		return arenas.config;
	}
	
	@Override
	public void saveLobbies() {
		try {
			lobbies.config.save(lobbies.file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void saveGames() {
		try {
			games.config.save(games.file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void saveBonuses() {
		try {
			bonuses.config.save(bonuses.file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void saveEngines() {
		try {
			engines.config.save(engines.file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void saveWings() {
		try {
			wings.config.save(wings.file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void saveItems() {
		try {
			items.config.save(items.file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void saveActions() {
		try {
			actions.config.save(actions.file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void saveActivators() {
		try {
			activators.config.save(activators.file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveModifications() {
		try {
			modifications.config.save(modifications.file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void saveItemSets() {
		try {
			itemSets.config.save(itemSets.file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveEffects() {
		try {
			effects.config.save(effects.file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void saveArenas() {
		try {
			arenas.config.save(arenas.file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
