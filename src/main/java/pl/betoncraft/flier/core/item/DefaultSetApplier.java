/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core.item;

import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.ItemSet;
import pl.betoncraft.flier.api.LoadingException;
import pl.betoncraft.flier.api.SetApplier;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * Default implementation of the SetApplier.
 *
 * @author Jakub Sapalski
 */
public class DefaultSetApplier implements SetApplier {
	
	protected ValueLoader loader;

	protected AddType addType;
	protected ConflicAction conflictAction;
	protected boolean saving;
	protected int amount;
	protected ItemSet set;

	public DefaultSetApplier(ConfigurationSection section, Map<String, ItemSet> available) throws LoadingException {
		loader = new ValueLoader(section);
		addType = loader.loadEnum("add_type", AddType.class);
		conflictAction = loader.loadEnum("conflict_action", ConflicAction.class);
		saving = loader.loadBoolean("saving", false);
		amount = loader.loadPositiveInt("amount", 1);
		String setName = loader.loadString("item");
		set = available.get(setName);
		if (set == null) {
			throw new LoadingException(String.format("Item set '%s' is not defined.", setName));
		}
	}
	
	public DefaultSetApplier(ItemSet set) {
		addType = AddType.FILL;
		conflictAction = ConflicAction.REPLACE;
		saving = true;
		amount = 1;
		this.set = set;
	}

	@Override
	public ItemSet getItemSet() {
		return set.replicate();
	}
	
	@Override
	public boolean isSaving() {
		return saving;
	}

	@Override
	public int getAmount() {
		return amount;
	}

	@Override
	public AddType getAddType() {
		return addType;
	}

	@Override
	public ConflicAction getConflictAction() {
		return conflictAction;
	}

	@Override
	public boolean isSimilar(SetApplier applier) {
		if (applier instanceof DefaultSetApplier) {
			DefaultSetApplier def = (DefaultSetApplier) applier;
			return def.set.isSimilar(set) && def.amount == amount;
		}
		return false;
	}

}
