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
			if (titlePlugin == Plugin.None) titlePlugin = Plugin.BountifulAPI;
			if (actionBarPlugin == Plugin.None) actionBarPlugin = Plugin.BountifulAPI;
			if (tabListPlugin == Plugin.None) tabListPlugin = Plugin.BountifulAPI;
		}
		// log integrations
		if (titlePlugin != Plugin.None) {
			Flier.getInstance().getLogger().info(String.format("Using %s for displaying titles.", titlePlugin));
		}
		if (actionBarPlugin != Plugin.None) {
			Flier.getInstance().getLogger().info(String.format("Using %s for managing action bar.", actionBarPlugin));
		}
		if (tabListPlugin != Plugin.None) {
			Flier.getInstance().getLogger().info(String.format("Using %s for setting tab list headers.", tabListPlugin));
		}
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
	public void sendActionBar(Player player, String message) {
		switch (actionBarPlugin) {
		case BountifulAPI:
			BountifulAPI.sendActionBar(player, message);
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
	
	@Override
	public boolean hasTitleHandler() {
		return titlePlugin != Plugin.None;
	}
	
	@Override
	public boolean hasActionBarHandler() {
		return actionBarPlugin != Plugin.None;
	}
	
	@Override
	public boolean hasTabListHandler() {
		return tabListPlugin != Plugin.None;
	}
	
}
