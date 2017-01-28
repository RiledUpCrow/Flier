/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.Flier;
import pl.betoncraft.flier.api.Action;
import pl.betoncraft.flier.api.Activator;
import pl.betoncraft.flier.api.InGamePlayer;
import pl.betoncraft.flier.api.Item;
import pl.betoncraft.flier.api.UsableItem;
import pl.betoncraft.flier.api.Usage;
import pl.betoncraft.flier.core.defaults.DefaultItem;
import pl.betoncraft.flier.exception.LoadingException;

/**
 * Default implementation of UsableItem.
 *
 * @author Jakub Sapalski
 */
public class DefaultUsableItem extends DefaultItem implements UsableItem {
	
	protected final boolean consumable;
	protected final int maxAmmo;
	protected final List<Usage> usages = new ArrayList<>();

	protected int time = 0;
	protected int whole = 0;
	protected int ammo;

	public DefaultUsableItem(ConfigurationSection section) throws LoadingException {
		super(section);
		consumable = loader.loadBoolean("consumable", false);
		maxAmmo = loader.loadNonNegativeInt("ammo", 0);
		ammo = maxAmmo;
		ConfigurationSection usagesSection = section.getConfigurationSection("usages");
		if (usagesSection != null) for (String id : usagesSection.getKeys(false)) {
			ConfigurationSection usageSection = usagesSection.getConfigurationSection(id);
			if (usageSection != null) try {
				usages.add(new DefaultUsage(usageSection));
			} catch (LoadingException e) {
				throw (LoadingException) new LoadingException(String.format("Error in '%s' usage.", id)).initCause(e);
			}
		}
	}

	@Override
	public boolean isReady() {
		return time == 0;
	}

	@Override
	public int getWholeCooldown() {
		return whole;
	}

	@Override
	public int getCooldown() {
		return time;
	}
	
	@Override
	public boolean isConsumable() {
		return consumable;
	}

	@Override
	public int getMaxAmmo() {
		return maxAmmo;
	}

	@Override
	public int getAmmo() {
		return ammo;
	}

	@Override
	public void setAmmo(int ammo) {
		this.ammo = ammo;
		if (this.ammo < 0) {
			this.ammo = 0;
		}
		if (this.ammo > maxAmmo) {
			this.ammo = maxAmmo;
		}
	}

	@Override
	public List<Usage> getUsages() {
		return usages;
	}

	@Override
	public boolean use(InGamePlayer player) {
		if (time > 0) {
			time--;
		}
		boolean used = false;
		if (isReady()) {
			usages:
			for (Usage usage : usages) {
				if (!usage.canUse(player)) {
					continue;
				}
				if (maxAmmo > 0 && ammo - usage.getAmmoUse() < 0) {
					continue;
				}
				for (Activator activator : usage.getActivators()) {
					if (!activator.isActive(player, this)) {
						continue usages;
					}
				}
				used = true;
				time = usage.getCooldown();
				setAmmo(ammo - usage.getAmmoUse());
				whole = time;
				for (Action action : usage.getActions()) {
					action.act(player);
				}
			}
		}
		return used;
	}
	
	@Override
	public Item replicate() {
		try {
			return Flier.getInstance().getItem(id);
		} catch (LoadingException e) {
			return null; // dead code
		}
	}
}
