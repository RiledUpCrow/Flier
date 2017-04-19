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
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
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
	
	private Optional<BukkitRunnable> stopper = Optional.empty();
	private Optional<Float> direction = Optional.empty();

	public SprintStartingAction(ConfigurationSection section) throws LoadingException {
		super(section);
		max = (float) loader.loadPositiveDouble("max", (double) WALK_SPEED * 4);
		if (max > 1) {
			throw new LoadingException("Value of 'max' speed must be lower than 1");
		}
		step = (float) (max - WALK_SPEED) / ((float) loader.loadPositiveDouble("time", 5.0) * 20);
	}

	@Override
	public boolean act(InGamePlayer data, UsableItem item) {
		Player player = data.getPlayer();
		if (player.isSprinting() && ((Entity) player).isOnGround()) {
			// record player's direction
			float dir = player.getLocation().getYaw();
			if (!direction.isPresent()) {
				direction = Optional.of(dir);
			}
			// direction must be correct (player is running straight)
			if (Math.abs(dir - direction.get()) < 15) {
				// reset the stopper so it doesn't break this speed up
				reset(player);
				if (player.getWalkSpeed() < max) {
					// increase the speed
					player.setWalkSpeed(Math.min(max, player.getWalkSpeed() + step));
				} else {
					// speed maxed, take-off
					Vector vel = player.getLocation().getDirection().setY(0.3).normalize().multiply(max);
					player.setVelocity(vel.multiply(2));
					Bukkit.getScheduler().runTaskLater(Flier.getInstance(), () -> {
						player.setGliding(true);
						player.setVelocity(vel.multiply(0.5));
						player.setSneaking(true);
					}, 1);
				}
			}
		}
		return true;
	}
	
	private void reset(Player player) {
		if (stopper.isPresent()) {
			stopper.get().cancel();
		}
		stopper = Optional.of(new BukkitRunnable() {
			@Override
			public void run() {
				stopper = Optional.empty();
				direction = Optional.empty();
				player.setWalkSpeed(WALK_SPEED);
			}
		});
		stopper.get().runTaskLater(Flier.getInstance(), 1);
	}

}
