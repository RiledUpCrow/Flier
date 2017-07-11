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

import org.bukkit.Bukkit;
import org.bukkit.Location;
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
	private Optional<Location> lastLoc = Optional.empty();

	public SprintStartingAction(ConfigurationSection section) throws LoadingException {
		super(section, false, false);
		max = (float) loader.loadPositiveDouble("max", (double) WALK_SPEED * 4);
		if (max > 1) {
			throw new LoadingException("Value of 'max' speed must be lower than 1");
		}
		step = (float) (max - WALK_SPEED) / ((float) loader.loadPositiveDouble("time", 5.0) * 20);
	}

	@Override
	public boolean act(Optional<InGamePlayer> creator, Optional<InGamePlayer> source,
			InGamePlayer target, Optional<UsableItem> item) {
		Player player = target.getPlayer();
		if (player.isSprinting() && ((Entity) player).isOnGround()) {
			Location loc = player.getLocation().clone();
			Vector vec;
			// record player's location and direction
			if (!lastLoc.isPresent() || !direction.isPresent()) {
				direction = Optional.of(loc.getYaw());
				vec = loc.getDirection();
			} else {
				vec = loc.toVector().subtract(lastLoc.get().toVector());
			}
			float dir = loc.clone().setDirection(vec).getYaw();
			lastLoc = Optional.of(loc);
			// direction must be correct (player is running straight)
			if (Math.abs(standarize(dir) - standarize(direction.get())) < 15) {
				// reset the stopper so it doesn't break this speed up
				if (stopper.isPresent()) {
					stopper.get().cancel();
				}
				// schedule it again to reset the speed in case the player stops
				stopper = Optional.of(new BukkitRunnable() {
					@Override
					public void run() {
						reset(player);
					}
				});
				stopper.get().runTaskLater(Flier.getInstance(), 1);
				// increase the speed or start if it's maxed
				if (player.getWalkSpeed() < max) {
					// increase the speed
					player.setWalkSpeed(Math.min(max, player.getWalkSpeed() + step));
				} else {
					// speed maxed, take-off
					Vector vel = player.getLocation().getDirection().setY(0.3).normalize().multiply(max);
					Runnable takeoff = () -> {
						player.setGliding(true);
						player.setVelocity(vel);
						player.setSneaking(true);
					};
					for (int i = 0; i < 4; i++) {
						Bukkit.getScheduler().runTaskLater(Flier.getInstance(), takeoff, i);
					}
				}
			} else {
				reset(player);
			}
		}
		return true;
	}
	
	private void reset(Player player) {
		stopper = Optional.empty();
		direction = Optional.empty();
		lastLoc = Optional.empty();
		player.setWalkSpeed(WALK_SPEED);
	}
	
	private float standarize(float yaw) {
		if (yaw > 0) {
			return yaw;
		} else {
			return 360 + yaw;
		}
	}

}
