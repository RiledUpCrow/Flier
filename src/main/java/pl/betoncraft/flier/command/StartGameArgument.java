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
import pl.betoncraft.flier.api.content.Lobby.JoinResult;
import pl.betoncraft.flier.api.core.CommandArgument;
import pl.betoncraft.flier.lobby.DefaultLobby;
import pl.betoncraft.flier.util.LangManager;

/**
 * Moves the player into the game.
 *
 * @author Jakub Sapalski
 */
public class StartGameArgument implements CommandArgument {

	private Permission permission = new Permission("flier.player.start");
	private Permission force = new Permission("flier.admin.start");

	@Override
	public void parse(CommandSender sender, String currentCommand, Iterator<String> it) {
		Flier flier = Flier.getInstance();
		Player player = null;
		String game;
		try {
			game = it.next();
		} catch (NoSuchElementException e) {
			CommandArgument.displayHelp(sender, currentCommand, this);
			return;
		}
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
				CommandArgument.displayHelp(sender, currentCommand, this);
				return;
			} else {
				player = (Player) sender;
			}
		}
		boolean found = false;
		for (Lobby lobby : flier.getLobbies().values()) {
			if (lobby.getPlayers().contains(player.getUniqueId())) {
				found = true;
				JoinResult res = lobby.joinGame(player, game);
				DefaultLobby.joinMessage(player, res);
				break;
			}
		}
		if (!found) {
			if (player.equals(sender)) {
				LangManager.sendMessage(sender, "you_not_in_lobby");
			} else {
				LangManager.sendMessage(sender, "player_not_in_lobby", player.getName());
			}
		}
	}

	@Override
	public String getName() {
		return "start";
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList(new String[]{getName(), "s"});
	}

	@Override
	public String getDescription(CommandSender sender) {
		if (CommandArgument.checkUser(sender, User.CONSOLE)) {
			return LangManager.getMessage(sender, "start_game_desc_1");
		} else {
			if (sender.hasPermission(force)) {
				return LangManager.getMessage(sender, "start_game_desc_2");
			} else {
				return LangManager.getMessage(sender, "start_game_desc_3");
			}
		}
	}

	@Override
	public String getHelp(CommandSender sender) {
		if (CommandArgument.checkUser(sender, User.CONSOLE)) {
			return "<game> <player>";
		} else {
			if (sender.hasPermission(force)) {
				return "<game> [player]";
			} else {
				return "<game>";
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
