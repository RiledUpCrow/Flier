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
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import com.google.common.collect.Sets;

import net.md_5.bungee.api.ChatColor;
import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.api.content.Game.Button;
import pl.betoncraft.flier.api.content.Lobby;
import pl.betoncraft.flier.api.core.CommandArgument;
import pl.betoncraft.flier.api.core.InGamePlayer;

/**
 * Chooses a SetApplier for the player.
 *
 * @author Jakub Sapalski
 */
public class ChooseItemArgument implements CommandArgument {

	private Permission permission = new Permission("flier.player.item");
	private Permission force = new Permission("flier.admin.item");

	@Override
	public void parse(CommandSender sender, String currentCommand, Iterator<String> it) {
		Flier flier = Flier.getInstance();
		String item = null;
		boolean buy = true;
		if (it.hasNext()) {
			item = it.next();
		} else {
			CommandArgument.displayHelp(sender, currentCommand, this);
			return;
		}
		if (it.hasNext()) {
			String b = it.next();
			if (b.equalsIgnoreCase("buy") || b.equalsIgnoreCase("b")) {
				buy = true;
			} else if (b.equalsIgnoreCase("sell") || b.equalsIgnoreCase("s")) {
				buy = false;
			} else {
				CommandArgument.displayObjects(sender, "buy", b, Sets.newHashSet("buy / b", "sell / s"));
			}
		} else {
			CommandArgument.displayHelp(sender, currentCommand, this);
			return;
		}
		Player player = null;
		if (it.hasNext()) {
			if (!sender.hasPermission(force)) {
				CommandArgument.noPermission(sender);
				return;
			}
			String playerName = it.next();
			player = Bukkit.getPlayer(playerName);
			if (player == null) {
				sender.sendMessage(String.format("%s%s is offline.", ChatColor.RED, playerName));
				return;
			}
		} else {
			if (!CommandArgument.checkUser(sender, User.PLAYER)) {
				CommandArgument.wrongUser(sender);
				return;
			} else {
				player = (Player) sender;
			}
		}
		UUID uuid = player.getUniqueId();
		InGamePlayer data = null;
		loop: for (Lobby lobby : flier.getLobbies().values()) {
			for (Set<Game> games : lobby.getGames().values()) {
				for (Game game : games) {
					data = game.getPlayers().get(uuid);
					if (data != null) {
						break loop;
					}
				}
			}
		}
		if (data != null) {
			Button button = data.getGame().getButtons().get(item);
			if (button != null) {
				data.getGame().applyButton(data, button, buy, player.equals(sender));
			} else {
				CommandArgument.displayObjects(sender, "button", item, data.getGame().getButtons().keySet());
				return;
			}
		} else {
			if (player.equals(sender)) {
				sender.sendMessage(String.format("%sYou are not in a lobby.", ChatColor.RED));
			} else {
				sender.sendMessage(String.format("%s%s is not in a lobby.", ChatColor.RED, player.getName()));
			}
		}
	}

	@Override
	public String getName() {
		return "item";
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList(new String[]{getName(), "i"});
	}

	@Override
	public String getDescription(CommandSender sender) {
		if (CommandArgument.checkUser(sender, User.CONSOLE)) {
			return "Force a player to choose an item.";
		} else {
			if (sender.hasPermission(force)) {
				return "Choose an item or force specified player to choose an item.";
			} else {
				return "Choose an item.";
			}
		}
	}

	@Override
	public String getHelp(CommandSender sender) {
		if (CommandArgument.checkUser(sender, User.CONSOLE)) {
			return "<item> <buy/sell> <player>";
		} else {
			if (sender.hasPermission(force)) {
				return "<item> <buy/sell> [player]";
			} else {
				return "<item> <buy/sell>";
			}
		}
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
