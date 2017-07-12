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
package pl.betoncraft.flier.api;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

import pl.betoncraft.flier.api.content.Action;
import pl.betoncraft.flier.api.content.Activator;
import pl.betoncraft.flier.api.content.Bonus;
import pl.betoncraft.flier.api.content.Effect;
import pl.betoncraft.flier.api.content.Engine;
import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.api.content.Lobby;
import pl.betoncraft.flier.api.content.Wings;
import pl.betoncraft.flier.api.core.Arena;
import pl.betoncraft.flier.api.core.ConfigManager;
import pl.betoncraft.flier.api.core.DatabaseManager;
import pl.betoncraft.flier.api.core.FancyStuffWrapper;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.ItemSet;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.Modification;
import pl.betoncraft.flier.api.core.NoArenaException;
import pl.betoncraft.flier.api.core.Owner;
import pl.betoncraft.flier.api.core.UsableItem;

/**
 * The Flier plugin.
 *
 * @author Jakub Sapalski
 */
public interface Flier extends Plugin {

	/**
	 * Gets the instance of the Flier plugin. This will return null if it can't
	 * find the plugin for some reason.
	 */
	public static Flier getInstance() {
		Plugin flier = Bukkit.getPluginManager().getPlugin("Flier");
		if (flier == null || !(flier instanceof Flier)) {
			return null;
		} else {
			return (Flier) flier;
		}
	}

	/**
	 * Reloads the plugin.
	 */
	public void reload();

	/**
	 * @return the instance of ConfigManager
	 */
	public ConfigManager getConfigManager();

	/**
	 * @return the instance of DatabaseManager
	 */
	public DatabaseManager getDatabaseManager();

	/**
	 * @return the currently used instance of FancyStuffWrapper, the object
	 *         which manages the integration with plugins responsible for
	 *         displaying titles, action bar messages and tab list headers and
	 *         footers.
	 */
	public FancyStuffWrapper getFancyStuff();
	
	/**
	 * Notifies the plugin that the player has joined a Game. It will update the list of players.
	 * 
	 * @param player player who has joined a Game
	 */
	public void playerJoinsGame(InGamePlayer player);
	
	/**
	 * Notifies the plugin that the player has left a Game. It will update the list of players.
	 * 
	 * @param player player who has left a Game
	 */
	public void playerLeavesGame(InGamePlayer player);

	/**
	 * @return an immutable view of the map containing players who are currently
	 *         in games
	 */
	public Map<UUID, InGamePlayer> getPlayers();

	/**
	 * @return an immutable view of the map containing Lobbies, where the key is
	 *         the Lobby ID
	 */
	public Map<String, Lobby> getLobbies();

	/**
	 * Creates a new instance of an Engine, using data with specified ID from
	 * <i>engines.yml</i> file.
	 * 
	 * @param id
	 *            ID of the Engine
	 * @return the Engine with specified ID, never null
	 * @throws LoadingException
	 *             when the Engine cannot be created due to an error
	 */
	public Engine getEngine(String id) throws LoadingException;

	/**
	 * Creates a new instance of a UsableItem, using data with specified ID from
	 * <i>items.yml</i> file.
	 * 
	 * @param id
	 *            ID of the Item
	 * @param owner
	 *            the player who will own this UsableItem
	 * @return the Item with specified name, never null
	 * @throws LoadingException
	 *             when the Item cannot be created due to an error
	 */
	public UsableItem getItem(String id, InGamePlayer owner) throws LoadingException;

	/**
	 * Creates a new instance of Wings, using data with specified ID from
	 * <i>wings.yml</i> file.
	 * 
	 * @param id
	 *            ID of the Wings
	 * @return the Wings with specified name, never null
	 * @throws LoadingException
	 *             when the Wings cannot be created due to an error
	 */
	public Wings getWing(String id) throws LoadingException;

	/**
	 * Creates a new instance of a Game, using data with specified ID from
	 * <i>games.yml</i> file.
	 * 
	 * @param id
	 *            ID of the Game
	 * @param lobby
	 *            Lobby from which this Game originates
	 * @return the Game with specified name, never null
	 * @throws LoadingException
	 *             when the Game cannot be created due to an error
	 * @throws NoArenaException
	 *             when the Lobby has no free Arenas for this Game
	 */
	public Game getGame(String id, Lobby lobby) throws LoadingException, NoArenaException;

	/**
	 * Creates a new instance of an Action, using data with specified ID from
	 * <i>actions.yml</i> file.
	 * 
	 * @param id
	 *            ID of the Action
	 * @param owner
	 *            the optional Owner of this Action; Actions without Owners are
	 *            considered coming from environment - not all Actions can work
	 *            that way
	 * @return the Action with specified name, never null
	 * @throws LoadingException
	 *             when the Action cannot be created due to an error
	 */
	public Action getAction(String id, Optional<Owner> owner) throws LoadingException;

	/**
	 * Creates a new instance of an Activator, using data with specified ID from
	 * <i>activators.yml</i> file.
	 * 
	 * @param id
	 *            ID of the Activator
	 * @param owner
	 *            the optional Owner of this Activator; Activators without
	 *            Owners are considered coming from environment - not all
	 *            Activators can work that way
	 * @return the Activator with specified name, never null
	 * @throws LoadingException
	 *             when the Activator cannot be created due to an error
	 */
	public Activator getActivator(String id, Optional<Owner> owner) throws LoadingException;

	/**
	 * Creates a new instance of a Bonus, using data with specified ID from
	 * <i>bonuses.yml</i> file.
	 * 
	 * @param id
	 *            ID of the Bonus
	 * @param game
	 *            the Game in which this Bonus was created
	 * @param owner
	 *            the optional Owner of this Bonus; Bonuses without Owners are
	 *            considered coming from environment - not all Bonuses can work
	 *            that way
	 * @return the Bonus with specified name, never null
	 * @throws LoadingException
	 *             when the Bonus cannot be created due to an error, type is not
	 *             defined or Bonus is not defined
	 */
	public Bonus getBonus(String id, Game game, Optional<Owner> owner) throws LoadingException;

	/**
	 * Creates a new instance of a Modification, using data with specified ID
	 * from <i>modifications.yml</i> file.
	 * 
	 * @param id
	 *            ID of the Modification
	 * @return the Modification with specified name, never null
	 * @throws LoadingException
	 *             when the Modification cannot be created due to an error or
	 *             Modification is not defined
	 */
	public Modification getModification(String id) throws LoadingException;

	/**
	 * Creates a new instance of an ItemSet, using data with specified ID from
	 * <i>sets.yml</i> file.
	 * 
	 * @param id
	 *            ID of the ItemSet
	 * @param owner
	 *            the player who will own this ItemSet
	 * @return the ItemSet with specified name, never null
	 * @throws LoadingException
	 *             when the ItemSet cannot be created due to an error or ItemSet
	 *             is not defined
	 */
	public ItemSet getItemSet(String id, InGamePlayer owner) throws LoadingException;

	/**
	 * Creates a new instance of an Effect, using data with specified ID from
	 * <i>effects.yml</i> file.
	 * 
	 * @param id
	 *            ID of the Effect
	 * @return the Effect with specified name, never null
	 * @throws LoadingException
	 *             when the Effect cannot be created due to an error, type is
	 *             not defined or Effect is not defined
	 */
	public Effect getEffect(String id) throws LoadingException;

	/**
	 * Creates a new instance of an Arena, using data with specified ID from
	 * <i>arenas.yml</i> file.
	 * 
	 * @param id
	 *            ID of the Arena
	 * @return the Arena with specified name, never null
	 * @throws LoadingException
	 *             when the Arena cannot be created due to an error or Arena is
	 *             not defined
	 */
	public Arena getArena(String id) throws LoadingException;

	/**
	 * Registers a new Engine type with specified name. The factory will be used
	 * to obtain copies of the Engine.
	 * 
	 * @param name
	 *            name of the type
	 * @param factory
	 *            factory which creates instances of that type
	 */
	public void registerEngine(String name, EngineFactory factory);

	/**
	 * Registers a new Wings type with specified name. The factory will be used
	 * to obtain copies of the Wings.
	 * 
	 * @param name
	 *            name of the type
	 * @param factory
	 *            factory which creates instances of that type
	 */
	public void registerWings(String name, WingsFactory factory);

	/**
	 * Registers a new Lobby type with specified name. The factory will be used
	 * to obtain copies of the Lobby.
	 * 
	 * @param name
	 *            name of the type
	 * @param factory
	 *            factory which creates instances of that type
	 */
	public void registerLobby(String name, LobbyFactory factory);

	/**
	 * Registers a new Game type with specified name. The factory will be used
	 * to obtain copies of the Game.
	 * 
	 * @param name
	 *            name of the type
	 * @param factory
	 *            factory which creates instances of that type
	 */
	public void registerGame(String name, GameFactory factory);

	/**
	 * Registers a new Bonus type with specified name. The factory will be used
	 * to obtain copies of the Bonus.
	 * 
	 * @param name
	 *            name of the type
	 * @param factory
	 *            factory which creates instances of that type
	 */
	public void registerBonus(String name, BonusFactory factory);

	/**
	 * Registers a new Action type with specified name. The factory will be used
	 * to obtain copies of the Action.
	 * 
	 * @param name
	 *            name of the type
	 * @param factory
	 *            factory which creates instances of that type
	 */
	public void registerAction(String name, ActionFactory factory);

	/**
	 * Registers a new Activator type with specified name. The factory will be
	 * used to obtain copies of the Activator.
	 * 
	 * @param name
	 *            name of the type
	 * @param factory
	 *            factory which creates instances of that type
	 */
	public void registerActivator(String name, ActivatorFactory factory);

	/**
	 * Registers a new Effect type with specified name. The factory will be used
	 * to obtain copies of the Effect.
	 * 
	 * @param name
	 *            name of the type
	 * @param factory
	 *            factory which creates instances of that type
	 */
	public void registerEffect(String name, EffectFactory factory);

	/**
	 * Gets the Lobby factory method for the given Lobby type.
	 * 
	 * @param name
	 *            name of the Lobby factory
	 * @return the Lobby factory
	 */
	public LobbyFactory getLobbyFactory(String name);

	/**
	 * Gets the Game factory method for the given Game type.
	 * 
	 * @param name
	 *            name of the Game factory
	 * @return the Game factory
	 */
	public GameFactory getGameFactory(String name);

	/**
	 * Gets the Engine factory method for the given Engine type.
	 * 
	 * @param name
	 *            name of the Engine factory
	 * @return the Engine factory
	 */
	public EngineFactory getEngineFactory(String name);

	/**
	 * Gets the Wings factory method for the given Wings type.
	 * 
	 * @param name
	 *            name of the Wings factory
	 * @return the Wings factory
	 */
	public WingsFactory getWingsFactory(String name);

	/**
	 * Gets the Bonus factory method for the given Bonus type.
	 * 
	 * @param name
	 *            name of the Bonus factory
	 * @return the Bonus factory
	 */
	public BonusFactory getBonusFactory(String name);

	/**
	 * Gets the Action factory method for the given Action type.
	 * 
	 * @param name
	 *            name of the Action factory
	 * @return the Action factory
	 */
	public ActionFactory getActionFactory(String name);

	/**
	 * Gets the Activator factory method for the given Activator type.
	 * 
	 * @param name
	 *            name of the Activator factory
	 * @return the Activator factory
	 */
	public ActivatorFactory getActivatorFactory(String name);

	/**
	 * Gets the Effect factory method for the given Effect type.
	 * 
	 * @param name
	 *            name of the Effect factory
	 * @return the Effect factory
	 */
	public EffectFactory getEffectFactory(String name);

	/**
	 * LobbyFactory is used to create copies of the Lobby type out of
	 * ConfigurationSections.
	 */
	public interface LobbyFactory {
		public Lobby get(ConfigurationSection settings) throws LoadingException;
	}

	/**
	 * GameFactory is used to create copies of the Game type out of
	 * ConfigurationSections. Additional argument, Lobby, will be passed to
	 * indicate parent Lobby of the created Game.
	 */
	public interface GameFactory {
		public Game get(ConfigurationSection settings, Lobby lobby) throws LoadingException, NoArenaException;
	}

	/**
	 * EngineFactory is used to create copies of the Engine type out of
	 * ConfigurationSections.
	 */
	public interface EngineFactory {
		public Engine get(ConfigurationSection settings) throws LoadingException;
	}

	/**
	 * WingsFactory is used to create copies of the Wings type out of
	 * ConfigurationSections.
	 */
	public interface WingsFactory {
		public Wings get(ConfigurationSection settings) throws LoadingException;
	}

	/**
	 * BonusFactory is used to create copies of the Bonus type out of
	 * ConfigurationSections. Additional argument, Game, will be passed to
	 * indicate parent Game of the created Bonus. If the Bonus was
	 * player-created, the Owner object will be passed to this Factory.
	 */
	public interface BonusFactory {
		public Bonus get(ConfigurationSection settings, Game game, Optional<Owner> owner) throws LoadingException;
	}

	/**
	 * ActionFactory is used to create copies of the Action type out of
	 * ConfigurationSections. If the Action was created by a player, an Owner
	 * object will be passed to this Factory.
	 */
	public interface ActionFactory {
		public Action get(ConfigurationSection settings, Optional<Owner> owner) throws LoadingException;
	}

	/**
	 * ActivatorFactory is used to create copies of the Activator type out of
	 * ConfigurationSections. If the Activator was created by a player, an Owner
	 * object will be passed to this Factory.
	 */
	public interface ActivatorFactory {
		public Activator get(ConfigurationSection settings, Optional<Owner> owner) throws LoadingException;
	}

	/**
	 * EffectFactory is used to create copies of the Effect type out of
	 * ConfigurationSections.
	 */
	public interface EffectFactory {
		public Effect get(ConfigurationSection settings) throws LoadingException;
	}

}
