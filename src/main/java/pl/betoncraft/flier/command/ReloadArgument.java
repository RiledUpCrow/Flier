/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.command;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import pl.betoncraft.flier.Flier;
import pl.betoncraft.flier.api.CommandArgument;

/**
 * 
 *
 * @author Jakub Sapalski
 */
class ReloadArgument implements CommandArgument {

	@Override
	public void parse(CommandSender sender, String currentCommand, Iterator<String> it) {
		Flier f = Flier.getInstance();
		f.onDisable();
		f.onEnable();
		sender.sendMessage(ChatColor.DARK_GREEN + "Reloaded!");
	}

	@Override
	public String name() {
		return "reload";
	}

	@Override
	public List<String> aliases() {
		return Arrays.asList(new String[]{"reload"});
	}

	@Override
	public String description() {
		return "Reloads the plugin.";
	}

	@Override
	public String help() {
		return "";
	}
}