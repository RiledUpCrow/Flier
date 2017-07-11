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
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import pl.betoncraft.betondb.Database;
import pl.betoncraft.betondb.MySQL;
import pl.betoncraft.betondb.SQLite;
import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.api.core.DatabaseManager;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.UsableItem;
import pl.betoncraft.flier.event.FlierPlayerKillEvent.KillType;

/**
 * Manages the database connection, table creation and queries.
 *
 * @author Jakub Sapalski
 */
public class DefaultDatabaseManager implements DatabaseManager {
	
	private boolean enabled = false;
	private Database db;
	
	public DefaultDatabaseManager() {

		// prepare required stuff
		ConfigurationSection dbSection = Flier.getInstance().getConfig().getConfigurationSection("database");
		String prefix = dbSection.getString("prefix", "flier_");
		boolean mysql = dbSection.getBoolean("mysql");

		// connect to the database
		if (mysql) {
			db = new MySQL(prefix,
					dbSection.getString("host"),
					dbSection.getString("port"),
					dbSection.getString("base"),
					dbSection.getString("user"),
					dbSection.getString("pass"));
		} else {
			db = new SQLite(Flier.getInstance().getDataFolder().getPath() + File.separator + "database.db");
		}

		// check if the connection was successful
		try {
			db.execute("SELECT 1");
		} catch (SQLException e) {
			// problem with connecting
			Logger logger = Flier.getInstance().getLogger();
			logger.warning("Could not connect to " + (mysql ? "MySQL" : "SQLite") + " database: " + e.getMessage());
			return;
		} catch (ClassNotFoundException e) {
			Logger logger = Flier.getInstance().getLogger();
			logger.warning((mysql ? "MySQL" : "SQLite") + " is not supported!");
			return;
		}

		// load queries
		ConfigurationSection queries = YamlConfiguration.loadConfiguration(
				new InputStreamReader(Flier.getInstance().getResource("queries.yml"), Charset.forName("UTF-8")));
		ConfigurationSection mysqlQueries = queries.getConfigurationSection("mysql");
		ConfigurationSection sqliteQueries = queries.getConfigurationSection("sqlite");

		try {
			// create tables
			db.execute(mysqlQueries.getString("create_kills"), sqliteQueries.getString("create_kills"));
			// register loaded statements
			for (String key : mysqlQueries.getKeys(false)) {
				db.registerStatement(key, mysqlQueries.getString(key), sqliteQueries.getString(key));
			}
		} catch (SQLException | ClassNotFoundException e) {
			// error in SQL syntax
			e.printStackTrace();
		}

		// schedule connection pinger to keep it alive
		Bukkit.getScheduler().runTaskTimerAsynchronously(
				Flier.getInstance(), () -> {
					try {
						db.query("ping", new Object[0]);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}, 60*20, 60*20);

		// everything enabled
		enabled = true;
	}
	
	@Override
	public void disconnect() {
		if (!enabled) {
			return;
		}
		try {
			db.disconnect();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void saveKill(Game game, InGamePlayer killed, InGamePlayer killer, UsableItem weapon, KillType type) {
		if (!enabled) {
			return;
		}
		db.update("add_kill", new Object[]{
				game.getUniqueNumber(), 
				killed.getPlayer().getUniqueId().toString(),
				killed.getKit().getClassName(),
				killer == null ? null : killer.getPlayer().getUniqueId().toString(),
				killer == null ? null : killer.getKit().getClassName(),
				weapon == null ? null : weapon.getID(),
				type.get(),
				game.getAttitude(killed, killer).get(),
				new Date()
		});
	}

}
