/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core;

import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;

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

	protected String id;
	protected AddType addType;
	protected ConflicAction conflictAction;
	protected boolean saving;
	protected int amount;
	protected String category;
	protected ConfigurationSection set;

	public DefaultSetApplier(ConfigurationSection section, Map<String, ConfigurationSection> available) throws LoadingException {
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
		try {
			ItemSet s = new DefaultSet(set); // check if everything's fine
			category = s.getCategory();
			id = s.getID();
		} catch (LoadingException e) {
			throw (LoadingException) new LoadingException(String.format("Error in '%' item set.", setName)).initCause(e);
		}
	}
	
	public DefaultSetApplier(ConfigurationSection set) {
		id = set.getName();
		addType = AddType.FILL;
		conflictAction = ConflicAction.REPLACE;
		saving = true;
		amount = 1;
		category = set.getString("category");
		this.set = set;
	}

	@Override
	public ItemSet getItemSet() {
		try {
			return new DefaultSet(set);
		} catch (LoadingException e) {
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
