/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.integration.betonquest;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.event.FlierPlayerHitEvent;

/**
 * BetonQuest objective which gets completed when the player gets hit in Flier game.
 *
 * @author Jakub Sapalski
 */
public class FlierGetHitObjective extends Objective implements Listener {

	public FlierGetHitObjective(Instruction instruction) throws InstructionParseException {
		super(instruction);
		template = ObjectiveData.class;
	}
	
	@EventHandler
	public void onDeath(FlierPlayerHitEvent event) {
		event.setSwitched(false);
		Player player = event.getPlayer().getPlayer();
		String playerID = PlayerConverter.getID(player);
		if (containsPlayer(playerID) && checkConditions(playerID)) {
			completeObjective(playerID);
		}
	}

	@Override
	public String getDefaultDataInstruction() {
		return "";
	}

	@Override
	public void start() {
		Bukkit.getPluginManager().registerEvents(this, Flier.getInstance());
	}

	@Override
	public void stop() {
		HandlerList.unregisterAll(this);
	}

}
