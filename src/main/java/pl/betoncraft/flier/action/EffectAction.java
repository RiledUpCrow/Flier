/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.action;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.flier.Flier;
import pl.betoncraft.flier.api.Action;
import pl.betoncraft.flier.api.InGamePlayer;
import pl.betoncraft.flier.core.defaults.DefaultAction;
import pl.betoncraft.flier.exception.LoadingException;

/**
 * An action type which adds a specified effect.
 *
 * @author Jakub Sapalski
 */
public class EffectAction extends DefaultAction {
	
	private final List<Action> actions = new ArrayList<>();
	private final int duration;

	public EffectAction(ConfigurationSection section) throws LoadingException {
		super(section);
		for (String actionName : section.getStringList("actions")) {
			Action action = Flier.getInstance().getAction(actionName);
			actions.add(action);
		}
		duration = loader.loadPositiveInt("duration");
	}

	@Override
	public boolean act(InGamePlayer player) {
		new BukkitRunnable() {
			private int i = duration;
			@Override
			public void run() {
				if (i-- == 0) {
					cancel();
				}
				for (Action action : actions) {
					action.act(player);
				}
			}
		}.runTaskTimer(Flier.getInstance(), 0, 1);
		return true;
	}

}
