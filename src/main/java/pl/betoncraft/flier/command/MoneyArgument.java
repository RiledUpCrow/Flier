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
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.api.content.Lobby;
import pl.betoncraft.flier.api.core.CommandArgument;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.util.LangManager;

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
				LangManager.sendMessage(sender, "player_offline", playerName);
				return;
			}
			UUID uuid = player.getUniqueId();
			InGamePlayer data = null;
			loop: for (Lobby lobby : Flier.getInstance().getLobbies().values()) {
				for (Set<Game> games : lobby.getGames().values()) {
					for (Game game : games) {
						data = game.getPlayers().get(uuid);
						if (data != null) {
							break loop;
						}
					}
				}
			}
			if (data == null) {
				LangManager.sendMessage(sender, "not_in_game", playerName);
				return;
			}
			data.setMoney(money);
			LangManager.sendMessage(sender, "money_set", playerName, money);
		} catch (NoSuchElementException e) {
			CommandArgument.displayHelp(sender, currentCommand, this);
		} catch (NumberFormatException e) {
			LangManager.sendMessage(sender, "money_integer");
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
		return LangManager.getMessage(sender, "money_desc");
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
