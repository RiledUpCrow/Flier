/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * An argument in the Flier command.
 *
 * @author Jakub Sapalski
 */
public interface CommandArgument {
	
	public String name();
	
	public List<String> aliases();
	
	public String description();
	
	public String help();
	
	public void parse(CommandSender sender, String currentCommand, Iterator<String> it);
	
	public static void nextArgument(CommandSender sender, String currentCommand, Iterator<String> it, String name,
			List<CommandArgument> arguments) {
		for (CommandArgument a : arguments) {
			if (a.aliases().contains(name)) {
				a.parse(sender, currentCommand + " " + name, it);
				return;
			}
		}
		displayHelp(sender, arguments);
	}
	
	public static void displayHelp(CommandSender sender, String currentCommand, CommandArgument argument) {
		sender.sendMessage(ChatColor.RED + "Wrong use of a command. Correct syntax:");
		sender.sendMessage(ChatColor.DARK_GREEN + "/" + currentCommand + " " + argument.help());
	}
	
	public static void displayHelp(CommandSender sender, List<CommandArgument> arguments) {
		sender.sendMessage(ChatColor.RED + "Available arguments:");
		for (CommandArgument a : arguments) {
			String aliases;
			if (a.aliases().size() > 1) {
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

	public static void displayObjects(CommandSender sender, String type, String name, Set<String> available) {
		sender.sendMessage(ChatColor.RED + "No such " + type + ": " + ChatColor.DARK_RED + name);
		StringBuilder builder = new StringBuilder();
		for (String g : available) {
			builder.append(ChatColor.YELLOW + g + ChatColor.GREEN + ", ");
		}
		sender.sendMessage(ChatColor.GREEN + "Available names: "
				+ builder.toString().trim().substring(0, builder.lastIndexOf(",")));
	}

}
