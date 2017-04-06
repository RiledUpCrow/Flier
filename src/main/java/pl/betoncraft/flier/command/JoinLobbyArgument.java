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
import java.util.NoSuchElementException;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Lobby;
import pl.betoncraft.flier.api.core.CommandArgument;
import pl.betoncraft.flier.util.LangManager;

/**
 * The argument responsible for joining the lobby.
 *
 * @author Jakub Sapalski
 */
class JoinLobbyArgument implements CommandArgument {
	
	private Permission permission = new Permission("flier.player.join");
	private Permission force = new Permission("flier.admin.join");

	@Override
	public void parse(CommandSender sender, String currentCommand, Iterator<String> it) {
		Flier flier = Flier.getInstance();
		try {
			String lobbyName = it.next();
			Lobby lobby = flier.getLobbies().get(lobbyName);
			if (lobby == null) {
				CommandArgument.displayObjects(sender, "object_lobby", lobbyName, flier.getLobbies().keySet());
				return;
			}
			if (it.hasNext()) {
				if (!sender.hasPermission(force)) {
					CommandArgument.noPermission(sender);
					return;
				}
				String playerName = it.next();
				Player player = Bukkit.getPlayer(playerName);
				if (player == null) {
					LangManager.sendMessage(sender, "player_offline", playerName);
				} else {
					lobby.addPlayer(player);
				}
			} else {
				if (CommandArgument.checkUser(sender, User.PLAYER)) {
					lobby.addPlayer((Player) sender);
				} else {
					CommandArgument.wrongUser(sender);
				}
			}
		} catch (NoSuchElementException e) {
			CommandArgument.displayHelp(sender, currentCommand, this);
		}
	}

	@Override
	public String getName() {
		return "join";
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList(new String[]{"join", "j"});
	}

	@Override
	public String getDescription(CommandSender sender) {
		if (CommandArgument.checkUser(sender, User.CONSOLE)) {
			return LangManager.getMessage(sender, "join_lobby_desc_1");
		} else {
			if (sender.hasPermission(force)) {
				return LangManager.getMessage(sender, "join_lobby_desc_2");
			} else {
				return LangManager.getMessage(sender, "join_lobby_desc_3");
			}
		}
	}

	@Override
	public String getHelp(CommandSender sender) {
		if (CommandArgument.checkUser(sender, User.CONSOLE)) {
			return "<lobby> <player>";
		} else {
			if (sender.hasPermission(force)) {
				return "<lobby> [player]";
			} else {
				return "<lobby>";
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