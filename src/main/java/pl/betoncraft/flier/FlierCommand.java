/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.betoncraft.flier.api.Game;

/**
 * The main command.
 *
 * @author Jakub Sapalski
 */
public class FlierCommand implements CommandExecutor {
	
	private List<Argument> arguments = new ArrayList<>();
	
	public FlierCommand() {
		
		// JOIN GAME ARGUMENT
		arguments.add(new Argument(new String[]{"join", "j"}, "Join a game.", "<game> <team> <class>", new Argument[]{}) {
			@Override
			void parse(CommandSender sender, String currentCommand, Iterator<String> it) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.DARK_RED + "Must be a player to use this argument!");
				}
				Flier f = Flier.getInstance();
				
				Game game;
				if (!it.hasNext()) {
					displayHelp(sender, currentCommand, this);
					return;
				} else {
					String gameName = it.next();
					game = f.getGame(gameName);
					if (game == null) {
						sender.sendMessage(ChatColor.RED + "No such game: " + ChatColor.DARK_RED + gameName);
						StringBuilder builder = new StringBuilder();
						for (String g : f.getGames().keySet()) {
							builder.append(ChatColor.YELLOW + g + ChatColor.GREEN + ", ");
						}
						sender.sendMessage(ChatColor.GREEN + "Available games: "
								+ builder.toString().trim().substring(0, builder.lastIndexOf(",")));
						return;
					}
				}
				
				game.addPlayer((Player) sender);
			}
		});
		
		// LEAVE GAME ARGUMENT
		arguments.add(new Argument(new String[]{"leave", "l"}, "Leave a game.", "<game>", new Argument[]{}) {
			@Override
			void parse(CommandSender sender, String currentCommand, Iterator<String> it) {
				if (!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.DARK_RED + "Must be a player!");
				}
				Flier f = Flier.getInstance();
				Player player = (Player) sender;
				for (Game g : f.getGames().values()) {
					if (g.getPlayers().containsKey(player.getUniqueId())) {
						g.removePlayer(player);
						return;
					}
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
