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
package pl.betoncraft.flier.bonus;

import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.util.Vector;

import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.api.core.Attacker;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.Target;
import pl.betoncraft.flier.api.core.UsableItem;

/**
 * An entity based Bonus type which also gets collected upon being hit with a
 * weapon.
 *
 * @author Jakub Sapalski
 */
public class TargetBonus extends EntityBonus implements Target {
	
	protected Attacker attacker;
	protected int noDamageTicks = 0;

	public TargetBonus(ConfigurationSection section, Game game, Optional<InGamePlayer> creator,
			Optional<UsableItem> item) throws LoadingException {
		super(section, game, creator, item);
	}
	
	@Override
	public void release() {
		super.release();
		entity.setInvulnerable(false);
		game.getTargets().put(entity.getUniqueId(), this);
	}
	
	@Override
	public void block() {
		super.block();
		if (entity != null) {
			game.getTargets().remove(entity.getUniqueId());
		}
	}

	@Override
	public Attacker getAttacker() {
		return attacker;
	}

	@Override
	public void setAttacker(Attacker attacker) {
		this.attacker = attacker;
	}

	@Override
	public boolean handleHit(Attacker attacker) {
		apply(attacker.getCreator());
		return true;
	}

	@Override
	public Location getLocation() {
		return location;
	}

	@Override
	public Vector getVelocity() {
		return new Vector();
	}

	@Override
	public Game getGame() {
		return game;
	}

	@Override
	public void setNoDamageTicks(int noDamageTicks) {
		this.noDamageTicks = noDamageTicks;
	}

	@Override
	public int getNoDamageTicks() {
		return noDamageTicks;
	}

	@Override
	public boolean isTargetable() {
		return isAvailable();
	}
	
	@Override
	public String getName() {
		return id;
	}

}
