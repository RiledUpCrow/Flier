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
import org.bukkit.permissions.Permission;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.core.CommandArgument;
import pl.betoncraft.flier.util.LangManager;

/**
 * Reloads the plugin.
 *
 * @author Jakub Sapalski
 */
class ReloadArgument implements CommandArgument {
	
	private Permission permission = new Permission("flier.admin.reload");

	@Override
	public void parse(CommandSender sender, String currentCommand, Iterator<String> it) {
		Flier f = Flier.getInstance();
		f.reload();
		LangManager.sendMessage(sender, "reloaded");
	}

	@Override
	public String getName() {
		return "reload";
	}

	@Override
	public List<String> getAliases() {
		return Arrays.asList(new String[]{"reload"});
	}

	@Override
	public String getDescription(CommandSender sender) {
		return LangManager.getMessage(sender, "reload_desc");
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
		return User.ANYONE;
	}

}