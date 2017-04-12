/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.action;

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
		super(section);
		speed = loader.loadPositiveDouble(SPEED);
	}

	@Override
	public boolean act(InGamePlayer player, UsableItem item) {
		Runnable launch = () -> {
			Vector vel = player.getPlayer().getLocation().getDirection().multiply(modMan.modifyNumber(SPEED, speed));
			player.getPlayer().setVelocity(vel);
			if (!player.getPlayer().isGliding()) {
				Bukkit.getScheduler().runTask(Flier.getInstance(), () -> {
					player.getPlayer().setGliding(true);
					player.getPlayer().setVelocity(vel);
				});
			}
		};
		if (((Entity) player.getPlayer()).isOnGround()) {
			player.getPlayer().setVelocity(new Vector(0, 2, 0));
			Bukkit.getScheduler().runTaskLater(Flier.getInstance(), launch, 5);
		} else {
			launch.run();
		}
		return true;
	}

}
