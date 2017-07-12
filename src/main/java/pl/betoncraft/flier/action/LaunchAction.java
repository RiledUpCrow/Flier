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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.Owner;

/**
 * Launches players in the direction of looking.
 *
 * @author Jakub Sapalski
 */
public class LaunchAction extends DefaultAction {
	
	private static final String SPEED = "speed";

	private final double speed;

	public LaunchAction(ConfigurationSection section, Optional<Owner> owner) throws LoadingException {
		super(section, owner);
		speed = loader.loadPositiveDouble(SPEED);
	}

	@Override
	public boolean act(InGamePlayer target, InGamePlayer source) {
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
