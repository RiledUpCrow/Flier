/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.integration.betonquest;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.betonquest.ConditionID;
import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.config.Config;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.event.FlierPlayerKillEvent;

/**
 * BetonQuest objective which gets completed when the player kills someone in Flier game.
 *
 * @author Jakub Sapalski
 */
public class FlierKillObjective extends Objective implements Listener {

	private int amount = 1;
	private String name = null;
	private ConditionID[] required;
	private boolean notify = false;

	public FlierKillObjective(Instruction instruction) throws InstructionParseException {
		super(instruction);
		template = KillData.class;
		amount = instruction.getInt();
		if (amount < 1) {
			throw new InstructionParseException("Amount cannot be less than 0");
		}
		name = instruction.getOptional("name");
		required = instruction.getList(instruction.getOptional("required"), e -> instruction.getCondition(e))
				.toArray(new ConditionID[0]);
	}
	
	@EventHandler
	public void onKill(FlierPlayerKillEvent event) {
		event.setSwitched(false);
		String victim = PlayerConverter.getID(event.getPlayer().getPlayer());
		String killer = PlayerConverter.getID(event.getOther().getPlayer());
		if (!victim.equals(killer) && containsPlayer(killer)) {
			if (name != null && !event.getOther().getPlayer().getName().equalsIgnoreCase(name)) {
				return;
			}
			for (ConditionID condition : required) {
				if (!BetonQuest.condition(victim, condition)) {
					return;
				}
			}
			if (!checkConditions(killer)) {
				return;
			}
			KillData data = (KillData) dataMap.get(killer);
			data.kill();
			if (data.getLeft() <= 0) {
				completeObjective(killer);
			} else if (notify) {
				Config.sendMessage(killer, "players_to_kill", new String[] { String.valueOf(data.getLeft()) });
			}
		}
	}

	@Override
	public String getDefaultDataInstruction() {
		return String.valueOf(amount);
	}

	@Override
	public void start() {
		Bukkit.getPluginManager().registerEvents(this, Flier.getInstance());
	}

	@Override
	public void stop() {
		HandlerList.unregisterAll(this);
	}
	
	public static class KillData extends ObjectiveData {
		
		private int amount;
		
		public KillData(String instruction, String playerID, String objID) {
			super(instruction, playerID, objID);
			amount = Integer.parseInt(instruction);
		}
		
		public void kill() {
			amount--;
			update();
		}
		
		public int getLeft() {
			return amount;
		}
		
		@Override
		public String toString() {
			return String.valueOf(amount);
		}
		
	}

}
