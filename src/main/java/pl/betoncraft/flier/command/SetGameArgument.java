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

import pl.betoncraft.flier.Flier;
import pl.betoncraft.flier.api.CommandArgument;
import pl.betoncraft.flier.api.Game;
import pl.betoncraft.flier.api.Lobby;

/**
 * 
 *
 * @author Jakub Sapalski
 */
class SetGameArgument implements CommandArgument {

	@Override
	public void parse(CommandSender sender, String currentCommand, Iterator<String> it) {
		Flier f = Flier.getInstance();
		try {
			String lobbyName = it.next();
			String gameName = it.next();
			Lobby lobby = f.getLobbies().get(lobbyName);
			if (lobby == null) {
				CommandArgument.displayObjects(sender, "lobby", lobbyName, f.getLobbies().keySet());
				return;
			}
			Game game = lobby.getGames().get(gameName);
			if (game == null) {
				CommandArgument.displayObjects(sender, "game", gameName, lobby.getGames().keySet());
				return;
			}
			lobby.setGame(game);
		} catch (NoSuchElementException e) {
			CommandArgument.displayHelp(sender, currentCommand, this);
		}
	}

	@Override
	public String getName() {
		return "setgame";
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList(new String[]{"setgame", "s"});
	}

	@Override
	public String getDescription() {
		return "Set current game.";
	}

	@Override
	public String getHelp() {
		return "<lobby> <game>";
	}
}