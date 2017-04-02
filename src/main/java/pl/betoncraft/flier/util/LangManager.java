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
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.IllegalFormatException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import pl.betoncraft.betonlangapi.BetonLangAPI;
import pl.betoncraft.betonlangapi.TranslatedPlugin;
import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;

/**
 * Manages languages.
 *
 * @author Jakub Sapalski
 */
public class LangManager {

	private static LangManager instance;
	private boolean api = false;
	private String lang;
	private ConfigurationSection messages;
	private Flier flier = Flier.getInstance();

	/**
	 * Creates new language manager. Needs to be reloaded before use.
	 * 
	 * @throws LoadingException
	 */
	public LangManager() {
		instance = this;
		if (Bukkit.getPluginManager().isPluginEnabled("BetonLangAPI")) {
			BetonLangAPI.registerPlugin(flier, new TranslatedPlugin(flier, "en"));
			api = true;
		}
	}
	
	/**
	 * Reloads the messages.
	 */
	public static void reload() throws LoadingException  {
		File file = new File(instance.flier.getDataFolder(), "messages.yml");
		if (!file.exists()) {
			try {
				file.createNewFile();
				FileOutputStream out = new FileOutputStream(file);
				InputStream in = Flier.getInstance().getResource("messages.yml");
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
		} else {
			// update new strings
			YamlConfiguration def = YamlConfiguration.loadConfiguration(
					new InputStreamReader(Flier.getInstance().getResource("messages.yml"), Charset.forName("UTF-8")));
			YamlConfiguration cur = YamlConfiguration.loadConfiguration(file);
			boolean changed = false;
			for (String key : def.getKeys(true)) {
				if (!cur.contains(key)) {
					changed = true;
					cur.set(key, def.get(key));
				}
			}
			if (changed) {
				try {
					cur.save(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		if (instance.api) {
			BetonLangAPI.reloadMessages(instance.flier);
		} else {
			instance.lang = instance.flier.getConfig().getString("language", "en");
			instance.messages = YamlConfiguration.loadConfiguration(file).getConfigurationSection(instance.lang);
		}
	}
	
	/**
	 * Gets the language used by the CommandSender.
	 * 
	 * @param player
	 *            CommandSender
	 * @return the language used
	 */
	public static String getLanguage(CommandSender player) {
		if (instance.api) {
			return player instanceof Player ? BetonLangAPI.getLanguage((Player) player) : instance.lang;
		} else {
			return instance.lang;
		}
	}

	/**
	 * Returns the message in the language this player uses in the Game.
	 * 
	 * @param player
	 *            player for whom the message needs to be translated
	 * @param message
	 *            message name
	 * @param variables
	 *            array of variables
	 * @return the message string
	 */
	public static String getMessage(InGamePlayer player, String message, Object... variables) {
		return getMessage(player.getLanguage(), message, variables);
	}
	
	/**
	 * Returns the message in this player's language.
	 * 
	 * @param player
	 *            CommandSender for whom the message needs to be translated
	 * @param message
	 *            message name
	 * @param variables
	 *            array of variables
	 * @return the message string
	 */
	public static String getMessage(CommandSender player, String message, Object... variables) {
		return getMessage(getLanguage(player), message, variables);
	}
	
	/**
	 * Returns the message in specified language.
	 * 
	 * @param lang
	 *            language to which the message needs to be translated
	 * @param message
	 *            message name
	 * @param variables
	 *            array of variables
	 * @return the message string
	 */
	public static String getMessage(String lang, String message, Object... variables) {
		String string;
		if (instance.api) {
			string = BetonLangAPI.getMessage(lang, instance.flier, message);
		} else {
			string = instance.messages.getString(message);
		}
		if (string == null) {
			instance.flier.getLogger()
					.warning(String.format("Message '%s' in language '%s' is not defined.", message, lang));
			return "";
		}
		try {
			return String.format(string, variables).replace('&', '§');
		} catch (IllegalFormatException e) {
			instance.flier.getLogger().warning(String.format("Error in '%s' message ('%s' language) formatting: %s",
					message, lang, e.getMessage()));
			return "";
		}
	}

	/**
	 * Sends specified message to specified player, optionally inserting
	 * variables.
	 * 
	 * @param player
	 *            the player to whom the message will be sent
	 * @param message
	 *            message name
	 * @param variables
	 *            array of variables
	 */
	public static void sendMessage(InGamePlayer player, String message, Object... variables) {
		send(player.getPlayer(), getMessage(player, message, variables));
	}
	
	/**
	 * Sends specified message to specified CommandSender, optionally inserting
	 * variables.
	 * 
	 * @param player
	 *            the CommandSender to whom the message will be sent
	 * @param message
	 *            message name
	 * @param variables
	 *            array of variables
	 */
	public static void sendMessage(CommandSender player, String message, Object... variables) {
		send(player, getMessage(player, message, variables));
	}
	
	private static void send(CommandSender player, String translated) {
		if (translated != null && !translated.isEmpty()) {
			player.sendMessage(translated);
		}
	}

}
