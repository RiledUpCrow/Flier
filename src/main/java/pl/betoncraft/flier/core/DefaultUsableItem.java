/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.content.Action;
import pl.betoncraft.flier.api.content.Activator;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.Item;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.Modification;
import pl.betoncraft.flier.api.core.Modification.ModificationTarget;
import pl.betoncraft.flier.api.core.UsableItem;
import pl.betoncraft.flier.api.core.Usage;
import pl.betoncraft.flier.event.FlierUseEvent;

/**
 * Default implementation of UsableItem.
 *
 * @author Jakub Sapalski
 */
public class DefaultUsableItem extends DefaultItem implements UsableItem {
	
	private static final String AMMO = "ammo";
	private static final String CONSUMABLE = "consumable";

	protected final boolean consumable;
	protected final int maxAmmo;
	protected final List<Usage> usages = new ArrayList<>();
	protected final int defAmount;
	protected final int maxAmount;
	protected final int minAmount;

	protected int amount;
	protected int time = 0;
	protected int whole = 0;
	protected int ammo;

	public DefaultUsableItem(ConfigurationSection section) throws LoadingException {
		super(section);
		consumable = loader.loadBoolean(CONSUMABLE, false);
		maxAmmo = loader.loadNonNegativeInt(AMMO, 0);
		ammo = maxAmmo;
		defAmount = loader.loadPositiveInt("amount", 1);
		maxAmount = loader.loadNonNegativeInt("max_amount", 0);
		minAmount = loader.loadNonNegativeInt("min_amount", 0);
		amount = defAmount;
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
		return modMan.modifyBoolean(CONSUMABLE, consumable);
	}

	@Override
	public int getMaxAmmo() {
		return (int) modMan.modifyNumber(AMMO, maxAmmo);
	}

	@Override
	public int getAmmo() {
		int max = getMaxAmmo();
		if (max != 0 && ammo > max) {
			ammo = max;
		}
		return ammo;
	}

	@Override
	public void setAmmo(int ammo) {
		this.ammo = ammo;
		if (this.ammo < 0) {
			this.ammo = 0;
		}
		int maxAmmo = getMaxAmmo();
		if (this.ammo > maxAmmo) {
			this.ammo = maxAmmo;
		}
	}
	
	@Override
	public int getAmount() {
		int max = getMaxAmount();
		if (max != 0 && amount > max) {
			amount = max;
		}
		return amount;
	}
	
	@Override
	public boolean setAmount(int amount) {
		int max = getMaxAmount();
		if (amount < 0 || (max != 0 && amount > max)) {
			return false;
		} else {
			this.amount = amount;
			return true;
		}
	}
	
	@Override
	public int getMaxAmount() {
		return (int) modMan.modifyNumber("max_amount", maxAmount);
	}
	
	@Override
	public int getMinAmount() {
		return (int) modMan.modifyNumber("min_amount", minAmount);
	}
	
	@Override
	public int getDefAmount() {
		return (int) modMan.modifyNumber("amount", defAmount);
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
				if (getMaxAmmo() > 0 && ammo - usage.getAmmoUse() < 0) {
					continue;
				}
				for (Activator activator : usage.getActivators()) {
					if (!activator.isActive(player, this)) {
						continue usages;
					}
				}
				FlierUseEvent event = new FlierUseEvent(player, this, usage);
				Bukkit.getPluginManager().callEvent(event);
				if (event.isCancelled()) {
					continue;
				}
				used = true;
				int cooldown = usage.getCooldown();;
				if (time < cooldown) {
					time = cooldown;
					whole = time;
				}
				setAmmo(ammo - usage.getAmmoUse());
				for (Action action : usage.getActions()) {
					action.act(player, this);
				}
			}
		}
		return used;
	}
	
	@Override
	public boolean isSimilar(Item item) {
		if (item instanceof DefaultUsableItem && super.isSimilar(item)) {
			DefaultUsableItem usable = (DefaultUsableItem) item;
			return usable.consumable == consumable &&
					usable.maxAmmo == maxAmmo &&
					usable.usages.stream().allMatch(thatUsage -> usages.stream().anyMatch(thisUsage -> thatUsage.equals(thisUsage)));
		}
		return false;
	}
	
	@Override
	public void refill() {
		ammo = getMaxAmmo();
		time = 0;
		whole = 0;
	}

	@Override
	public void addModification(Modification mod) {
		if (mod.getTarget() == ModificationTarget.USABLE_ITEM) {
			modMan.addModification(mod);
		} else if (mod.getTarget() == ModificationTarget.ACTION) {
			usages.forEach(usage -> usage.getActions().stream()
					.filter(action -> mod.getNames().contains(action.getID()))
					.forEach(action -> action.addModification(mod))
			);
		} else if (mod.getTarget() == ModificationTarget.ACTIVATOR) {
			usages.forEach(usage -> usage.getActivators().stream()
					.filter(activator -> mod.getNames().contains(activator.getID()))
					.forEach(activator -> activator.addModification(mod))
			);
		}
	}

	@Override
	public void removeModification(Modification mod) {
		if (mod.getTarget() == ModificationTarget.USABLE_ITEM) {
			modMan.removeModification(mod);
		} else if (mod.getTarget() == ModificationTarget.ACTION) {
			usages.forEach(usage -> usage.getActions().stream()
					.filter(action -> mod.getNames().contains(action.getID()))
					.forEach(action -> action.removeModification(mod))
			);
		} else if (mod.getTarget() == ModificationTarget.ACTIVATOR) {
			usages.forEach(usage -> usage.getActivators().stream()
					.filter(activator -> mod.getNames().contains(activator.getID()))
					.forEach(activator -> activator.removeModification(mod))
			);
		}
	}
	
	@Override
	public void clearModifications() {
		modMan.clear();
	}
}
