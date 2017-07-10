/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
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
import pl.betoncraft.flier.api.core.Target;
import pl.betoncraft.flier.api.core.UsableItem;
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

	public Bomb(ConfigurationSection section) throws LoadingException {
		super(section);
		yield = (float) loader.loadPositiveDouble(POWER);
		fuse = loader.loadNonNegativeInt(FUSE, 80);
		// register a single listener for all bombs
		if (listener == null) {
			listener = new BombListener();
			Bukkit.getPluginManager().registerEvents(listener, Flier.getInstance());
		}
	}

	@Override
	public boolean act(Optional<InGamePlayer> source, InGamePlayer target, Optional<UsableItem> item) {
		TNTPrimed tnt = (TNTPrimed) target.getPlayer().getWorld().spawnEntity(
				target.getPlayer().getLocation(), EntityType.PRIMED_TNT);
		Attacker.saveAttacker(tnt, new DefaultAttacker(this, source.orElse(null), item.orElse(null)));
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
				Target target = attacker.getShooter().getGame().getTargets().get(event.getEntity().getUniqueId());
				if (target != null && target.isTargetable()) {
					attacker.getShooter().getGame().handleHit(target, attacker);
				}
			}
		}

	}

}
