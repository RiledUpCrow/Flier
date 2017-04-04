/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.integration.betonquest;

import java.util.UUID;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Activator;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.LoadingException;

/**
 * BetonQuest condition which checks if an Activator is active.
 *
 * @author Jakub Sapalski
 */
public class ActivatorCondition extends Condition {
	
	private String name;
	private Activator activator;

	public ActivatorCondition(Instruction instruction) throws InstructionParseException {
		super(instruction);
		name = instruction.next();
	}

	@Override
	public boolean check(String playerID) throws QuestRuntimeException {
		if (activator == null) {
			try {
				activator = Flier.getInstance().getActivator(name);
			} catch (LoadingException e) {
				throw new QuestRuntimeException("Error in " + name + " activator");
			}
		}
		if (activator == null) {
			throw new QuestRuntimeException("Activator " + name + " is not defined");
		}
		UUID uuid = PlayerConverter.getPlayer(playerID).getUniqueId();
		InGamePlayer player = Flier.getInstance().getPlayers().get(uuid);
		return player != null && activator.isActive(player, player.getHeldItem());
	}

}
