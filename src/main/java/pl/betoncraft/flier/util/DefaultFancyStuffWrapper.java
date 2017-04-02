/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.connorlinfoot.bountifulapi.BountifulAPI;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.core.FancyStuffWrapper;

/**
 * Default implementation of FancyStuffWrapper.
 *
 * @author Jakub Sapalski
 */
public class DefaultFancyStuffWrapper implements FancyStuffWrapper {
	
	private Plugin titlePlugin = Plugin.None;
	private Plugin actionBarPlugin = Plugin.None;
	private Plugin tabListPlugin = Plugin.None;
	
	private enum Plugin {
		None, BountifulAPI
	}

	public DefaultFancyStuffWrapper() {
		if (Bukkit.getPluginManager().isPluginEnabled("BountifulAPI")) {
			titlePlugin = Plugin.BountifulAPI;
			actionBarPlugin = Plugin.BountifulAPI;
			tabListPlugin = Plugin.BountifulAPI;
		}
		Flier.getInstance().getLogger().info(String.format("Using %s for displaying titles.", titlePlugin));
		Flier.getInstance().getLogger().info(String.format("Using %s for managing action bar.", actionBarPlugin));
		Flier.getInstance().getLogger().info(String.format("Using %s for setting tab list headers.", tabListPlugin));
	}
	
	@Override
	public void sendTitle(Player player, String title, String sub, int fadeIn, int stay, int fadeOut) {
		switch (titlePlugin) {
		case BountifulAPI:
			if (fadeIn + stay + fadeOut <= 0) {
				fadeIn = 20;
				stay = 100;
				fadeOut = 20;
			}
			BountifulAPI.sendTitle(player, fadeIn, stay, fadeOut, title, sub);
			break;
		case None:
			// dispatch a regular command, it will be displayed in the console
			String timing = fadeIn + stay + fadeOut <= 0 ?
					null : String.format("title %s times %d %d %d", player.getName(), fadeIn, stay, fadeOut);
			String subTitle = sub == null ?
					null : String.format("title %s subtitle {\"text\":\"%s\"}", player.getName(), sub);
			String mainTitle = String.format("title %s title {\"text\":\"%s\"}", player.getName(), title);
			if (timing != null) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), timing);
			if (subTitle != null) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), subTitle);
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), mainTitle);
			break;
		}
	}
	
	@Override
	public void sendActionBar(Player player, String message, int duration) {
		switch (actionBarPlugin) {
		case BountifulAPI:
			BountifulAPI.sendActionBar(player, message, duration);
			break;
		case None:
			// can't update action bar in Spigot
			break;
		}
	}
	
	@Override
	public void setTabList(Player player, String header, String footer) {
		switch (tabListPlugin) {
		case BountifulAPI:
			BountifulAPI.sendTabTitle(player, header, footer);
			break;
		case None:
			// can't update tab list in Spigot
			break;
		}
	}
	
}
