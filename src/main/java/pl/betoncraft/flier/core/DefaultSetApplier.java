/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.core.ItemSet;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.SetApplier;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * Default implementation of the SetApplier.
 *
 * @author Jakub Sapalski
 */
public class DefaultSetApplier implements SetApplier {
	
	protected ValueLoader loader;

	protected final String id;
	protected final AddType addType;
	protected final ConflicAction conflictAction;
	protected final boolean saving;
	protected final int amount;
	protected final String category;

	public DefaultSetApplier(ConfigurationSection section) throws LoadingException {
		loader = new ValueLoader(section);
		addType = loader.loadEnum("add_type", AddType.class);
		conflictAction = loader.loadEnum("conflict_action", ConflicAction.class);
		saving = loader.loadBoolean("saving", false);
		amount = loader.loadPositiveInt("amount", 1);
		id = loader.loadString("item_set");
		// check if everything's fine
		ItemSet set = Flier.getInstance().getItemSet(id);
		category = set.getCategory();
	}
	
	public DefaultSetApplier(String name) throws LoadingException {
		addType = AddType.FILL;
		conflictAction = ConflicAction.REPLACE;
		saving = true;
		amount = 1;
		id = name;
		// check if everything's fine
		ItemSet set = Flier.getInstance().getItemSet(id);
		category = set.getCategory();
		
	}

	@Override
	public ItemSet getItemSet() {
		try {
			return Flier.getInstance().getItemSet(id);
		} catch (LoadingException e) {
			e.printStackTrace();
			return null; // won't happen, it's already checked
		}
	}

	@Override
	public String getCategory() {
		return category;
	}

	@Override
	public String getID() {
		return id;
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
		return applier.getID().equals(id);
	}

}
