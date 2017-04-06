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

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Lobby;
import pl.betoncraft.flier.api.core.CommandArgument;
import pl.betoncraft.flier.util.LangManager;

/**
 * Moves the player out of the lobby.
 *
 * @author Jakub Sapalski
 */
class LeaveLobbyArgument implements CommandArgument {

	private Permission permission = new Permission("flier.player.leave");
	private Permission force = new Permission("flier.admin.leave");

	@Override
	public void parse(CommandSender sender, String currentCommand, Iterator<String> it) {
		Flier f = Flier.getInstance();
		Player player = null;
		if (it.hasNext()) {
			if (!sender.hasPermission(force)) {
				CommandArgument.noPermission(sender);
				return;
			}
			String playerName = it.next();
			player = Bukkit.getPlayer(playerName);
			if (player == null) {
				LangManager.sendMessage(sender, "player_offline", playerName);
				return;
			}
		} else {
			if (!CommandArgument.checkUser(sender, User.PLAYER)) {
				CommandArgument.wrongUser(sender);
				return;
			} else {
				player = (Player) sender;
			}
		}
		for (Lobby lobby : f.getLobbies().values()) {
			lobby.removePlayer(player);
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
	public String getDescription(CommandSender sender) {
		if (CommandArgument.checkUser(sender, User.CONSOLE)) {
			return LangManager.getMessage(sender, "leave_lobby_desc_1");
		} else {
			if (sender.hasPermission(force)) {
				return LangManager.getMessage(sender, "leave_lobby_desc_2");
			} else {
				return LangManager.getMessage(sender, "leave_lobby_desc_3");
			}
		}
	}

	@Override
	public String getHelp(CommandSender sender) {
		if (CommandArgument.checkUser(sender, User.CONSOLE)) {
			return "<player>";
		} else {
			if (sender.hasPermission(force)) {
				return "[player]";
			} else {
				return "";
			}
		}
	}

	@Override
	public Permission getPermission() {
		return permission;
	}

	@Override
	public User getUser() {
		return User.ANYONE;
	}

}