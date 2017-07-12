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
package pl.betoncraft.flier.action.attack;

import java.util.Optional;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.core.Attacker;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.Owner;
import pl.betoncraft.flier.api.core.Target;
import pl.betoncraft.flier.core.DefaultAttacker;

/**
 * Spawns a TNT which explodes immediately.
 *
 * @author Jakub Sapalski
 */
public class Bomb extends DefaultAttack {

	private static final String POWER = "power";
	private static final String FUSE = "fuse";
	
	private static BombListener listener;

	private final float yield;
	private final int fuse;

	public Bomb(ConfigurationSection section, Optional<Owner> owner) throws LoadingException {
		super(section, owner);
		yield = (float) loader.loadPositiveDouble(POWER);
		fuse = loader.loadNonNegativeInt(FUSE, 80);
		// register a single listener for all bombs
		if (listener == null) {
			listener = new BombListener();
			Bukkit.getPluginManager().registerEvents(listener, Flier.getInstance());
		}
	}

	@Override
	public boolean act(InGamePlayer target, InGamePlayer source) {
		TNTPrimed tnt = (TNTPrimed) target.getPlayer().getWorld().spawnEntity(
				target.getPlayer().getLocation(), EntityType.PRIMED_TNT);
		Attacker.saveAttacker(tnt, new DefaultAttacker(this, owner.get().getPlayer(), target, owner.get().getItem()));
		tnt.setIsIncendiary(false);
		tnt.setVelocity(target.getPlayer().getVelocity());
		tnt.setYield((float) modMan.modifyNumber(POWER, yield));
		tnt.setFuseTicks((int) modMan.modifyNumber(FUSE, fuse));
		return true;
	}
	
	public class BombListener implements Listener {
			
		@EventHandler(priority=EventPriority.LOW)
		public void onHit(EntityDamageByEntityEvent event) {
			if (event.isCancelled()) {
				return;
			}
			Attacker attacker = Attacker.getAttacker(event.getDamager());
			if (attacker != null && attacker.getDamager() instanceof Bomb) {
				event.setCancelled(true);
				Target target = attacker.getCreator().getGame().getTargets().get(event.getEntity().getUniqueId());
				if (target != null && target.isTargetable()) {
					attacker.getCreator().getGame().handleHit(target, attacker);
				}
			}
		}

	}

}
