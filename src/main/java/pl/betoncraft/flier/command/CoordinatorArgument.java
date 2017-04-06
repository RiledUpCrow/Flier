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
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;

import pl.betoncraft.flier.api.core.CommandArgument;
import pl.betoncraft.flier.util.Coordinator;
import pl.betoncraft.flier.util.LangManager;

/**
 * Toggles the Coordinator.
 *
 * @author Jakub Sapalski
 */
public class CoordinatorArgument implements CommandArgument {
	
	private Permission permission = new Permission("flier.admin.coordinator");

	@Override
	public void parse(CommandSender sender, String currentCommand, Iterator<String> it) {
		UUID uuid = ((Player) sender).getUniqueId();
		if (Coordinator.isActive(uuid)) {
			Coordinator.removePlayer(uuid);
			LangManager.sendMessage(sender, "coordinator_disabled");
		} else {
			Coordinator.addPlayer(uuid);
			LangManager.sendMessage(sender, "coordinator_enabled");
		}
	}

	@Override
	public String getName() {
		return "coordinator";
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList(new String[]{getName(), "c"});
	}

	@Override
	public String getDescription(CommandSender sender) {
		return LangManager.getMessage(sender, "coordinator_desc");
	}

	@Override
	public String getHelp(CommandSender sender) {
		return "";
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
