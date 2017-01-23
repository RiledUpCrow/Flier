/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.betoncraft.flier.Flier;
import pl.betoncraft.flier.api.Game;
import pl.betoncraft.flier.api.Lobby;

/**
 * The main command.
 *
 * @author Jakub Sapalski
 */
public class FlierCommand implements CommandExecutor {
	
	private List<Argument> arguments = new ArrayList<>();
	
	public FlierCommand() {
		
		// JOIN GAME ARGUMENT
		arguments.add(new Argument(new String[]{"join", "j"}, "Join a lobby.", "<lobby>", new Argument[]{}) {
			@Override
			void parse(CommandSender sender, String currentCommand, Iterator<String> it) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.DARK_RED + "Must be a player to use this argument!");
					return;
				}
				Flier f = Flier.getInstance();
				try {
					String lobbyName = it.next();
					Lobby lobby = f.getLobbies().get(lobbyName);
					if (lobby == null) {
						displayObjects(sender, "lobby", lobbyName, f.getLobbies().keySet());
						return;
					}
					lobby.addPlayer((Player) sender);
				} catch (NoSuchElementException e) {
					displayHelp(sender, currentCommand, this);
				}
			}
		});
		
		// LEAVE GAME ARGUMENT
		arguments.add(new Argument(new String[]{"leave", "l"}, "Leave current lobby.", "", new Argument[]{}) {
			@Override
			void parse(CommandSender sender, String currentCommand, Iterator<String> it) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.DARK_RED + "Must be a player!");
					return;
				}
				Flier f = Flier.getInstance();
				Player player = (Player) sender;
				for (Lobby lobby : f.getLobbies().values()) {
					lobby.removePlayer(player);
					return;
				}
			}
		});
		
		// SET GAME ARGUMENT
		arguments.add(new Argument(new String[]{"setgame", "s"}, "Set current game.", "<lobby> <game>", new Argument[]{}) {
			@Override
			void parse(CommandSender sender, String currentCommand, Iterator<String> it) {
				Flier f = Flier.getInstance();
				try {
					String lobbyName = it.next();
					String gameName = it.next();
					Lobby lobby = f.getLobbies().get(lobbyName);
					if (lobby == null) {
						displayObjects(sender, "lobby", lobbyName, f.getLobbies().keySet());
						return;
					}
					Game game = lobby.getGames().get(gameName);
					if (game == null) {
						displayObjects(sender, "game", gameName, lobby.getGames().keySet());
						return;
					}
					lobby.setGame(game);
				} catch (NoSuchElementException e) {
					displayHelp(sender, currentCommand, this);
				}
			}
		});
		
		// RELOAD
		arguments.add(new Argument(new String[]{"reload"}, "Reload the plugin", "", new Argument[]{}) {
			@Override
			void parse(CommandSender sender, String currentCommand, Iterator<String> it) {
				Flier f = Flier.getInstance();
				f.onDisable();
				f.onEnable();
				sender.sendMessage(ChatColor.DARK_GREEN + "Reloaded!");
			}
		});
		
		Flier.getInstance().getCommand("flier").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equals("flier")) {
			Iterator<String> it = Arrays.asList(args).iterator();
			if (!it.hasNext()) {
				displayHelp(sender, arguments);
			} else {
				manageIterator(sender, label, it, it.next(), arguments);
			}
			return true;
		}
		return false;
	}
	
	private void manageIterator(CommandSender sender, String currentCommand, Iterator<String> it, String name,
			List<Argument> arguments) {
		for (Argument a : arguments) {
			if (a.aliases().contains(name)) {
				a.parse(sender, currentCommand + " " + name, it);
				return;
			}
		}
		displayHelp(sender, arguments);
	}
	
	private void displayHelp(CommandSender sender, String currentCommand, Argument argument) {
		sender.sendMessage(ChatColor.RED + "Wrong use of a command. Correct syntax:");
		sender.sendMessage(ChatColor.DARK_GREEN + "/" + currentCommand + " " + argument.help());
	}
	
	private void displayHelp(CommandSender sender, List<Argument> arguments) {
		sender.sendMessage(ChatColor.RED + "Available arguments:");
		for (Argument a : arguments) {
			String aliases;
			if (a.aliases.size() > 1) {
				StringBuilder builder = new StringBuilder();
				for (String alias : a.aliases().subList(1, a.aliases().size())) {
					builder.append(ChatColor.LIGHT_PURPLE + alias + ChatColor.WHITE + ", ");
				}
				aliases = ChatColor.WHITE + "(" + builder.toString().trim().substring(0, builder.lastIndexOf(","))
						+ ChatColor.WHITE + ")";
			} else {
				aliases = "";
			}
			sender.sendMessage(ChatColor.YELLOW + "- " + ChatColor.DARK_AQUA + a.name() + " " + aliases
					+ ChatColor.YELLOW + ": " + ChatColor.GREEN + a.description());
		}
	}

	private void displayObjects(CommandSender sender, String type, String name, Set<String> available) {
		sender.sendMessage(ChatColor.RED + "No such " + type + ": " + ChatColor.DARK_RED + name);
		StringBuilder builder = new StringBuilder();
		for (String g : available) {
			builder.append(ChatColor.YELLOW + g + ChatColor.GREEN + ", ");
		}
		sender.sendMessage(ChatColor.GREEN + "Available names: "
				+ builder.toString().trim().substring(0, builder.lastIndexOf(",")));
	}
	
	private abstract class Argument {
		
		String name;
		List<String> aliases;
		String description;
		String help;
		List<Argument> arguments;
		
		Argument(String[] aliases, String description, String help, Argument[] arguments) {
			name = aliases[0];
			this.aliases = Arrays.asList(aliases);
			this.description = description;
			this.help = help;
			this.arguments = Arrays.asList(arguments);
		}
		
		String name() {
			return name;
		}
		
		List<String> aliases() {
			return aliases;
		}
		
		String description() {
			return description;
		}
		
		String help() {
			return help;
		}
		
		abstract void parse(CommandSender sender, String currentCommand, Iterator<String> it);
		
	}

}
