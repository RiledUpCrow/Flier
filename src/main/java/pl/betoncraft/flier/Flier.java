/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

public class Flier extends JavaPlugin implements Listener, CommandExecutor {
	
	private static double MAX_SPEED = 1.5;
	private static double MIN_SPEED = 0.5;
	private static double ACCELERATION = 1.2;
	private static int MAX_FUEL = 100;
	private static int CONSUMPTION = 2;
	private static int REGENERATION = 1;
	
	private static int BURST_AMOUNT = 10;
	private static int BURST_TICKS = 1;
	private static double FIREBALL_SPEED = 5;
	private static int COOLDOWN = 40;
	
	private ItemStack gun = new ItemStack(Material.BLAZE_ROD);
	private ItemStack engine = new ItemStack(Material.FEATHER);
	private ItemStack elytra = new ItemStack(Material.ELYTRA);
	
	private Scoreboard score;
	private Objective fuel;
	
	private final Map<UUID, Long> glowTimer = new HashMap<>();
	private final Map<UUID, Long> weaponCooldown = new HashMap<>();
	private final List<UUID> fireBlocker = new LinkedList<>();
	
	@Override
	public void onEnable() {
		saveDefaultConfig();
		MAX_SPEED = getConfig().getDouble("max_speed", MAX_SPEED);
		MIN_SPEED = getConfig().getDouble("min_speed", MIN_SPEED);
		ACCELERATION = getConfig().getDouble("acceleration", ACCELERATION);
		MAX_FUEL = getConfig().getInt("max_fuel", MAX_FUEL);
		BURST_AMOUNT = getConfig().getInt("burst_amount", BURST_AMOUNT);
		BURST_TICKS = getConfig().getInt("burst_ticks", BURST_TICKS);
		FIREBALL_SPEED = getConfig().getDouble("fireball_speed", FIREBALL_SPEED);
		COOLDOWN = getConfig().getInt("cooldown", COOLDOWN);
		CONSUMPTION = getConfig().getInt("consumption", CONSUMPTION);
		REGENERATION = getConfig().getInt("regeneration", REGENERATION);
		ItemMeta gunMeta = gun.getItemMeta();
		gunMeta.setDisplayName(ChatColor.GOLD + "Gun");
		gunMeta.setLore(Arrays.asList(new String[]{
				ChatColor.RED + "Shoots fireballs when flying.",
				ChatColor.RED + "Hold it in the main hand and left-click."
		}));
		gun.setItemMeta(gunMeta);
		ItemMeta engineMeta = engine.getItemMeta();
		engineMeta.setDisplayName(ChatColor.AQUA + "Engine");
		engineMeta.setLore(Arrays.asList(new String[]{
				ChatColor.GREEN + "Speeds you up when flying.",
				ChatColor.GREEN + "Hold it in the off-hand and right-click."
		}));
		engine.setItemMeta(engineMeta);
		ItemMeta elytraMeta = elytra.getItemMeta();
		elytraMeta.spigot().setUnbreakable(true);
		elytraMeta.setDisplayName(ChatColor.LIGHT_PURPLE + "Wings");
		elytraMeta.setLore(Arrays.asList(new String[]{
				ChatColor.WHITE + "Lets you fly.",
				ChatColor.WHITE + "Wear it on your back and press space while falling to activate."
		}));
		elytra.setItemMeta(elytraMeta);
		new BukkitRunnable() {
			@Override
			public void run() {
				long time = new Date().getTime();
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (player.isGliding() && player.isSneaking()) {
						// speed up
						speedUp(player);
					} else {
						// regenerate fuel
						Score fuel = Flier.this.fuel.getScore(player.getName());
						int amount = fuel.getScore();
						if (amount < MAX_FUEL) {
							fuel.setScore(amount + REGENERATION);
						}
					}
					// stop glowing
					Long glow = glowTimer.get(player.getUniqueId());
					if (glow != null && time >= glow) {
						glowTimer.remove(player.getUniqueId());
						player.setGlowing(false);
					}
					Long cooldown = weaponCooldown.get(player.getUniqueId());
					if (cooldown != null && time >= cooldown) {
						weaponCooldown.remove(player.getUniqueId());
					}
				}
			}
		}.runTaskTimer(this, 5, 5);
		score = Bukkit.getScoreboardManager().getNewScoreboard();
		fuel = score.registerNewObjective("Fuel", "dummy");
		fuel.setDisplaySlot(DisplaySlot.SIDEBAR);
		fuel.setDisplayName("Fuel");
		Bukkit.getPluginManager().registerEvents(this, this);
		getCommand("flier").setExecutor(this);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equals("flier")) {
			if (sender instanceof Player) {
				Player player = (Player) sender;
				player.getInventory().addItem(gun.clone(), engine.clone(), elytra.clone());
				player.sendMessage(ChatColor.DARK_GREEN + "Gun and engine!");
			} else {
				sender.sendMessage(ChatColor.DARK_RED + "Must be a player!");
			}
			return true;
		}
		return false;
	}
	
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (player.isGliding()) {
			switch (event.getAction()) {
			case LEFT_CLICK_AIR:
			case LEFT_CLICK_BLOCK:
				if (event.getItem().isSimilar(gun)) {
					event.setCancelled(true);
					fireSeries(player);
				}
				break;
			default:
				break;
			}
		}
	}
	
	@EventHandler
	public void onGliding(EntityToggleGlideEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			Score fuel = this.fuel.getScore(player.getName());
			if (!fuel.isScoreSet()) {
				fuel.setScore(MAX_FUEL);
			}
			player.setScoreboard(score);
		}
	}
	
	private void speedUp(Player player) {
		if (!player.getInventory().getItemInOffHand().isSimilar(engine)) {
			return;
		}
		Score fuel = this.fuel.getScore(player.getName());
		int amount = fuel.getScore();
		if (amount <= 0) {
			return;
		}
		fuel.setScore(amount - CONSUMPTION);
		Vector velocity = player.getVelocity();
		Vector direction = player.getLocation().getDirection();
		double speed = velocity.length();
		Vector speedUp = velocity.getMidpoint(direction.normalize().multiply(speed));
		double newSpeed = speedUp.length();
		speedUp.normalize();
		if (newSpeed < MIN_SPEED) {
			newSpeed = MIN_SPEED;
		} else if (newSpeed < MAX_SPEED) {
			newSpeed *= ACCELERATION;
			if (newSpeed > MAX_SPEED) {
				newSpeed = MAX_SPEED;
			}
		}
		speedUp.multiply(newSpeed);
		player.setVelocity(speedUp);
		player.setGlowing(true);
		glowTimer.put(player.getUniqueId(), new Date().getTime() + 1000*5);
	}
	
	private void fireSeries(Player player) {
		UUID id = player.getUniqueId();
		if (weaponCooldown.containsKey(id) || fireBlocker.contains(id)) {
			return;
		}
		fireBlocker.add(id);
		weaponCooldown.put(id, new Date().getTime() + 50*COOLDOWN);
		new BukkitRunnable() {
			int counter = BURST_AMOUNT;
			@Override
			public void run() {
				Vector direction = player.getLocation().getDirection();
				direction.multiply(FIREBALL_SPEED);
				Location launch = player.getLocation().clone().add(direction);
				Fireball fireball = (Fireball) launch.getWorld().spawnEntity(launch, EntityType.FIREBALL);
				fireball.setVelocity(direction);
				fireball.setShooter(player);
				counter --;
				if (counter <= 0) {
					cancel();
					fireBlocker.remove(player.getUniqueId());
				}
			}
		}.runTaskTimer(this, 0, BURST_TICKS);
	}
	
	@EventHandler
	public void onHit(EntityDamageByEntityEvent event) {
		if (event.getEntity() instanceof Player) {
			Player player = (Player) event.getEntity();
			if (event.getDamager() instanceof Fireball) {
				Fireball fireball = (Fireball) event.getDamager();
				if (player.isGliding() && !fireball.getShooter().equals(player)) {
					ItemStack elytra = player.getInventory().getChestplate();
					if (elytra != null) {
						player.getInventory().setChestplate(null);
						Map<Integer, ItemStack> left = player.getInventory().addItem(elytra);
						if (!left.isEmpty()) {
							for (ItemStack item : left.values()) {
								if (item != null) {
									player.getWorld().dropItem(player.getLocation(), item);
								}
							}
						}
					}
				} else {
					if (fireball.getShooter() instanceof Player) {
						Player shooter = (Player) fireball.getShooter();
						if (!shooter.equals(player) && shooter.isGliding() && event.getEntity().isOnGround()) {
							event.setCancelled(true);
							player.damage(player.getHealth(), shooter);
						}
					}
				}
			}
		}
	}

}
