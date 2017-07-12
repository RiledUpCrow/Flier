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

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import com.google.common.collect.Sets;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Button;
import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.api.content.Lobby;
import pl.betoncraft.flier.api.core.CommandArgument;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.util.LangManager;

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
				CommandArgument.displayObjects(sender, "object_action", b, Sets.newHashSet("buy / b", "sell / s"));
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
				LangManager.sendMessage(sender, "player_offline", playerName);
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
			for (List<Game> games : lobby.getGames().values()) {
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
				CommandArgument.displayObjects(sender, "object_button", item, data.getGame().getButtons().keySet());
				return;
			}
		} else {
			if (player.equals(sender)) {
				LangManager.sendMessage(sender, "you_not_in_lobby");
			} else {
				LangManager.sendMessage(sender, "player_not_in_lobby", player.getName());
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
			return LangManager.getMessage(sender, "item_desc_1");
		} else {
			if (sender.hasPermission(force)) {
				return LangManager.getMessage(sender, "item_desc_2");
			} else {
				return LangManager.getMessage(sender, "item_desc_3");
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
