/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api.core;

import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.event.FlierPlayerKillEvent.KillType;

/**
 * Manages the communication between the plugin and database.
 *
 * @author Jakub Sapalski
 */
public interface DatabaseManager {

	public void disconnect();

	public void saveKill(Game game, InGamePlayer killed, InGamePlayer killer, UsableItem weapon, KillType type);

}
