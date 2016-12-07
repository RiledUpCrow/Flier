/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import pl.betoncraft.flier.api.Game;
import pl.betoncraft.flier.api.PlayerClass;
import pl.betoncraft.flier.api.Team;

/**
 * The main command.
 *
 * @author Jakub Sapalski
 */
public class FlierCommand implements CommandExecutor {
	
	public FlierCommand() {
		Flier.getInstance().getCommand("flier").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equals("flier")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				if (args.length < 2) {
					player.sendMessage(ChatColor.DARK_RED + "/flier join game arguments...");
					player.sendMessage(ChatColor.DARK_RED + "/flier leave game");
					return true;
				} else if (args.length >= 2) {
					Flier f = Flier.getInstance();
					Game game = f.getGame(args[1]);
					if (game == null) {
						player.sendMessage(ChatColor.DARK_RED + "Game does not exist!");
						return true;
					}
					switch (args[0].toLowerCase()) {
					case "join":
						Team team = game.getTeam(args[2]);
						if (team == null) {
							player.sendMessage(ChatColor.DARK_RED + "Team does not exist!");
							return true;
						}
						PlayerClass clazz = f.getClass(args[3]);
						if (clazz == null) {
							player.sendMessage(ChatColor.DARK_RED + "Class does not exist!");
							return true;
						}
						game.addPlayer(player, team, clazz);
						break;
					case "leave":
						game.removePlayer(player);
						break;
					}
				}
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "Must be a player!");
			}
			return true;
		}
		return false;
	}

}
