/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
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
		apply(attacker.getShooter());
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
