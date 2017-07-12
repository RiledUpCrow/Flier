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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Action;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.Modification;
import pl.betoncraft.flier.api.core.Owner;

/**
 * An action type which adds a specified effect.
 *
 * @author Jakub Sapalski
 */
public class EffectAction extends DefaultAction {

	private static final String DURATION = "duration";

	private final List<Action> actions = new ArrayList<>();
	private final int duration;

	public EffectAction(ConfigurationSection section, Optional<Owner> owner) throws LoadingException {
		super(section, owner);
		Flier flier = Flier.getInstance();
		for (String actionName : section.getStringList("actions")) {
			actions.add(flier.getAction(actionName, owner));
		}
		duration = loader.loadPositiveInt(DURATION);
	}

	@Override
	public boolean act(InGamePlayer target, InGamePlayer source) {
		new BukkitRunnable() {
			private int i = (int) modMan.modifyNumber(DURATION, EffectAction.this.duration);
			@Override
			public void run() {
				if (i-- == 0) {
					cancel();
				}
				for (Action action : actions) {
					action.act(target, source);
				}
			}
		}.runTaskTimer(Flier.getInstance(), 0, 1);
		return true;
	}

	@Override
	public void addModification(Modification mod) {
		super.addModification(mod);
		actions.stream()
				.filter(action -> mod.getNames().contains(action.getID()))
				.forEach(action -> action.addModification(mod));
	}

	@Override
	public void removeModification(Modification mod) {
		super.removeModification(mod);
		actions.stream()
				.filter(action -> mod.getNames().contains(action.getID()))
				.forEach(action -> action.removeModification(mod));
	}

}
