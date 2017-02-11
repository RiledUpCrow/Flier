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
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import pl.betoncraft.flier.Flier;
import pl.betoncraft.flier.api.CommandArgument;
import pl.betoncraft.flier.api.Lobby;

/**
 * Moves the player out of the lobby.
 *
 * @author Jakub Sapalski
 */
class LeaveLobbyArgument implements CommandArgument {
	
	private Permission permission = new Permission("flier.player.leave");

	@Override
	public void parse(CommandSender sender, String currentCommand, Iterator<String> it) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.DARK_RED + "Must be a player!");
			return;
		}
		Flier f = Flier.getInstance();
		Player player = (Player) sender;
		for (Lobby lobby : f.getLobbies().values()) {
			lobby.removePlayer(player);
			return;
		}
	}

	@Override
	public String getName() {
		return "leave";
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList(new String[]{"leave", "l"});
	}

	@Override
	public String getDescription() {
		return "Leave current lobby.";
	}

	@Override
	public String getHelp() {
		return "";
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