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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import pl.betoncraft.flier.Flier;
import pl.betoncraft.flier.api.CommandArgument;
import pl.betoncraft.flier.api.Lobby;

/**
 * The argument responsible for joining the lobby.
 *
 * @author Jakub Sapalski
 */
class JoinLobbyArgument implements CommandArgument {
	
	private Permission permission = new Permission("flier.player.join");

	@Override
	public void parse(CommandSender sender, String currentCommand, Iterator<String> it) {
		try {
			String lobbyName = it.next();
			Lobby lobby = Flier.getInstance().getLobbies().get(lobbyName);
			if (lobby == null) {
				CommandArgument.displayObjects(sender, "lobby", lobbyName, Flier.getInstance().getLobbies().keySet());
				return;
			}
			lobby.addPlayer((Player) sender);
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
	public String getDescription() {
		return "Join a lobby.";
	}

	@Override
	public String getHelp() {
		return "<lobby>";
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