/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.integration.betonquest;

import java.util.HashSet;
import java.util.UUID;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.InstructionParseException;
import pl.betoncraft.betonquest.QuestRuntimeException;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.flier.api.Flier;

/**
 * Checks if the player is in specified Game
 *
 * @author Jakub Sapalski
 */
public class GameCondition extends Condition {
	
	private String game;
	private Flier flier;

	public GameCondition(Instruction instruction) throws InstructionParseException {
		super(instruction);
		game = instruction.next();
		flier = Flier.getInstance();
	}

	@Override
	public boolean check(String playerID) throws QuestRuntimeException {
		UUID uuid = PlayerConverter.getPlayer(playerID).getUniqueId();	
		return flier.getLobbies().values().stream()
				.filter(lobby -> lobby.getPlayers().contains(uuid))
				.findAny()
				.map(lobby -> lobby.getGames().getOrDefault(game, new HashSet<>()))
				.map(games -> games.stream().anyMatch(game -> game.getPlayers().containsKey(uuid)))
				.orElse(false);
	}

}
