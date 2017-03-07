/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.event;

import java.util.Arrays;
import java.util.List;

import org.bukkit.event.Cancellable;

import pl.betoncraft.flier.api.core.Damager;
import pl.betoncraft.flier.api.core.Damager.DamageResult;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.core.MatchingTwoPlayersEvent;

/**
 * Called when the player hits someone with a Damager.
 *
 * @author Jakub Sapalski
 */
public class FlierHitPlayerEvent extends MatchingTwoPlayersEvent implements Cancellable {
	
	private static final List<DamageResult> all = Arrays.asList(DamageResult.values());

	private List<DamageResult> result;
	private Damager damager;
	private boolean cancel = false;

	/**
	 * Creates new MatchingTwoPlayersEvent with details about hitting the player.
	 * 
	 * @param player
	 */
	public FlierHitPlayerEvent(InGamePlayer player, InGamePlayer target, List<DamageResult> results, Damager damager) {
		super(player, target, "target_", "shooter_");
		this.result = results;
		this.damager = damager;
		booleans.put("self_hit", player.equals(target));
		for (DamageResult result : all) {
			booleans.put(result.toString().toLowerCase(), results.contains(result));
		}
		strings.put("attitude", player.getLobby().getGame().getAttitude(player, target).toString());
		numbers.put("damage_to_wings", damager.getDamage());
		numbers.put("damage_to_health", damager.getPhysical());
	}

	/**
	 * @return the damager which hit the other player
	 */
	public Damager getDamager() {
		return damager;
	}
	
	/**
	 * @return the list of results of the attack
	 */
	public List<DamageResult> getResult() {
		return result;
	}

	@Override
	public boolean isCancelled() {
		return cancel;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancel = cancel;
	}

}
