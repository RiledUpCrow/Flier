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

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import net.md_5.bungee.api.ChatColor;
import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Lobby;
import pl.betoncraft.flier.api.core.CommandArgument;
import pl.betoncraft.flier.api.core.InGamePlayer;

/**
 * Sets player's money in the game.
 *
 * @author Jakub Sapalski
 */
public class MoneyArgument implements CommandArgument {
	
	private Permission permission = new Permission("flier.admin.setmoney");

	@Override
	public void parse(CommandSender sender, String currentCommand, Iterator<String> it) {
		try {
			String playerName = it.next();
			int money = Integer.parseInt(it.next());
			Player player = Bukkit.getPlayer(playerName);
			if (player == null) {
				sender.sendMessage(ChatColor.RED + playerName + " is offline.");
				return;
			}
			for (Lobby lobby : Flier.getInstance().getLobbies().values()) {
				InGamePlayer data = lobby.getGame().getPlayers().get(player.getUniqueId());
				if (data == null) {
					sender.sendMessage(ChatColor.RED + playerName + " is not in any game.");
					return;
				}
				data.setMoney(money);
				sender.sendMessage(ChatColor.GREEN + playerName + " has now $" + money + ".");
			}
		} catch (NoSuchElementException e) {
			CommandArgument.displayHelp(sender, currentCommand, this);
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.RED + "Money amount must be an integer.");
		}
	}

	@Override
	public String getName() {
		return "money";
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList(new String[]{"money", "m"});
	}

	@Override
	public String getDescription(CommandSender sender) {
		return "Sets player's money in the game.";
	}

	@Override
	public String getHelp(CommandSender sender) {
		return "<player> <money>";
	}

	@Override
	public Permission getPermission() {
		return permission;
	}

	@Override
	public User getUser() {
		return User.ANYONE;
	}

}
