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
package pl.betoncraft.flier.game;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Bonus;
import pl.betoncraft.flier.api.content.Button;
import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.api.content.Lobby;
import pl.betoncraft.flier.api.content.Wings;
import pl.betoncraft.flier.api.core.Arena;
import pl.betoncraft.flier.api.core.Attacker;
import pl.betoncraft.flier.api.core.FancyStuffWrapper;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.Kit;
import pl.betoncraft.flier.api.core.Kit.AddResult;
import pl.betoncraft.flier.api.core.Kit.RespawnAction;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.NoArenaException;
import pl.betoncraft.flier.api.core.SetApplier;
import pl.betoncraft.flier.api.core.Target;
import pl.betoncraft.flier.core.DefaultKit;
import pl.betoncraft.flier.core.DefaultPlayer;
import pl.betoncraft.flier.event.FlierClickButtonEvent;
import pl.betoncraft.flier.event.FlierGameCreateEvent;
import pl.betoncraft.flier.event.FlierGameEndEvent;
import pl.betoncraft.flier.event.FlierGameEndEvent.GameEndCause;
import pl.betoncraft.flier.event.FlierGameStartEvent;
import pl.betoncraft.flier.event.FlierPlayerKillEvent;
import pl.betoncraft.flier.event.FlierPlayerKillEvent.KillType;
import pl.betoncraft.flier.game.WaitingRoom.WaitReason;
import pl.betoncraft.flier.sidebar.Altitude;
import pl.betoncraft.flier.sidebar.Ammo;
import pl.betoncraft.flier.sidebar.Fuel;
import pl.betoncraft.flier.sidebar.Health;
import pl.betoncraft.flier.sidebar.Money;
import pl.betoncraft.flier.sidebar.Speed;
import pl.betoncraft.flier.sidebar.Time;
import pl.betoncraft.flier.util.DoubleClickBlocker;
import pl.betoncraft.flier.util.DummyPlayer;
import pl.betoncraft.flier.util.EffectListener;
import pl.betoncraft.flier.util.LangManager;
import pl.betoncraft.flier.util.Utils;
import pl.betoncraft.flier.util.ValueLoader;

/**
 * Basic rules of a game.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultGame implements Listener, Game {
	
	private static final String MONEY_SUICIDE = "money.suicide";
	private static final String MONEY_BY_FRIENDLY_HIT = "money.by_friendly_hit";
	private static final String MONEY_BY_FRIENDLY_DEATH = "money.by_friendly_death";
	private static final String MONEY_BY_ENEMY_HIT = "money.by_enemy_hit";
	private static final String MONEY_BY_ENEMY_DEATH = "money.by_enemy_death";
	private static final String MONEY_FRIENDLY_HIT = "money.friendly_hit";
	private static final String MONEY_FRIENDLY_KILL = "money.friendly_kill";
	private static final String MONEY_ENEMY_HIT = "money.enemy_hit";
	private static final String MONEY_ENEMY_KILL = "money.enemy_kill";
	private static final String MONEY_ENABLED = "money.enabled";
	private static final String DEFAULT_KIT = "default_kit";
	private static final String BUTTONS = "buttons";
	private static final String BONUSES = "bonuses";
	private static final String RESPAWN_ACTION = "respawn_action";
	private static final String MAX_TIME = "max_time";
	private static final String MAX_PLAYERS = "max_players";
	private static final String ROUNDS = "rounds";
	private static final String EFFECTS = "effects";
	private static final String LEAVE_BLOCKS = "leave_blocks";
	private static final String RADIUS = "radius";
	private static final String CENTER = "center";
	private static final String VIABLE_ARENAS = "viable_arenas";
	private static final String NAME = "name";

	protected static final List<DamageCause> allowedDamage = new ArrayList<>(Arrays.asList(new DamageCause[]{
			DamageCause.CONTACT, DamageCause.CUSTOM, DamageCause.FALL, DamageCause.FLY_INTO_WALL,
			DamageCause.DROWNING, DamageCause.FALLING_BLOCK, DamageCause.FIRE,
			DamageCause.FIRE_TICK, DamageCause.LAVA, DamageCause.LIGHTNING, DamageCause.SUFFOCATION
	}));
	
	static {
		try {
			// this is a fix for 1.9 version, which lacks HOT_FLOOR enum
			allowedDamage.add(DamageCause.valueOf("HOT_FLOOR"));
		} catch (IllegalArgumentException e) {}
	}

	protected final String id;
	protected final String name;
	protected final int uniqueNumber = new Random().nextInt(Integer.MAX_VALUE);
	protected final ValueLoader loader;
	protected GameHeartBeat heartBeat;
	protected WaitingRoom waitingRoom;
	
	protected final Map<UUID, InGamePlayer> dataMap = new HashMap<>();
	protected final Map<UUID, Target> targets = new HashMap<>();
	protected final FancyStuffWrapper fancyStuff;
	protected final EffectListener listener;
	protected final List<Bonus> bonuses = new ArrayList<>();
	protected final Map<String, Button> buttons = new HashMap<>();
	protected final Map<InGamePlayer, List<Button>> unlocked = new HashMap<>();
	protected final RespawnAction respawnAction;
	protected final boolean rounds;
	protected final int maxPlayers;
	protected final int maxTime;
	protected final Kit defKit;
	protected final boolean useMoney;
	protected final int enemyKillMoney;
	protected final int enemyHitMoney;
	protected final int friendlyKillMoney;
	protected final int friendlyHitMoney;
	protected final int byEnemyDeathMoney;
	protected final int byEnemyHitMoney;
	protected final int byFriendlyDeathMoney;
	protected final int byFriendlyHitMoney;
	protected final int suicideMoney;

	protected Lobby lobby;
	protected Arena arena;
	protected boolean running = false;
	protected int timeLeft;
	protected List<Block> leaveBlocks = new ArrayList<>();
	protected Location center;
	protected int minX, minZ, maxX, maxZ;
	
	public DefaultGame(ConfigurationSection section, Lobby lobby) throws LoadingException, NoArenaException {
		
		Flier flier = Flier.getInstance();
		this.lobby = lobby;
		id = section.getName();
		loader = new ValueLoader(section);
		name = loader.loadString(NAME, id);
		
		// select an arena
		List<String> viableArenas = section.getStringList(VIABLE_ARENAS);
		if (viableArenas.isEmpty()) {
			throw new LoadingException("No viable arenas are specified.");
		}
		for (Entry<String, Arena> entry : lobby.getArenas().entrySet()) {
			if (viableArenas.contains(entry.getKey()) && !entry.getValue().isUsed()) {
				arena = entry.getValue();
				arena.setUsed(true);
				break;
			}
		}
		if (arena == null) {
			throw new NoArenaException();
		}
		
		// calculate borders
		center = arena.getLocationSet(loader.loadString(CENTER)).getSingle();
		int radius = loader.loadPositiveInt(RADIUS);
		minX = center.getBlockX() - radius;
		maxX = center.getBlockX() + radius;
		minZ = center.getBlockZ() - radius;
		maxZ = center.getBlockZ() + radius;
		
		// load "leave" blocks
		for (Location loc : arena.getLocationSet(loader.loadString(LEAVE_BLOCKS)).getMultiple()) {
			leaveBlocks.add(loc.getBlock());
		}
		
		// load other stuffs
		fancyStuff = flier.getFancyStuff();
		listener = new EffectListener(section.getStringList(EFFECTS), this);
		rounds = loader.loadBoolean(ROUNDS);
		maxPlayers = loader.loadNonNegativeInt(MAX_PLAYERS, 0);
		maxTime = loader.loadNonNegativeInt(MAX_TIME, 0) * 20;
		timeLeft = maxTime;
		respawnAction = loader.loadEnum(RESPAWN_ACTION, RespawnAction.class);
		waitingRoom = new WaitingRoom(this, loader);
		
		// bonuses
		for (String bonusName : section.getStringList(BONUSES)) {
			bonuses.add(flier.getBonus(bonusName, this, Optional.empty()));
		}
		
		// buttons
		ConfigurationSection buttonsSection = section.getConfigurationSection(BUTTONS);
		if (buttonsSection != null) for (String button : buttonsSection.getKeys(false)) {
			ConfigurationSection buttonSection = buttonsSection.getConfigurationSection(button);
			if (buttonSection == null) {
				throw new LoadingException(String.format("'%s' is not a button.", button));
			}
			try {
				buttons.put(button, new DefaultButton(this, buttonSection));
			} catch (LoadingException e) {
				throw (LoadingException) new LoadingException(
						String.format("Error in '%s' button.", button)).initCause(e);
			}
		}
		
		// default kit
		try {
			defKit = new DefaultKit(section.getStringList(DEFAULT_KIT), respawnAction, new DummyPlayer());
		} catch (LoadingException e) {
			throw (LoadingException) new LoadingException("Error in default kit.").initCause(e);
		}
		
		// money
		useMoney = loader.loadBoolean(MONEY_ENABLED, false);
		enemyKillMoney = loader.loadInt(MONEY_ENEMY_KILL, 0);
		enemyHitMoney = loader.loadInt(MONEY_ENEMY_HIT, 0);
		friendlyKillMoney = loader.loadInt(MONEY_FRIENDLY_KILL, 0);
		friendlyHitMoney = loader.loadInt(MONEY_FRIENDLY_HIT, 0);
		byEnemyDeathMoney = loader.loadInt(MONEY_BY_ENEMY_DEATH, 0);
		byEnemyHitMoney = loader.loadInt(MONEY_BY_ENEMY_HIT, 0);
		byFriendlyDeathMoney = loader.loadInt(MONEY_BY_FRIENDLY_DEATH, 0);
		byFriendlyHitMoney = loader.loadInt(MONEY_BY_FRIENDLY_HIT, 0);
		suicideMoney = loader.loadInt(MONEY_SUICIDE, 0);
		
		// registering an event listener
		Bukkit.getPluginManager().registerEvents(this, Flier.getInstance());
		
		// game created, firing an event
		if (lobby.isOpen()) {
			FlierGameCreateEvent event = new FlierGameCreateEvent(this);
			Bukkit.getPluginManager().callEvent(event);
		}
	}
	
	protected class GameHeartBeat extends BukkitRunnable {
		
		private int tickCounter = 0;
		
		public GameHeartBeat(DefaultGame game) {
			runTaskTimer(Flier.getInstance(), 1, 1);
		}

		@Override
		public void run() {
			if (maxTime != 0 && --timeLeft == 0) {
				endGame();
			}
			if (running) {
				for (InGamePlayer data : getPlayers().values()) {
					// invisibility fix
					if (tickCounter % 20 == 0) {
						getPlayers().values().stream().filter(other -> !other.equals(data)).forEach(other -> {
							data.getPlayer().showPlayer(other.getPlayer());
						});
					}
					Location loc = data.getPlayer().getLocation();
					// height damage
					if (loc.getBlockX() < minX || loc.getBlockX() > maxX ||
							loc.getBlockZ() < minZ || loc.getBlockZ() > maxZ) {
						data.getPlayer().damage(data.getPlayer().getHealth() + 1);
					}
				}
				tickCounter++;
				if (tickCounter > 1000) {
					tickCounter = 0;
				}
			}
		}
	}
	
	/**
	 * Ends the game by selecting the winner.
	 */
	public void endGame() {
		// move all players to the waiting room
		running = false;
		waitingRoom.reason = WaitReason.GAME_ENDS;
		dataMap.values().stream()
				.filter(player -> !waitingRoom.waitingPlayers.contains(player))
				.forEach(player -> moveToWaitingRoom(player));
		// display message
		dataMap.values().forEach(player -> LangManager.sendMessage(player, "game_ends"));
		// end game
		int delay = waitingRoom.respawnDelay == 0 ? 20 * 10 : waitingRoom.respawnDelay;
		Bukkit.getScheduler().scheduleSyncDelayedTask(Flier.getInstance(),
				() -> lobby.endGame(this, GameEndCause.FINISHED), delay);
	}
	
	@Override
	public String getID() {
		return id;
	}

	@Override
	public String getName(CommandSender player) {
		return name.startsWith("$") ? LangManager.getMessage(player, name.substring(1)) : name;
	}
	
	@Override
	public int getUniqueNumber() {
		return uniqueNumber;
	}

	@Override
	public InGamePlayer addPlayer(Player player) {
		// can't join if the waiting room is locked
		if (waitingRoom.isLocked()) {
			throw new IllegalStateException("The game is locked.");
		}
		// can't join if already joined
		UUID uuid = player.getUniqueId();
		if (dataMap.containsKey(uuid)) {
			throw new IllegalStateException("Player is already in game.");
		}
		InGamePlayer data =  new DefaultPlayer(player, this, defKit);
		dataMap.put(uuid, data);
		targets.put(uuid, data);
		Flier.getInstance().playerJoinsGame(data);
		// creating default stuff
		data.getLines().add(new Fuel(data));
		data.getLines().add(new Health(data));
		data.getLines().add(new Speed(data));
		data.getLines().add(new Altitude(data));
		// ammunition will be displayed on action bar if possible
		if (!fancyStuff.hasActionBarHandler()) {
			data.getLines().add(new Ammo(data));
		}
		if (useMoney) {
			data.getLines().add(new Money(data));
		}
		if (maxTime != 0) {
			data.getLines().add(new Time(data));
		}
		// move into waiting room
		moveToWaitingRoom(data);
		return data;
	}
	
	/**
	 * Displays a message about the cause of the waiting.
	 * 
	 * @param player
	 * @param reason
	 */
	public void waitMessage(Player player, WaitReason reason) {
		switch (reason) {
		case MORE_PLAYERS:
			for (InGamePlayer data : waitingRoom.waitingPlayers) {
				LangManager.sendMessage(data, "more_players", waitingRoom.minPlayers - dataMap.size());
			}
			break;
		case NO_WAIT:
			// nothing
			break;
		case RESPAWN_DELAY:
			LangManager.sendMessage(player, "respawn_delay", (double) waitingRoom.currentWaitingTime / 20.0);
			break;
		case START_DELAY:
			LangManager.sendMessage(player, "start_delay", (double) waitingRoom.currentWaitingTime / 20.0);
			break;
		case ROUND:
			LangManager.sendMessage(player, "round_delay");
			break;
		default:
			break;
		}
	}
	
	@Override
	public void removePlayer(Player player) {
		InGamePlayer data = dataMap.remove(player.getUniqueId());
		if (data == null) {
			return;
		}
		targets.remove(player.getUniqueId());
		unlocked.remove(data);
		waitingRoom.removePlayer(data);
		Flier.getInstance().playerLeavesGame(data);
		data.clearPlayer();
		data.getPlayer().teleport(lobby.getSpawn());
		LangManager.sendMessage(player, "game_left", getName(player));
	}
	
	@Override
	public Map<UUID, InGamePlayer> getPlayers() {
		return Collections.unmodifiableMap(dataMap);
	}
	
	@Override
	public Map<UUID, Target> getTargets() {
		return targets;
	}

	@Override
	public void start() {
		running = true;
		heartBeat = new GameHeartBeat(this);
		for (Bonus bonus : bonuses) {
			bonus.start();
		}
		if (waitingRoom.locking) {
			waitingRoom.locked = true;
		}
		// game started, fire an event
		FlierGameStartEvent event = new FlierGameStartEvent(this);
		Bukkit.getPluginManager().callEvent(event);
	}
	
	protected abstract Set<InGamePlayer> getPlayersForRespawn(Set<InGamePlayer> players);

	@Override
	public void stop(GameEndCause cause) {
		HandlerList.unregisterAll(this);
		arena.setUsed(false);
		for (Bonus bonus : bonuses) {
			bonus.stop();
		}
		if (heartBeat != null) {
			heartBeat.cancel();
		}
		if (waitingRoom.ticker != null) {
			waitingRoom.ticker.cancel();
		}
		Collection<InGamePlayer> copy = new ArrayList<>(dataMap.values());
		for (InGamePlayer data : copy) {
			removePlayer(data.getPlayer());
		}
		// game ended, fire an event
		if (lobby.isOpen()) {
			FlierGameEndEvent event = new FlierGameEndEvent(this, cause);
			Bukkit.getPluginManager().callEvent(event);
		}
		// after firing the event unregister all listeners
		listener.stop();
	}
	
	@Override
	public boolean isRunning() {
		return running;
	}
	
	@Override
	public boolean isLocked() {
		return waitingRoom.isLocked();
	}
	
	@Override
	public void handleKill(InGamePlayer killed, DamageCause cause) {
		Attacker attacker = killed.getAttacker();
		InGamePlayer killer = attacker == null ? null : attacker.getCreator();
		boolean fall = cause == DamageCause.FALL;
		if (killer != null && !killer.equals(killed)) {
			if (fall) {
				shotDownMessage("shot_down", killed, killer);
			} else {
				killedMessage("killed", killed, killer);
			}
			// fire an event
			FlierPlayerKillEvent deathEvent = new FlierPlayerKillEvent(killed, killer,
					fall ? KillType.SHOT_DOWN : KillType.KILLED);
			Bukkit.getPluginManager().callEvent(deathEvent);
			Attitude a = getAttitude(killer, killed);
			if (a == Attitude.FRIENDLY) {
				pay(killer, friendlyKillMoney);
				pay(killed, byFriendlyDeathMoney);
			} else if (a == Attitude.HOSTILE) {
				pay(killer, enemyKillMoney);
				pay(killed, byEnemyDeathMoney);
			}
		} else {
			suicideMessage("suicide", killed);
			// fire an event
			FlierPlayerKillEvent deathEvent = new FlierPlayerKillEvent(killed, killed,
					fall ? KillType.SHOT_DOWN : KillType.KILLED);
			Bukkit.getPluginManager().callEvent(deathEvent);
			pay(killed, suicideMoney);
		}
	}
	
	protected void moveToWaitingRoom(InGamePlayer player) {
		Bukkit.getScheduler().scheduleSyncDelayedTask(Flier.getInstance(), () -> {
			Utils.clearPlayer(player.getPlayer());
			player.setAttacker(null);
			player.setPlaying(false);
			Kit kit = player.getKit();
			kit.onRespawn();
			player.updateKit();
			WaitReason reason = waitingRoom.addPlayer(player);
			waitMessage(player.getPlayer(), reason);
		});
	}
	
	@Override
	public void handleHit(Target attacked, Attacker attacker) {
		InGamePlayer creator = attacker.getCreator();
		boolean hit = attacked.handleHit(attacker);
		// handle a general hit
		if (hit && attacker.getDamager().isFinalHit() && creator != null && attacked instanceof InGamePlayer) {
			// pay money for a hit
			InGamePlayer attackedPlayer = (InGamePlayer) attacked;
			Attitude a = getAttitude(creator, attacked);
			if (a == Attitude.FRIENDLY) {
				pay(creator, friendlyHitMoney);
				pay(attackedPlayer, byFriendlyHitMoney);
			} else if (a == Attitude.HOSTILE) {
				pay(creator, enemyHitMoney);
				pay(attackedPlayer, byEnemyHitMoney);
			}
		}
	}
	
	@Override
	public void handleRespawn(InGamePlayer player) {
		player.getPlayer().getInventory().setHeldItemSlot(0);
		new BukkitRunnable() {
			@Override
			public void run() {
				player.setPlaying(true);
			}
		}.runTaskLater(Flier.getInstance(), 20);
		LangManager.sendMessage(player, "no_waiting");
		// spawn event must be called after teleportation
	}
	
	@Override
	public Map<String, Button> getButtons() {
		return buttons;
	}

	@Override
	public boolean applyButton(InGamePlayer player, Button button, boolean buy, boolean notify) {
		boolean applied = false;
		if (button != null) {
			// check permissions
			if (!button.getPermissions().stream().allMatch(perm -> player.getPlayer().hasPermission(perm))) {
				LangManager.sendMessage(player, "no_permission");
				return applied;
			}
			List<Button> ul = unlocked.computeIfAbsent(player, k -> new LinkedList<>());
			boolean unlocked = button.getUnlockCost() == 0 || ul.contains(button);
			if (!unlocked) {
				if (!button.getRequirements().stream().map(name -> buttons.get(name)).allMatch(b -> ul.contains(b))) {
					if (notify) LangManager.sendMessage(player, "unlock_other");
				} else if (button.getUnlockCost() <= player.getMoney()) {
					SetApplier applier = button.getOnUnlock();
					Runnable run = () -> {
						ul.add(button);
						player.setMoney(player.getMoney() - button.getUnlockCost());
						player.updateKit();
					};
					String message;
					if (applier == null) {
						run.run();
						applied = true;
						message = "unlocked";
					} else {
						AddResult result = applier.isSaving() ? player.getKit().addStored(applier) :
							player.getKit().addCurrent(applier);
						switch (result) {
						case ADDED:
						case FILLED:
						case REPLACED:
						case REMOVED:
							run.run();
							applied = true;
							message = "unlocked";
							break;
						default:
							message = "cant_use";
						}
					}
					if (notify) LangManager.sendMessage(player, message);
				} else {
					if (notify) LangManager.sendMessage(player, "no_money_unlock");
				}
			} else {
				int cost;
				SetApplier applier;
				if (buy) {
					cost = button.getBuyCost();
					applier = button.getOnBuy();
				} else {
					cost = button.getSellCost();
					applier = button.getOnSell();
				}
				if (applier != null) {
					if (cost <= player.getMoney()) {
						AddResult result = applier.isSaving() ? player.getKit().addStored(applier) :
							player.getKit().addCurrent(applier);
						Runnable run = () -> {
							player.setMoney(player.getMoney() - cost);
							player.updateKit();
						};
						String message = null;
						switch (result) {
						case ADDED:
							run.run();
							applied = true;
							message = "items_added";
							break;
						case FILLED:
							run.run();
							applied = true;
							message = "items_refilled";
							break;
						case REMOVED:
							run.run();
							applied = true;
							message = "items_removed";
							break;
						case REPLACED:
							run.run();
							applied = true;
							message = "items_replaced";
							break;
						case ALREADY_EMPTIED:
							// no running, items were not added
							message = "cant_sell";
							break;
						case ALREADY_MAXED:
							// no running, items were not added
							message = "item_limit";
							break;
						case SKIPPED:
							// no running, items were not added
							message = "item_conflict";
							break;
						}
						if (notify) LangManager.sendMessage(player, message);
					} else {
						if (notify) LangManager.sendMessage(player, "no_money_buy");
					}
				} else {
					if (notify) LangManager.sendMessage(player, "cant_do");
				}
			}
		}
		return applied;
	}
	
	@Override
	public Lobby getLobby() {
		return lobby;
	}
	
	@Override
	public List<Bonus> getBonuses() {
		return bonuses;
	}
	
	@Override
	public Location getCenter() {
		return center;
	}
	
	@Override
	public Arena getArena() {
		return arena;
	}
	
	@Override
	public int getMaxPlayers() {
		return maxPlayers;
	}
	
	@Override
	public int getTimeLeft() {
		return timeLeft;
	}
	
	@Override
	public boolean hasRounds() {
		return rounds;
	}
	
	@EventHandler(priority=EventPriority.HIGH)
	public void onClick(PlayerInteractEvent event) {
		InGamePlayer data = getPlayers().get(event.getPlayer().getUniqueId());
		if (data != null) {
			event.setCancelled(true);
			// handle button clicking
			if (event.hasBlock()) {
				// this prevents double clicks on next tick
				if (DoubleClickBlocker.isBlocked(event.getPlayer())) {
					return;
				} else {
					DoubleClickBlocker.block(event.getPlayer());
				}
				// apply the button
				Button button = buttons.values().stream()
						.filter(b -> b.getLocations().stream()
								.map(loc -> loc.getBlock())
								.anyMatch(block -> event.getClickedBlock().equals(block))
						)
						.findFirst()
						.orElse(null);
				if (button != null) {
					FlierClickButtonEvent e = new FlierClickButtonEvent(data, button);
					Bukkit.getPluginManager().callEvent(e);
					if (!e.isCancelled()) {
						applyButton(data, button, event.getAction() == Action.LEFT_CLICK_BLOCK, true);
					}
					return;
				}
				// handle leaving block
				if (leaveBlocks.contains(event.getClickedBlock())) {
					lobby.leaveGame(event.getPlayer());
				}
			}
			// not a button
			ItemStack item = event.getPlayer().getInventory().getItemInMainHand();
			Wings wings = data.getKit().getWings();
			if (item != null && wings != null && item.isSimilar(wings.getItem(data))) {
				// handle wearing wings
				event.getPlayer().getInventory().setChestplate(item);
				event.getPlayer().getInventory().setItemInMainHand(null);
				event.getPlayer().getWorld().playSound(
						event.getPlayer().getLocation(), Sound.ITEM_ARMOR_EQUIP_GENERIC, 1, 1);
			} else {
				// handle a regular click
				switch (event.getAction()) {
				case LEFT_CLICK_AIR:
				case LEFT_CLICK_BLOCK:
					data.addTrigger("left_click");
					break;
				case RIGHT_CLICK_AIR:
				case RIGHT_CLICK_BLOCK:
					data.addTrigger("right_click");
					break;
				default:
					break;
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST)
	public void onDamage(EntityDamageEvent event) {
		if (event.isCancelled()) {
			return;
		}
		// prevent out-of-game damage to targets
		Target target = getTargets().get(event.getEntity().getUniqueId());
		if (target != null) {
			if (!allowedDamage.contains(event.getCause())) {
				event.setCancelled(true);
			}
			// handle death of players
			if (target instanceof InGamePlayer) {
				InGamePlayer data = (InGamePlayer) target;
				if (data.getPlayer().getHealth() - event.getFinalDamage() <= 0) {
					event.setCancelled(true);
					handleKill(data, event.getCause());
				}
			}
		}
		// prevent damage to entities caused by in-game targets
		if (event instanceof EntityDamageByEntityEvent) {
			EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) event;
			Target attacker = getTargets().get(entityEvent.getDamager().getUniqueId());
			if (attacker != null) {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onBlockExplode(EntityExplodeEvent event) {
		if (Attacker.getAttacker(event.getEntity()) != null) {
			event.blockList().clear();
		}
	}
	
	@EventHandler
	public void onInvInteract(InventoryClickEvent event) {
		if (getPlayers().containsKey(event.getWhoClicked().getUniqueId())) {
			event.setCancelled(true);
		}
	}
	
	private void shotDownMessage(String message, InGamePlayer killed, InGamePlayer killer) {
		for (InGamePlayer player : dataMap.values()) {
			LangManager.sendMessage(player, message,
					Utils.formatPlayer(killed, player),
					Utils.formatPlayer(killer, player));
		}
	}
	
	private void killedMessage(String message, InGamePlayer killed, InGamePlayer killer) {
		for (InGamePlayer player : dataMap.values()) {
			LangManager.sendMessage(player, message,
					Utils.formatPlayer(killed, player),
					Utils.formatPlayer(killer, player));
		}
	}
	
	private void suicideMessage(String message, InGamePlayer killed) {
		for (InGamePlayer player : dataMap.values()) {
			LangManager.sendMessage(player, message,
					Utils.formatPlayer(killed, player));
		}
	}
	
	private void pay(InGamePlayer player, int amount) {
		player.setMoney(player.getMoney() + amount);
	}

}
