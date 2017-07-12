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
package pl.betoncraft.flier.core;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.Item;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.util.LangManager;
import pl.betoncraft.flier.util.ModificationManager;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * A base class for items saved in the configuration sections.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultItem implements Item {

	private static final String WEIGHT = "weight";

	protected final String id;
	protected final String name;
	protected final ValueLoader loader;
	protected final ModificationManager modMan;

	protected final Material material;
	protected final String rawName;
	protected final List<String> rawLore;
	protected final double weight;
	protected final int slot;

	public DefaultItem(ConfigurationSection section) throws LoadingException {
		id = section.getName();
		loader = new ValueLoader(section);
		modMan = new ModificationManager();
		name = loader.loadString("name", id);
		material = loader.loadEnum("material", Material.class);
		rawName = loader.loadString("name");
		rawLore = section.getStringList("lore");
		weight = loader.loadDouble(WEIGHT, 0.0);
		slot = loader.loadInt("slot", -1);
	}

	@Override
	public String getID() {
		return id;
	}
	
	@Override
	public String getName(CommandSender player) {
		return name.startsWith("$") ? LangManager.getMessage(player, name.substring(1)) : name;
	}

	@SuppressWarnings("deprecation")
	@Override
	public ItemStack getItem(InGamePlayer player) {
		String name = rawName.startsWith("$") ?
				LangManager.getMessage(player, rawName.substring(1)) :
				ChatColor.translateAlternateColorCodes('&', rawName);
		List<String> lore = rawLore.stream()
				.map(s -> s.startsWith("$") ?
						LangManager.getMessage(player, s.substring(1)) :
						ChatColor.translateAlternateColorCodes('&', s))
				.collect(Collectors.toList());
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		try {
			meta.setUnbreakable(true);
		} catch (NoSuchMethodError e) {
			meta.spigot().setUnbreakable(true);
		}
		item.setItemMeta(meta);
		return item;
	}

	@Override
	public double getWeight() {
		return modMan.modifyNumber(WEIGHT, weight);
	}

	@Override
	public int slot() {
		return slot;
	}
	
	@Override
	public boolean isSimilar(Item item) {
		if (item instanceof DefaultItem) {
			DefaultItem defItem = (DefaultItem) item;
			return id.equals(defItem.id);
		}
		return false;
	}

}
