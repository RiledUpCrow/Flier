package pl.betoncraft.flier.integration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.api.content.Lobby;
import pl.betoncraft.flier.util.LangManager;

public class PlaceholderAPI extends PlaceholderExpansion {

	private Flier plugin;

	public PlaceholderAPI(Flier plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean persist() {
		return true;
	}

	@Override
	public boolean canRegister() {
		return true;
	}

	@Override
	public String getAuthor() {
		return plugin.getDescription().getAuthors().toString();
	}

	@Override
	public String getIdentifier() {
		return "flier";
	}

	@Override
	public String getVersion() {
		return plugin.getDescription().getVersion();
	}

	@Override
	public String onPlaceholderRequest(Player p, String identifier) {
		if(p == null)
			return null;
		try {
			return root(p, new ArrayList<>(Arrays.asList(identifier.split("\\."))));
		} catch (ArgumentException e) {
			plugin.getLogger().warning(String.format("Error in '%s' placeholder: %s", identifier, e.getMessage()));
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
		Lobby lobby = plugin.getLobbies().get(lobbyName);
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
