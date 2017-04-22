/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.activator;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.UsableItem;

/**
 * Activates when the player is standing on a correct block.
 *
 * @author Jakub Sapalski
 */
public class BlockStandingActivator extends DefaultActivator {
	
	private Material block;

	public BlockStandingActivator(ConfigurationSection section) throws LoadingException {
		super(section);
		block = loader.loadEnum("block", Material.class);
		if (!block.isBlock() || !block.isSolid()) {
			throw new LoadingException(String.format("Material '%s' is not a solid block.", block));
		}
	}

	@Override
	public boolean isActive(InGamePlayer player, UsableItem item) {
		return player.getLocation().add(0, -1, 0).getBlock().getType() == block;
	}

}
