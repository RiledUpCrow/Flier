/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.action;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.UsableItem;

/**
 * Launches players in the direction of looking.
 *
 * @author Jakub Sapalski
 */
public class LaunchAction extends DefaultAction {
	
	private static final String SPEED = "speed";

	private final double speed;

	public LaunchAction(ConfigurationSection section) throws LoadingException {
		super(section, false, false);
		speed = loader.loadPositiveDouble(SPEED);
	}

	@Override
	public boolean act(Optional<InGamePlayer> source, InGamePlayer target, Optional<UsableItem> item) {
		Runnable launch = () -> {
			Vector vel = target.getPlayer().getLocation().getDirection().multiply(modMan.modifyNumber(SPEED, speed));
			target.getPlayer().setVelocity(vel);
			if (!target.getPlayer().isGliding()) {
				Bukkit.getScheduler().runTask(Flier.getInstance(), () -> {
					target.getPlayer().setGliding(true);
					target.getPlayer().setVelocity(vel);
				});
			}
		};
		if (((Entity) target.getPlayer()).isOnGround()) {
			target.getPlayer().setVelocity(new Vector(0, 2, 0));
			Bukkit.getScheduler().runTaskLater(Flier.getInstance(), launch, 5);
		} else {
			launch.run();
		}
		return true;
	}

}
