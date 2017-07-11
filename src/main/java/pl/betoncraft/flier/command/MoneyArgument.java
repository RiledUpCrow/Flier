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
