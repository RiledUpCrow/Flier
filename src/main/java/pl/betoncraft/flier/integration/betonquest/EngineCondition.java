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

import java.util.UUID;

import pl.betoncraft.betonquest.Instruction;
import pl.betoncraft.betonquest.exceptions.InstructionParseException;
import pl.betoncraft.betonquest.exceptions.QuestRuntimeException;
import pl.betoncraft.betonquest.api.Condition;
import pl.betoncraft.betonquest.utils.PlayerConverter;
import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.core.InGamePlayer;

/**
 * BetonQuest condition which checks if the player is using his engine.
 *
 * @author Jakub Sapalski
 */
public class EngineCondition extends Condition {

	private Flier flier;

	public EngineCondition(Instruction instruction) throws InstructionParseException {
		super(instruction, true);
		flier = Flier.getInstance();
	}


	@Override
	public Boolean execute(String playerID) throws QuestRuntimeException {
		UUID uuid = PlayerConverter.getPlayer(playerID).getUniqueId();
		InGamePlayer player = flier.getPlayers().get(uuid);
		return player != null && player.isAccelerating();
	}

}
