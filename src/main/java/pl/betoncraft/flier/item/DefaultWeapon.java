/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.item;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import pl.betoncraft.flier.api.Weapon;
import pl.betoncraft.flier.core.PlayerData;

/**
 * A default Weapon implementation.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultWeapon extends DefaultItem implements Weapon {

	protected int cooldown = 40;
	protected int slot = -1;
	protected boolean consumable = false;
	protected boolean onlyAir = false;
	protected boolean suicidal = false;
	protected boolean friendlyFire = true;

	protected double damage = 50;
	protected boolean wingsOff = true;
	protected boolean killsOnGround = true;
	protected double physicalDamage = 5;

	protected final Map<UUID, Long> weaponCooldown = new HashMap<>();
	
	public DefaultWeapon(ConfigurationSection section) {
		super(section);
		slot = section.getInt("slot", slot);
		cooldown = section.getInt("cooldown", cooldown);
		consumable = section.getBoolean("consumable", consumable);
		onlyAir = section.getBoolean("only_air", onlyAir);
		suicidal = section.getBoolean("suicidal", suicidal);
		friendlyFire = section.getBoolean("friendly_fire", friendlyFire);
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
	public boolean friendlyFire() {
		return friendlyFire;
	}

	@Override
	public boolean suicidal() {
		return suicidal;
	}
	
	@Override
	public int slot() {
		return slot;
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
