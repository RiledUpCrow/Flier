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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import net.md_5.bungee.api.ChatColor;
import pl.betoncraft.flier.api.core.CommandArgument;
import pl.betoncraft.flier.util.PlayerBackup;

/**
 * Loads the player from the file.
 *
 * @author Jakub Sapalski
 */
public class LoadArgument implements CommandArgument {
	
	private Permission permission = new Permission("flier.dev.load");

	@Override
	public String getName() {
		return "load";
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList(new String[]{getName()});
	}

	@Override
	public String getDescription() {
		return "Loads you from the file.";
	}

	@Override
	public String getHelp() {
		return "";
	}

	@Override
	public void parse(CommandSender sender, String currentCommand, Iterator<String> it) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(String.format("%sMust be a player.", ChatColor.RED));
			return;
		}
		Player player = (Player) sender;
		if (new PlayerBackup(player).load()) {
			sender.sendMessage(String.format("%sSuccessfully loaded from a file.", ChatColor.GREEN));
		} else {
			sender.sendMessage(String.format("%sCould not load from a file.", ChatColor.RED));
		}
	}

	@Override
	public Permission getPermission() {
		return permission;
	}

	@Override
	public User getUser() {
		return User.PLAYER;
	}

}
