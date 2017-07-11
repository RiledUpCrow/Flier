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
