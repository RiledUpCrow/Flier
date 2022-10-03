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
package pl.betoncraft.flier.api.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import pl.betoncraft.flier.util.LangManager;

/**
 * An argument in the Flier command.
 *
 * @author Jakub Sapalski
 */
public interface CommandArgument {

	/**
	 * @return the name of this argument (the default alias)
	 */
	public String getName();

	/**
	 * @return the list of aliases which can be used instead of the name
	 */
	public List<String> getAliases();

	/**
	 * @return the description of this argument's behavior for this sender
	 */
	public String getDescription(CommandSender sender);

	/**
	 * @return the help string which shows how to use the command for this sender
	 */
	public String getHelp(CommandSender sender);
	
	/**
	 * @return the list of Permissions which enable using this Argument
	 */
	public Permission getPermission();
	
	public default List<CommandArgument> getSubCommand() {
		return new ArrayList<>();
	}
	
	/**
	 * The user of an Argument.
	 */
	public enum User {
		CONSOLE, PLAYER, ANYONE
	}
	
	/**
	 * @return who can use this Argument
	 */
	public User getUser();

	/**
	 * Parses the argument. Extracts all information from the argument iterator.
	 * It may pass the iterator to another CommandArgument if it wishes so.
	 * 
	 * @param sender
	 *            the sender of the command
	 * @param currentCommand
	 *            the command string parsed so far
	 * @param iterator
	 *            iterator over next arguments
	 */
	public void parse(CommandSender sender, String currentCommand, Iterator<String> iterator);

	/**
	 * Parses the next argument. Call it if you're finished with the current
	 * argument and there are more arguments to be parsed.
	 * 
	 * @param sender
	 *            the sender of the command
	 * @param currentCommand
	 *            the command string parsed so far
	 * @param iterator
	 *            iterator over next arguments (it should be already incremented
	 *            to the next argument, see {@code name} argument)
	 * @param name
	 *            name of the next argument (you should get it by calling
	 *            {@code it.next()})
	 * @param arguments
	 *            the list of CommandArguments which are valid in this context
	 */
	public static void nextArgument(CommandSender sender, String currentCommand, Iterator<String> iterator, String name,
			List<CommandArgument> arguments) {
		for (CommandArgument a : arguments) {
			if (!a.getAliases().contains(name)) {
				continue;
			} else if (!checkUser(sender, a.getUser())) {
				wrongUser(sender);
			} else if (!sender.hasPermission(a.getPermission())) {
				noPermission(sender);
			} else {
				a.parse(sender, currentCommand + " " + name, iterator);
			}
			return;
		}
		displayHelp(sender, arguments);
	}

	/**
	 * Displays the help for the currently parsed argument.
	 * 
	 * @param sender
	 *            sender of the command, it will receive the message
	 * @param currentCommand
	 *            the command string parsed to this point
	 * @param argument
	 *            the argument which is being used incorrectly
	 */
	public static void displayHelp(CommandSender sender, String currentCommand, CommandArgument argument) {
		LangManager.sendMessage(sender, "wrong_use");
		sender.sendMessage(ChatColor.DARK_GREEN + "/" + currentCommand + " " + argument.getHelp(sender));
	}

	/**
	 * Displays all available arguments at this point.
	 * 
	 * @param sender
	 *            sender of the command, it will receive the message
	 * @param arguments
	 *            list of arguments which are available in this context
	 */
	public static void displayHelp(CommandSender sender, List<CommandArgument> arguments) {
		LangManager.sendMessage(sender, "available_arguments");
		for (CommandArgument a : arguments) {
			// skip the argument if the sender can't use it
			if (!checkUser(sender, a.getUser())) {
				continue;
			} else if (!sender.hasPermission(a.getPermission())) {
				continue;
			}
			// get all aliases
			String aliases;
			if (a.getAliases().size() > 1) {
				StringBuilder builder = new StringBuilder();
				for (String alias : a.getAliases().subList(1, a.getAliases().size())) {
					builder.append(ChatColor.LIGHT_PURPLE + alias + ChatColor.WHITE + ", ");
				}
				aliases = ChatColor.WHITE + "(" + builder.toString().trim().substring(0, builder.lastIndexOf(","))
						+ ChatColor.WHITE + ")";
			} else {
				aliases = "";
			}
			// display a message
			sender.sendMessage(ChatColor.YELLOW + "- " + ChatColor.DARK_AQUA + a.getName() + " " + aliases
					+ ChatColor.YELLOW + ": " + ChatColor.GREEN + a.getDescription(sender));
		}
	}

	/**
	 * Displays a list of available objects of some type.
	 * 
	 * @param sender
	 *            sender of the command, it will receive the message
	 * @param type
	 *            name of the type of the object, it will be displayed to the
	 *            sender
	 * @param name
	 *            the user have tried to use this object in the command but it
	 *            doesn't exist; this is its name
	 * @param available
	 *            a set of available object names
	 */
	public static void displayObjects(CommandSender sender, String type, String name, Set<String> available) {
		type = LangManager.getMessage(sender, type);
		LangManager.sendMessage(sender, "no_such_object", type, name);
		StringBuilder builder = new StringBuilder();
		for (String g : available) {
			builder.append(String.format("%s%s%s, ", ChatColor.YELLOW, g, ChatColor.GREEN));
		}
		int lastIndex = builder.lastIndexOf(",");
		if (lastIndex < 0) {
			lastIndex = 0;
		}
		LangManager.sendMessage(sender, "available_names", builder.toString().trim().substring(0, lastIndex));
	}
	
	/**
	 * Displays the information about lack of permission to use an Argument.
	 * 
	 * @param sender the sender of this command
	 */
	public static void noPermission(CommandSender sender) {
		LangManager.sendMessage(sender, "no_permission");
	}
	
	/**
	 * Displays the information about being a wrong user type of an Argument.
	 * 
	 * @param sender the sender of this command
	 */
	public static void wrongUser(CommandSender sender) {
		if (sender instanceof Player) {
			LangManager.sendMessage(sender, "console_only");
		} else {
			LangManager.sendMessage(sender, "player_only");
		}
	}
	
	/**
	 * Checks if the sender is of matching type.
	 * 
	 * @param sender
	 *            the sender of this command
	 * @param target
	 *            the required type of the command sender
	 * @return whenever this sender has a required type
	 */
	public static boolean checkUser(CommandSender sender, User target) {
		switch (target) {
		case ANYONE: return true;
		case PLAYER: return sender instanceof Player;
		case CONSOLE: return sender instanceof ConsoleCommandSender;
		default: return false;
		}
	}

}
