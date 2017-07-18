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
package pl.betoncraft.flier.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.permissions.Permission;

import pl.betoncraft.flier.api.content.Button;
import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.SetApplier;
import pl.betoncraft.flier.core.DefaultSetApplier;
import pl.betoncraft.flier.util.LangManager;
import pl.betoncraft.flier.util.ValueLoader;

public class DefaultButton implements Button {

	protected final Game game;
	protected final String id;
	protected final String name;
	protected List<Location> locations;
	protected final Set<String> requirements;
	protected final Set<Permission> permissions;
	protected final int buyCost;
	protected final int sellCost;
	protected final int unlockCost;
	protected final SetApplier onBuy;
	protected final SetApplier onSell;
	protected final SetApplier onUnlock;

	public DefaultButton(Game game, ConfigurationSection section) throws LoadingException {
		this.game = game;
		id = section.getName();
		ValueLoader loader = new ValueLoader(section);
		name = loader.loadString("name", id);
		locations = Arrays.asList(game.getArena().getLocationSet(section.getString("blocks")).getMultiple());
		if (locations.isEmpty()) {
			throw new LoadingException("Blocks must be specified.");
		}
		requirements = new HashSet<>(section.getStringList("required"));
		permissions = new HashSet<>(
				section.getStringList("permissions").stream()
						.map(str -> new Permission(str))
						.collect(Collectors.toList())
		);
		buyCost = loader.loadInt("buy_cost", 0);
		sellCost = loader.loadInt("sell_cost", 0);
		unlockCost = loader.loadNonNegativeInt("unlock_cost", 0);
		try {
			ConfigurationSection buySection = section.getConfigurationSection("on_buy");
			onBuy = buySection == null ? null : new DefaultSetApplier(buySection);
		} catch (LoadingException e) {
			throw (LoadingException) new LoadingException("Error in 'on_buy' section.").initCause(e);
		}
		try {
			ConfigurationSection sellSection = section.getConfigurationSection("on_sell");
			onSell = sellSection == null ? null : new DefaultSetApplier(sellSection);
		} catch (LoadingException e) {
			throw (LoadingException) new LoadingException("Error in 'on_sell' section.").initCause(e);
		}
		try {
			ConfigurationSection unlockSection = section.getConfigurationSection("on_unlock");
			onUnlock = unlockSection == null ? null : new DefaultSetApplier(unlockSection);
		} catch (LoadingException e) {
			throw (LoadingException) new LoadingException("Error in 'on_unlock' section.").initCause(e);
		}
	}
	
	@Override
	public String getID() {
		return id;
	}
	
	@Override
	public String getName(CommandSender player) {
		return name.startsWith("$") ? LangManager.getMessage(player, name.substring(1)) : name;
	}
	
	@Override
	public List<Location> getLocations() {
		return locations;
	}
	
	@Override
	public void setLocations(List<Location> locs) {
		locations = new ArrayList<>(locs.size());
		locs.forEach(location ->
				locations.add(new Location(
						location.getWorld(),
						location.getBlockX(),
						location.getBlockY(),
						location.getBlockZ()
				))
		);
	}
	
	@Override
	public Set<String> getRequirements() {
		return requirements;
	}
	
	@Override
	public Set<Permission> getPermissions() {
		return permissions;
	}

	@Override
	public int getBuyCost() {
		return buyCost;
	}

	@Override
	public int getSellCost() {
		return sellCost;
	}

	@Override
	public int getUnlockCost() {
		return unlockCost;
	}

	@Override
	public SetApplier getOnBuy() {
		return onBuy;
	}

	@Override
	public SetApplier getOnSell() {
		return onSell;
	}

	@Override
	public SetApplier getOnUnlock() {
		return onUnlock;
	}
	
}