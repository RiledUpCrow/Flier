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
package pl.betoncraft.flier.action;

import java.util.Optional;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

import com.google.common.collect.Sets;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.Kit.AddResult;
import pl.betoncraft.flier.api.core.SetApplier;
import pl.betoncraft.flier.api.core.UsableItem;
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
		super(section, false, false);
		applier = new DefaultSetApplier(section);
	}

	@Override
	public boolean act(Optional<InGamePlayer> creator, Optional<InGamePlayer> source,
			InGamePlayer target, Optional<UsableItem> item) {
		if (accepted.contains(applier.isSaving() ?
				target.getKit().addStored(applier) :
				target.getKit().addCurrent(applier))) {
			target.updateKit();
			return true;
		}
		return false;
	}

}
