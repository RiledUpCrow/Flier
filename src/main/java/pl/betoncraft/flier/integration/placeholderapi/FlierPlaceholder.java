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
package pl.betoncraft.flier.integration.placeholderapi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import me.clip.placeholderapi.external.EZPlaceholderHook;
import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.api.content.Lobby;
import pl.betoncraft.flier.util.LangManager;

/**
 * Flier placeholder for PlaceholderAPI.
 *
 * @author Jakub Sapalski
 */
public class FlierPlaceholder extends EZPlaceholderHook {
	
	private Flier flier;

	public FlierPlaceholder(Plugin plugin, String identifier) {
		super(plugin, identifier);
		flier = Flier.getInstance();
	}

	@Override
	public String onPlaceholderRequest(Player player, String identifier) {
		List<String> args = new ArrayList<>(Arrays.asList(identifier.split("\\.")));
		try {
			return root(player, args);
		} catch (ArgumentException e) {
			flier.getLogger().warning(String.format("Error in '%s' placeholder: %s", identifier, e.getMessage()));
			return "error";
		}
	}
	
	private String next(List<String> args) throws ArgumentException {
		// get the next argument or throw an exception
		if (args.isEmpty()) {
			throw new ArgumentException("Not enough arguments.");
		}
		return args.remove(0);
	}
	
	private String root(Player player, List<String> args) throws ArgumentException {
		String arg = next(args);
		switch (arg.toLowerCase()) {
		case "game":
			return game(player, args);
		default:
			throw new ArgumentException("Unknown argument: " + arg);
		}
	}
	
	private String game(Player player, List<String> args) throws ArgumentException {
		String lobbyName = next(args);
		String gameName = next(args);
		String gameNumber = next(args);
		Lobby lobby = flier.getLobbies().get(lobbyName);
		if (lobby == null) {
			throw new ArgumentException(String.format("Lobby '%s' does not exist.", lobbyName));
		}
		int index;
		try {
			index = Integer.parseInt(gameNumber);
		} catch (NumberFormatException e) {
			throw new ArgumentException("Cannot parse game number: " + gameNumber);
		}
		Game game = null;
		List<Game> games = lobby.getGames().get(gameName);
		if (games != null) {
			game = index < games.size() ? games.get(index) : null;
		}
		String arg = next(args);
		switch (arg.toLowerCase()) {
		case "name":
			return gameName(player, game);
		case "arena":
			return gameArena(player, game);
		case "players":
			return gamePlayers(game);
		case "locked":
			return gameLocked(player, game);
		default:
			throw new ArgumentException("Unknown argument: " + arg);
		}
	}
	
	private String gameName(Player player, Game game) {
		return game == null ? "" : game.getName(player);
	}
	
	private String gameArena(Player player, Game game) {
		return game == null ? "" : game.getArena().getName(player);
	}
	
	private String gamePlayers(Game game) {
		return game == null ? "" : Integer.toString(game.getPlayers().size());
	}
	
	private String gameLocked(Player player, Game game) {
		if (game == null) {
			return "";
		} else {
			String message = game.isLocked() ? "game_closed" : "game_open";
			return LangManager.getMessage(player, message);
		}
	}
	
	private class ArgumentException extends Exception {
		
		private static final long serialVersionUID = -4773876703700206485L;

		public ArgumentException(String message) {
			super(message);
		}
		
	}

}
