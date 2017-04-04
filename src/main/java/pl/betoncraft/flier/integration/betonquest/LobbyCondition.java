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
import pl.betoncraft.flier.api.content.Lobby;

/**
 * Checks if the player is in specified Lobby.
 *
 * @author Jakub Sapalski
 */
public class LobbyCondition extends Condition {
	
	private String lobby;
	private Flier flier;

	public LobbyCondition(Instruction instruction) throws InstructionParseException {
		super(instruction);
		lobby = instruction.next();
		flier = Flier.getInstance();
	}

	@Override
	public boolean check(String playerID) throws QuestRuntimeException {
		UUID uuid = PlayerConverter.getPlayer(playerID).getUniqueId();	
		Lobby l = flier.getLobbies().get(lobby);
		return l != null && l.getPlayers().contains(uuid);
	}

}
