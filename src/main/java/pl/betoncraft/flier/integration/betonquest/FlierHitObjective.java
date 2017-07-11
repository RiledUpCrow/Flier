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
import pl.betoncraft.flier.event.FlierPlayerHitEvent;

/**
 * BetonQuest objective which gets completed when the player hits someone in Flier game.
 *
 * @author Jakub Sapalski
 */
public class FlierHitObjective extends Objective implements Listener {

	private int amount = 1;
	private String name = null;
	private ConditionID[] required;
	private boolean notify = false;

	public FlierHitObjective(Instruction instruction) throws InstructionParseException {
		super(instruction);
		template = HitData.class;
		amount = instruction.getInt();
		if (amount < 1) {
			throw new InstructionParseException("Amount cannot be less than 0");
		}
		name = instruction.getOptional("name");
		required = instruction.getList(instruction.getOptional("required"), e -> instruction.getCondition(e))
				.toArray(new ConditionID[0]);
	}
	
	@EventHandler
	public void onKill(FlierPlayerHitEvent event) {
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
			HitData data = (HitData) dataMap.get(killer);
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
	
	public static class HitData extends ObjectiveData {
		
		private int amount;
		
		public HitData(String instruction, String playerID, String objID) {
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
