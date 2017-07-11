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

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import net.md_5.bungee.api.ChatColor;
import pl.betoncraft.flier.api.core.CommandArgument;
import pl.betoncraft.flier.util.PlayerBackup;

/**
 * Saves the player to the file.
 *
 * @author Jakub Sapalski
 */
public class SaveArgument implements CommandArgument {
	
	private Permission permission = new Permission("flier.dev.save");

	@Override
	public String getName() {
		return "save";
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList(new String[]{getName()});
	}

	@Override
	public String getDescription(CommandSender sender) {
		return "Saves you to the file.";
	}

	@Override
	public String getHelp(CommandSender sender) {
		return "";
	}

	@Override
	public void parse(CommandSender sender, String currentCommand, Iterator<String> it) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(String.format("%sMust be a player.", ChatColor.RED));
			return;
		}
		Player player = (Player) sender;
		if (new PlayerBackup(player).save()) {
			sender.sendMessage(String.format("%sSuccessfully saved to a file.", ChatColor.GREEN));
		} else {
			sender.sendMessage(String.format("%sCould not save to a file.", ChatColor.RED));
		}
	}

	@Override
	public Permission getPermission() {
		return permission;
	}

	@Override
	public User getUser() {
		return User.PLAYER;
	}

}
