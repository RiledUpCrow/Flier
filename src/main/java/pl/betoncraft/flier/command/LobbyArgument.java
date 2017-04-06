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
import org.bukkit.permissions.Permission;

import pl.betoncraft.flier.api.core.CommandArgument;
import pl.betoncraft.flier.util.LangManager;

/**
 * Contains lobby-related commands.
 *
 * @author Jakub Sapalski
 */
public class LobbyArgument implements CommandArgument {
	
	private List<CommandArgument> arguments;
	private Permission permission = new Permission("flier.player.lobby");

	public LobbyArgument() {
		arguments = Arrays.asList(new CommandArgument[]{ 
				new JoinLobbyArgument(),
				new LeaveLobbyArgument(),
				new ChooseItemArgument(),
				new StartGameArgument()
		});
	}

	@Override
	public void parse(CommandSender sender, String currentCommand, Iterator<String> it) {
		if (!it.hasNext()) {
			CommandArgument.displayHelp(sender, arguments);
		} else {
			CommandArgument.nextArgument(sender, currentCommand, it, it.next(), arguments);
		}
	}

	@Override
	public String getName() {
		return "lobby";
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList(new String[] { getName(), "l" });
	}

	@Override
	public String getDescription(CommandSender sender) {
		return LangManager.getMessage(sender, "lobby_desc");
	}

	@Override
	public String getHelp(CommandSender sender) {
		return "";
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
