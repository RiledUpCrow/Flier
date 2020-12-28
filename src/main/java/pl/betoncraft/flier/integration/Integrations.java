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
package pl.betoncraft.flier.integration;

import org.bukkit.Bukkit;

import pl.betoncraft.betonquest.BetonQuest;
import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.integration.betonquest.ButtonObjective;
import pl.betoncraft.flier.integration.betonquest.EngineCondition;
import pl.betoncraft.flier.integration.betonquest.FlierDeathObjective;
import pl.betoncraft.flier.integration.betonquest.FlierGetHitObjective;
import pl.betoncraft.flier.integration.betonquest.FlierHitObjective;
import pl.betoncraft.flier.integration.betonquest.FlierKillObjective;
import pl.betoncraft.flier.integration.betonquest.FlierRespawnObjective;
import pl.betoncraft.flier.integration.betonquest.GameCondition;
import pl.betoncraft.flier.integration.betonquest.JoinGameObjective;
import pl.betoncraft.flier.integration.betonquest.JoinLobbyObjective;
import pl.betoncraft.flier.integration.betonquest.LobbyCondition;
import pl.betoncraft.flier.integration.betonquest.UsableItemObjective;

/**
 * Loads all possible integrations.
 *
 * @author Jakub Sapalski
 */
public class Integrations {
	
	public Integrations() {
		Flier flier = Flier.getInstance();
		if (Bukkit.getPluginManager().isPluginEnabled("BetonQuest")) {
			BetonQuest bq = BetonQuest.getInstance();
			bq.registerConditions("ingame", GameCondition.class);
			bq.registerConditions("inlobby", LobbyCondition.class);
			bq.registerConditions("flierengine", EngineCondition.class);
			bq.registerObjectives("joinlobby", JoinLobbyObjective.class);
			bq.registerObjectives("joingame", JoinGameObjective.class);
			bq.registerObjectives("flierrespawn", FlierRespawnObjective.class);
			bq.registerObjectives("flieruse", UsableItemObjective.class);
			bq.registerObjectives("flierkill", FlierKillObjective.class);
			bq.registerObjectives("flierhit", FlierHitObjective.class);
			bq.registerObjectives("flierdeath", FlierDeathObjective.class);
			bq.registerObjectives("fliergethit", FlierGetHitObjective.class);
			bq.registerObjectives("flierbutton", ButtonObjective.class);
			flier.getLogger().info("Hooked into BetonQuest!");
		}
		if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			flier.getLogger().info("Hooked into PlaceholderAPI!");
		}
	}

}
