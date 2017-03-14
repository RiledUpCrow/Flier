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

import net.md_5.bungee.api.ChatColor;
import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Lobby;
import pl.betoncraft.flier.api.core.CommandArgument;

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
		if (it.hasNext()) {
			if (!sender.hasPermission(force)) {
				CommandArgument.noPermission(sender);
				return;
			}
			String playerName = it.next();
			player = Bukkit.getPlayer(playerName);
			if (player == null) {
				sender.sendMessage(String.format("%s%s is offline.", ChatColor.RED, playerName));
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
				lobby.getGame().addPlayer(player);
				break;
			}
		}
		if (!found) {
			if (player.equals(sender)) {
				sender.sendMessage(String.format("%sYou are not in a lobby.", ChatColor.RED));
			} else {
				sender.sendMessage(String.format("%s%s is not in a lobby.", ChatColor.RED, player.getName()));
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
			return "Force a player to start a game.";
		} else {
			if (sender.hasPermission(force)) {
				return "Start a game or force specified player to start game.";
			} else {
				return "Start a game.";
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
