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

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import pl.betoncraft.flier.api.core.CommandArgument;

/**
 * The main command.
 *
 * @author Jakub Sapalski
 */
public class FlierCommand implements CommandExecutor {
	
	private List<CommandArgument> arguments = new ArrayList<>();
	
	public FlierCommand() {
		arguments.add(new LobbyArgument());
		arguments.add(new MoneyArgument());
		arguments.add(new SaveArgument());
		arguments.add(new LoadArgument());
		arguments.add(new CoordinatorArgument());
		arguments.add(new ReloadArgument());
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equals("flier")) {
			Iterator<String> it = Arrays.asList(args).iterator();
			if (!it.hasNext()) {
				CommandArgument.displayHelp(sender, arguments);
			} else {
				CommandArgument.nextArgument(sender, label, it, it.next(), arguments);
			}
			return true;
		}
		return false;
	}

}
