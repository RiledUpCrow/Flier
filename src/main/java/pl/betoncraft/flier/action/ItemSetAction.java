/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.action;

import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import com.google.common.collect.Sets;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.PlayerClass.AddResult;
import pl.betoncraft.flier.api.core.SetApplier;
import pl.betoncraft.flier.core.DefaultSetApplier;

/**
 * Action which applies an ItemSet.
 *
 * @author Jakub Sapalski
 */
public class ItemSetAction extends DefaultAction {

	private SetApplier applier;
	private Set<AddResult> accepted = Sets.newHashSet(
			AddResult.ADDED,
			AddResult.FILLED,
			AddResult.REMOVED,
			AddResult.REPLACED
	);

	public ItemSetAction(ConfigurationSection section) throws LoadingException {
		super(section);
		applier = new DefaultSetApplier(section);
	}

	@Override
	public boolean act(InGamePlayer player) {
		if (accepted.contains(applier.isSaving() ?
				player.getClazz().addStored(applier) :
				player.getClazz().addCurrent(applier))) {
			player.updateClass();
			return true;
		}
		return false;
	}

}
