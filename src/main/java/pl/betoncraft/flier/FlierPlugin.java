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
package pl.betoncraft.flier;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.bstats.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.flier.action.ConsumeAction;
import pl.betoncraft.flier.action.EffectAction;
import pl.betoncraft.flier.action.FuelAction;
import pl.betoncraft.flier.action.HealthAction;
import pl.betoncraft.flier.action.ItemSetAction;
import pl.betoncraft.flier.action.LaunchAction;
import pl.betoncraft.flier.action.LeaveGameAction;
import pl.betoncraft.flier.action.MoneyAction;
import pl.betoncraft.flier.action.ScoreAction;
import pl.betoncraft.flier.action.SprintStartingAction;
import pl.betoncraft.flier.action.SuicideAction;
import pl.betoncraft.flier.action.TargetAction;
import pl.betoncraft.flier.action.WingsHealthAction;
import pl.betoncraft.flier.action.WingsOffAction;
import pl.betoncraft.flier.action.attack.Bomb;
import pl.betoncraft.flier.action.attack.Explosion;
import pl.betoncraft.flier.action.attack.HomingMissile;
import pl.betoncraft.flier.action.attack.ParticleGun;
import pl.betoncraft.flier.action.attack.ProjectileGun;
import pl.betoncraft.flier.activator.AmmoActivator;
import pl.betoncraft.flier.activator.BlockStandingActivator;
import pl.betoncraft.flier.activator.HoldingThisActivator;
import pl.betoncraft.flier.activator.IntervalActivator;
import pl.betoncraft.flier.activator.ItemActivator;
import pl.betoncraft.flier.activator.TriggerActivator;
import pl.betoncraft.flier.activator.WingsHealthActivator;
import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Action;
import pl.betoncraft.flier.api.content.Activator;
import pl.betoncraft.flier.api.content.Bonus;
import pl.betoncraft.flier.api.content.Effect;
import pl.betoncraft.flier.api.content.Engine;
import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.api.content.Lobby;
import pl.betoncraft.flier.api.content.Wings;
import pl.betoncraft.flier.api.core.Arena;
import pl.betoncraft.flier.api.core.Attacker;
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
import pl.betoncraft.flier.bonus.EntityBonus;
import pl.betoncraft.flier.bonus.ProximityBonus;
import pl.betoncraft.flier.bonus.TargetBonus;
import pl.betoncraft.flier.command.FlierCommand;
import pl.betoncraft.flier.core.DefaultArena;
import pl.betoncraft.flier.core.DefaultModification;
import pl.betoncraft.flier.core.DefaultSet;
import pl.betoncraft.flier.core.DefaultUsableItem;
import pl.betoncraft.flier.effect.GameSoundEffect;
import pl.betoncraft.flier.effect.GlowingEffect;
import pl.betoncraft.flier.effect.ParticleEffect;
import pl.betoncraft.flier.effect.PrivateSoundEffect;
import pl.betoncraft.flier.effect.PublicSoundEffect;
import pl.betoncraft.flier.engine.MultiplyingEngine;
import pl.betoncraft.flier.game.DeathMatchGame;
import pl.betoncraft.flier.game.TeamDeathMatch;
import pl.betoncraft.flier.integration.Integrations;
import pl.betoncraft.flier.lobby.PhysicalLobby;
import pl.betoncraft.flier.stats.StatisticWriter;
import pl.betoncraft.flier.util.Coordinator;
import pl.betoncraft.flier.util.DefaultConfigManager;
import pl.betoncraft.flier.util.DefaultDatabaseManager;
import pl.betoncraft.flier.util.DefaultFancyStuffWrapper;
import pl.betoncraft.flier.util.LangManager;
import pl.betoncraft.flier.util.Utils;
import pl.betoncraft.flier.wings.SimpleWings;

public class FlierPlugin extends JavaPlugin implements Flier {
	
	private ConfigManager configManager;
	private DatabaseManager databaseManager;
	private FancyStuffWrapper fancyStuff;
	private FlierCommand flierCommand;
	private Listener autoJoin;

	private Map<String, EngineFactory> engineTypes = new HashMap<>();
	private Map<String, WingsFactory> wingTypes = new HashMap<>();
	private Map<String, GameFactory> gameTypes = new HashMap<>();
	private Map<String, LobbyFactory> lobbyTypes = new HashMap<>();
	private Map<String, BonusFactory> bonusTypes = new HashMap<>();
	private Map<String, ActionFactory> actionTypes = new HashMap<>();
	private Map<String, ActivatorFactory> activatorTypes = new HashMap<>();
	private Map<String, EffectFactory> effectTypes = new HashMap<>();
	
	private Map<String, Lobby> lobbies = new HashMap<>();
	private Map<UUID, InGamePlayer> players = new HashMap<>();

	@Override
	public void onEnable() {
		configManager = new DefaultConfigManager();
		fancyStuff = new DefaultFancyStuffWrapper();
		flierCommand = new FlierCommand();
		getCommand("flier").setExecutor(flierCommand);
		new LangManager();

		// register new types
		registerEngine("multiplyingEngine", s -> new MultiplyingEngine(s));
		registerWings("simpleWings", s -> new SimpleWings(s));
		registerLobby("physicalLobby", s -> new PhysicalLobby(s));
		registerGame("teamDeathMatch", (s, l) -> new TeamDeathMatch(s, l));
		registerGame("deathMatch", (s, l) -> new DeathMatchGame(s, l));
		registerBonus("entity", (s, g, o) -> new EntityBonus(s, g, o));
		registerBonus("invisible", (s, g, o) -> new ProximityBonus(s, g, o));
		registerBonus("target", (s, g, o) -> new TargetBonus(s, g, o));
		registerAction("leave", (s, o) -> new LeaveGameAction(s, o));
		registerAction("projectileGun", (s, o) -> new ProjectileGun(s, o));
		registerAction("particleGun", (s, o) -> new ParticleGun(s, o));
		registerAction("homingMissile", (s, o) -> new HomingMissile(s, o));
		registerAction("bomb", (s, o) -> new Bomb(s, o));
		registerAction("explosion", (s, o) -> new Explosion(s, o));
		registerAction("suicide", (s, o) -> new SuicideAction(s, o));
		registerAction("launcher", (s, o) -> new LaunchAction(s, o));
		registerAction("effect", (s, o) -> new EffectAction(s, o));
		registerAction("money", (s, o) -> new MoneyAction(s, o));
		registerAction("wingsHealth", (s, o) -> new WingsHealthAction(s, o));
		registerAction("fuel", (s, o) -> new FuelAction(s, o));
		registerAction("targetCompass", (s, o) -> new TargetAction(s, o));
		registerAction("itemSet", (s, o) -> new ItemSetAction(s, o));
		registerAction("consume", (s, o) -> new ConsumeAction(s, o));
		registerAction("health", (s, o) -> new HealthAction(s, o));
		registerAction("sprintStarting", (s, o) -> new SprintStartingAction(s, o));
		registerAction("wingsOff", (s, o) -> new WingsOffAction(s, o));
		registerAction("score", (s, o) -> new ScoreAction(s, o));
		registerActivator("trigger", (s, o) -> new TriggerActivator(s, o));
		registerActivator("holdingThis", (s, o) -> new HoldingThisActivator(s, o));
		registerActivator("interval", (s, o) -> new IntervalActivator(s, o));
		registerActivator("wingsHealth", (s, o) -> new WingsHealthActivator(s, o));
		registerActivator("item", (s, o) -> new ItemActivator(s, o));
		registerActivator("ammo", (s, o) -> new AmmoActivator(s, o));
		registerActivator("blockStanding", (s, o) -> new BlockStandingActivator(s, o));
		registerEffect("publicSound", s -> new PublicSoundEffect(s));
		registerEffect("privateSound", s -> new PrivateSoundEffect(s));
		registerEffect("gameSound", s -> new GameSoundEffect(s));
		registerEffect("particle", s -> new ParticleEffect(s));
		registerEffect("glow", s -> new GlowingEffect(s));
		
		// add projectile cleanup listener
		Bukkit.getPluginManager().registerEvents(new Listener() {
			@EventHandler
			public void onChunkUnload(ChunkUnloadEvent event) {
				Entity[] entities = event.getChunk().getEntities();
				for (int i = 0; i < entities.length; i++) {
					if (entities[i] instanceof Projectile && Attacker.getAttacker((Projectile) entities[i]) != null) {
						entities[i].remove();
					}
				}
			}
		}, this);
		
		new Coordinator(); // temporary solution
		
		new Integrations();
		
		// TODO add after-crash player restore
		
		// schedule loading after all plugins are enabled
		Bukkit.getScheduler().runTask(this, () -> reload());
		
		// start metrics
		Metrics metrics = new Metrics(this);
		metrics.addCustomChart(new Metrics.SingleLineChart("ingame_players") {
			@Override
			public int getValue() {
				return players.size();
			}
		});
		metrics.addCustomChart(new Metrics.AdvancedPie("game_types") {
			@Override
			public HashMap<String, Integer> getValues(HashMap<String, Integer> map) {
				for (Lobby lobby : lobbies.values()) {
					for (List<Game> set : lobby.getGames().values()) {
						for (Game game : set) {
							String name;
							if (game instanceof DeathMatchGame) {
								name = "DeathMatch";
							} else if (game instanceof TeamDeathMatch) {
								name = "Team DeathMatch";
							} else {
								name = "Custom";
							}
							int amount = map.computeIfAbsent(name, key -> 0);
							amount++;
							map.put(name, amount);
						}
					}
				}
				return map;
			}
		});
		
		// database stuff
		databaseManager = new DefaultDatabaseManager();
		new StatisticWriter(databaseManager);
		
		getLogger().info("Flier enabled!");
	}

	@Override
	public void onDisable() {
		for (Lobby lobby : lobbies.values()) {
			lobby.stop();
		}
		databaseManager.disconnect();
	}

	@Override
	public void reload() {
		try {
			// reload configuration files
			reloadConfig();
			configManager = new DefaultConfigManager();
			LangManager.reload();
			// stop current lobbies and games
			for (Lobby lobby : lobbies.values()) {
				lobby.stop();
			}
			lobbies.clear();
			// load new lobbies and games
			ConfigurationSection lobbySection = configManager.getLobbies();
			if (lobbySection != null) {
				for (String id : lobbySection.getKeys(false)) {
					String name = "lobby";
					ConfigurationSection section = lobbySection.getConfigurationSection(id);
					String type = getType(section);
					LobbyFactory factory = getLobbyFactory(type);
					checkFactory(factory, name, type);
					lobbies.put(id, factory.get(section));
				}
			}
			// unregister the old automatic lobby joining
			if (autoJoin != null) {
				HandlerList.unregisterAll(autoJoin);
			}
			// register new automatic lobby joining in case it's enabled
			if (getConfig().getBoolean("autojoin.enabled", false)) {
				String name = getConfig().getString("autojoin.lobby", null);
				if (name != null) {
					Lobby lobby = lobbies.get(name);
					if (lobby != null) {
						autoJoin = new Listener() {
							@EventHandler
							public void onJoin(PlayerJoinEvent event) {
								BukkitRunnable joiner = new BukkitRunnable() {
									@Override
									public void run() {
										lobby.addPlayer(event.getPlayer());
									}
								};
								int delay = getConfig().getInt("autojoin.delay", 0);
								if (delay == 0) {
									joiner.run();
								} else {
									joiner.runTaskLater(FlierPlugin.this, delay);
								}
							}
						};
						// add all online players in case of a reload
						for (Player player : Bukkit.getOnlinePlayers()) {
							lobby.addPlayer(player);
						}
						// add all players joining in the future
						Bukkit.getPluginManager().registerEvents(autoJoin, this);
					} else {
						getLogger().warning(String.format("Automatic joining specifies non-existing '%s' lobby.", name));
					}
				} else {
					getLogger().warning("Automatic joining is enabled but the lobby is not specified.");
				}
			}
		} catch (LoadingException e) {
			getLogger().severe("There was an error during loading:");
			getLogger().severe(String.format("    - %s", e.getMessage()));
			Throwable cause = e.getCause();
			while (cause != null) {
				getLogger().severe(String.format("    - %s", cause.getMessage()));
				cause = cause.getCause();
			}
		}
		getLogger().info(String.format("Loaded %d lobbies.", lobbies.size()));
	}
	
	@Override
	public ConfigManager getConfigManager() {
		return configManager;
	}
	
	@Override
	public DatabaseManager getDatabaseManager() {
		return databaseManager;
	}
	
	@Override
	public FancyStuffWrapper getFancyStuff() {
		return fancyStuff;
	}
	
	@Override
	public Map<UUID, InGamePlayer> getPlayers() {
		return players;
	}

	@Override
	public Map<String, Lobby> getLobbies() {
		return Collections.unmodifiableMap(lobbies);
	}

	@Override
	public Engine getEngine(String id) throws LoadingException {
		String name = "engine";
		ConfigurationSection section = getSection(configManager.getEngines(), id, name);
		String type = getType(section);
		EngineFactory factory = getEngineFactory(type);
		checkFactory(factory, name, type);
		try {
			return factory.get(section);
		} catch (LoadingException e) {
			throw loadingError(e, id, name);
		}
	}

	@Override
	public UsableItem getItem(String id, InGamePlayer player) throws LoadingException {
		String name = "item";
		ConfigurationSection section = getSection(configManager.getItems(), id, name);
		try {
			return new DefaultUsableItem(section, player);
		} catch (LoadingException e) {
			throw loadingError(e, id, name);
		}
	}

	@Override
	public Wings getWing(String id) throws LoadingException {
		String name = "wings";
		ConfigurationSection section = getSection(configManager.getWings(), id, name);
		String type = getType(section);
		WingsFactory factory = getWingsFactory(type);
		checkFactory(factory, name, type);
		try {
			return factory.get(section);
		} catch (LoadingException e) {
			throw loadingError(e, id, name);
		}
	}
	
	@Override
	public Game getGame(String id, Lobby lobby) throws LoadingException, NoArenaException {
		String name = "game";
		ConfigurationSection section = getSection(configManager.getGames(), id, name);
		String type = getType(section);
		GameFactory factory = getGameFactory(type);
		checkFactory(factory, name, type);
		try {
			return factory.get(section, lobby);
		} catch (LoadingException e) {
			throw loadingError(e, id, name);
		}
	}
	
	@Override
	public Action getAction(String id, Optional<Owner> owner) throws LoadingException {
		String name = "action";
		ConfigurationSection section = getSection(configManager.getActions(), id, name);
		String type = getType(section);
		ActionFactory factory = getActionFactory(type);
		checkFactory(factory, name, type);
		try {
			return factory.get(section, owner);
		} catch (LoadingException e) {
			throw loadingError(e, id, name);
		}
	}
	
	@Override
	public Activator getActivator(String id, Optional<Owner> owner) throws LoadingException {
		String name = "activator";
		ConfigurationSection section = getSection(configManager.getActivators(), id, name);
		String type = getType(section);
		ActivatorFactory factory = getActivatorFactory(type);
		checkFactory(factory, name, type);
		try {
			return factory.get(section, owner);
		} catch (LoadingException e) {
			throw loadingError(e, id, name);
		}
	}
	
	@Override
	public Bonus getBonus(String id, Game game, Optional<Owner> owner) throws LoadingException {
		String name = "bonus";
		ConfigurationSection section = getSection(configManager.getBonuses(), id, name);
		String type = getType(section);
		BonusFactory factory = getBonusFactory(type);
		checkFactory(factory, name, type);
		try {
			return factory.get(section, game, owner);
		} catch (LoadingException e) {
			throw loadingError(e, id, name);
		}
	}

	@Override
	public Modification getModification(String id) throws LoadingException {
		String name = "modification";
		ConfigurationSection section = getSection(configManager.getModifications(), id, name);
		try {
			return new DefaultModification(section);
		} catch (LoadingException e) {
			throw loadingError(e, id, name);
		}
	}
	
	@Override
	public ItemSet getItemSet(String id, InGamePlayer owner) throws LoadingException {
		String name = "item set";
		ConfigurationSection section = getSection(configManager.getItemSets(), id, name);
		try {
			return new DefaultSet(section, owner);
		} catch (LoadingException e) {
			throw loadingError(e, id, name);
		}
	}
	
	@Override
	public Effect getEffect(String id) throws LoadingException {
		String name = "effect";
		ConfigurationSection section = getSection(configManager.getEffects(), id, name);
		String type = getType(section);
		EffectFactory factory = getEffectFactory(type);
		checkFactory(factory, name, type);
		try {
			return factory.get(section);
		} catch (LoadingException e) {
			throw loadingError(e, id, name);
		}
	}

	@Override
	public Arena getArena(String id) throws LoadingException {
		String name = "arena";
		ConfigurationSection section = getSection(configManager.getArenas(), id, name);
		try {
			return new DefaultArena(section);
		} catch (LoadingException e) {
			throw loadingError(e, id, name);
		}
	}
	
	private ConfigurationSection getSection(ConfigurationSection file, String id, String name) throws LoadingException {
		ConfigurationSection section = file.getConfigurationSection(id);
		if (section == null || section.getKeys(false).size() == 0) {
			throw new LoadingException(String.format("%s with ID '%s' does not exist.", Utils.capitalize(name), id));
		}
		return section;
	}
	
	private String getType(ConfigurationSection section) throws LoadingException {
		String type = section.getString("type");
		if (type == null) {
			throw new LoadingException("Type is not defined.");
		}
		return type;
	}
	
	private void checkFactory(Object object, String name, String type) throws LoadingException {
		if (object == null) {
			throw new LoadingException(String.format("%s type '%s' does not exist.", Utils.capitalize(name), type));
		}
	}
	
	private LoadingException loadingError(LoadingException e, String id, String name) {
		return (LoadingException) new LoadingException(String.format("Error in '%s' %s.", id, name)).initCause(e);
	}
	
	@Override
	public EngineFactory getEngineFactory(String name) {
		return engineTypes.get(name);
	}
	
	@Override
	public WingsFactory getWingsFactory(String name) {
		return wingTypes.get(name);
	}
	
	@Override
	public LobbyFactory getLobbyFactory(String name) {
		return lobbyTypes.get(name);
	}
	
	@Override
	public GameFactory getGameFactory(String name) {
		return gameTypes.get(name);
	}
	
	@Override
	public BonusFactory getBonusFactory(String name) {
		return bonusTypes.get(name);
	}
	
	@Override
	public ActionFactory getActionFactory(String name) {
		return actionTypes.get(name);
	}
	
	@Override
	public ActivatorFactory getActivatorFactory(String name) {
		return activatorTypes.get(name);
	}
	
	@Override
	public EffectFactory getEffectFactory(String name) {
		return effectTypes.get(name);
	}

	@Override
	public void registerEngine(String name, EngineFactory factory) {
		engineTypes.put(name, factory);
	}

	@Override
	public void registerWings(String name, WingsFactory factory) {
		wingTypes.put(name, factory);
	}

	@Override
	public void registerLobby(String name, LobbyFactory factory) {
		lobbyTypes.put(name, factory);
	}

	@Override
	public void registerGame(String name, GameFactory factory) {
		gameTypes.put(name, factory);
	}
	
	@Override
	public void registerBonus(String name, BonusFactory factory) {
		bonusTypes.put(name, factory);
	}
	
	@Override
	public void registerAction(String name, ActionFactory factory) {
		actionTypes.put(name, factory);
	}
	
	@Override
	public void registerActivator(String name, ActivatorFactory factory) {
		activatorTypes.put(name, factory);
	}
	
	@Override
	public void registerEffect(String name, EffectFactory factory) {
		effectTypes.put(name, factory);
	}

}
