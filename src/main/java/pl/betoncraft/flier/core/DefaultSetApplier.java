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

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.ItemSet;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.SetApplier;
import pl.betoncraft.flier.util.DummyPlayer;
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
		ItemSet set = Flier.getInstance().getItemSet(id, new DummyPlayer());
		category = set.getCategory();
	}
	
	public DefaultSetApplier(String name) throws LoadingException {
		addType = AddType.FILL;
		conflictAction = ConflicAction.REPLACE;
		saving = true;
		amount = 1;
		id = name;
		// check if everything's fine
		ItemSet set = Flier.getInstance().getItemSet(id, new DummyPlayer());
		category = set.getCategory();
		
	}

	@Override
	public ItemSet getItemSet(InGamePlayer owner) {
		try {
			return Flier.getInstance().getItemSet(id, owner);
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
