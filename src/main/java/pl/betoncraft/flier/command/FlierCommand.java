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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import pl.betoncraft.flier.api.core.CommandArgument;

/**
 * The main command.
 *
 * @author Jakub Sapalski
 */
public class FlierCommand implements CommandExecutor, TabCompleter {
	
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

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		String prefix = args[args.length - 1].toLowerCase(Locale.ROOT);
		List<String> list = new ArrayList<>();
		for(CommandArgument cmd : arguments) {
			if(args.length == 1) {
				if(cmd.getName().startsWith(prefix) || prefix.isEmpty())
					list.add(cmd.getName());
			} else if(args.length == 2) {
				for(CommandArgument subCmd : cmd.getSubCommand()) {
					if(subCmd.getName().startsWith(prefix) || prefix.isEmpty())
						list.add(subCmd.getName());
				}
			}
		}
		return list.isEmpty() ? null : list;
	}

}
