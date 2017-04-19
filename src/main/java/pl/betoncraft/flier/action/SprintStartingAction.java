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
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.UsableItem;

/**
 * Takes off the player after he sprints up to a required speed.
 *
 * @author Jakub Sapalski
 */
public class SprintStartingAction extends DefaultAction {

	private static final float WALK_SPEED = 0.20300001f;
	private final float max;
	private final float step;

	public SprintStartingAction(ConfigurationSection section) throws LoadingException {
		super(section);
		max = (float) loader.loadPositiveDouble("max", (double) WALK_SPEED * 4);
		step = (float) (max - WALK_SPEED) / (loader.loadPositiveInt("time", 5) * 20);
	}

	@Override
	public boolean act(InGamePlayer data, UsableItem item) {
		Player player = data.getPlayer();
		// sprint starting
		if (player.isSprinting() && ((Entity) player).isOnGround()) {
			if (player.getWalkSpeed() < max) {
				player.setWalkSpeed(player.getWalkSpeed() + step);
			} else {
				Vector vel = player.getLocation().getDirection().setY(0.5).normalize().multiply(max);
				player.setVelocity(vel.multiply(2));
				Bukkit.getScheduler().runTaskLater(Flier.getInstance(), () -> {
					player.setGliding(true);
					player.setVelocity(vel.multiply(0.5));
				}, 1);
				player.setWalkSpeed(WALK_SPEED);
			}
		} else {
			player.setWalkSpeed(WALK_SPEED);
		}
		return true;
	}

}
