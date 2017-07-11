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