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

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.api.Objective;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.event.FlierUseEvent;

/**
 * BetonQuest objective which gets completed when the player uses a correct UsableItem.
 *
 * @author Jakub Sapalski
 */
public class UsableItemObjective extends Objective implements Listener {
	
	private String item;
	private String usage;

	public UsableItemObjective(Instruction instruction) throws InstructionParseException {
		super(instruction);
		template = ObjectiveData.class;
		item = instruction.getOptional("item");
		usage = instruction.getOptional("usage");
	}
	
	@EventHandler
	public void onUse(FlierUseEvent event) {
		if ((item == null || event.getItem().getID().equals(item)) &&
				(usage == null || event.getUsage().getID().equals(usage))) {
			String playerID = PlayerConverter.getID(event.getPlayer().getPlayer());
			if (containsPlayer(playerID) && checkConditions(playerID)) {
				completeObjective(playerID);
			}
		}
	}

	@Override
	public String getDefaultDataInstruction() {
		return "";
	}

	@Override
	public String getProperty(String s, String s1) {
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
