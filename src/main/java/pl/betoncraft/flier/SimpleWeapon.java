/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import pl.betoncraft.flier.api.Damager;
import pl.betoncraft.flier.api.Weapon;

/**
 * Implementation of a simple, burst shooting weapon.
 *
 * @author Jakub Sapalski
 */
public class SimpleWeapon extends DefaultItem implements Weapon {
	
	private EntityType entity = EntityType.FIREBALL;
	private int slot = -1;
	private int burstAmount = 10;
	private int burstTicks = 1;
	private double projectileSpeed = 5;
	private int cooldown = 40;
	private boolean consumable = false;
	private boolean onlyAir = false;
	private double damage = 50;
	private boolean wingsOff = true;
	private boolean killsOnGround = true;
	private double physicalDamage = 5;

	private final Map<UUID, Long> weaponCooldown = new HashMap<>();
	private final List<UUID> fireBlocker = new LinkedList<>();
	
	public SimpleWeapon(ConfigurationSection section) {
		super(section);
		entity = EntityType.valueOf(section.getString("entity", "FIREBALL").toUpperCase().replace(' ', '_'));
		slot = section.getInt("slot", slot);
		burstAmount = section.getInt("burst_amount", burstAmount);
		burstTicks = section.getInt("burst_ticks", burstTicks);
		projectileSpeed = section.getDouble("projectile_speed", projectileSpeed);
		cooldown = section.getInt("cooldown", cooldown);
		consumable = section.getBoolean("consumable", consumable);
		onlyAir = section.getBoolean("only_air", onlyAir);
		damage = section.getDouble("damage", damage);
		wingsOff = section.getBoolean("wings_off", wingsOff);
		killsOnGround = section.getBoolean("kills_on_ground", killsOnGround);
		physicalDamage = section.getDouble("physical_damage", physicalDamage);
		weight = section.getDouble("weight", weight);
	}
	
	@Override
	public double getDamage() {
		return damage;
	}
	
	@Override
	public boolean wingsOff() {
		return wingsOff;
	}
	
	@Override
	public boolean killsOnGround() {
		return killsOnGround;
	}

	@Override
	public double getPhysical() {
		return physicalDamage;
	}
	
	@Override
	public boolean isConsumable() {
		return consumable;
	}
	
	@Override
	public boolean onlyAir() {
		return onlyAir;
	}
	
	@Override
	public int slot() {
		return slot;
	}
	
	@Override
	public void use(PlayerData data) {
		if (onlyAir() && !data.isFlying()) {
			return;
		}
		Player player = data.getPlayer();
		UUID id = player.getUniqueId();
		if (weaponCooldown.containsKey(id) || fireBlocker.contains(id)) {
			return;
		}
		fireBlocker.add(id);
		weaponCooldown.put(id, new Date().getTime() + 50*cooldown);
		new BukkitRunnable() {
			int counter = burstAmount;
			@Override
			public void run() {
				Vector velocity = player.getLocation().getDirection().clone().multiply(projectileSpeed);
				Vector pointer = player.getLocation().getDirection().clone().multiply(player.getVelocity().length() * 3);
				Location launch = (player.isGliding() ? player.getLocation() : player.getEyeLocation())
						.clone().add(pointer);
				Projectile projectile = (Projectile) launch.getWorld().spawnEntity(launch, entity);
				projectile.setVelocity(velocity);
				projectile.setShooter(player);
				Damager.saveDamager(projectile, SimpleWeapon.this);
				counter --;
				if (counter <= 0) {
					cancel();
					fireBlocker.remove(player.getUniqueId());
				}
			}
		}.runTaskTimer(Flier.getInstance(), 0, burstTicks);
	}
	
	@Override
	public void cooldown(PlayerData data) {
		Player player = data.getPlayer();
		Long cooldown = weaponCooldown.get(player.getUniqueId());
		if (cooldown != null && System.currentTimeMillis() >= cooldown) {
			weaponCooldown.remove(player.getUniqueId());
		}
	}

}
