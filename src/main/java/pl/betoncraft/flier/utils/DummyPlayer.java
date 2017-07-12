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
package pl.betoncraft.flier.utils;

import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.api.core.Attacker;
import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.Kit;
import pl.betoncraft.flier.api.core.SidebarLine;
import pl.betoncraft.flier.api.core.UsableItem;

/**
 * Dummy player for testing games.
 *
 * @author Jakub Sapalski
 */
public class DummyPlayer implements InGamePlayer {

	@Override
	public Attacker getAttacker() {
		return null;
	}

	@Override
	public void setAttacker(Attacker attacker) {
	}

	@Override
	public boolean handleHit(Attacker attacker) {
		return false;
	}

	@Override
	public Location getLocation() {
		return null;
	}

	@Override
	public Vector getVelocity() {
		return null;
	}

	@Override
	public Game getGame() {
		return null;
	}

	@Override
	public void setNoDamageTicks(int noDamageTicks) {
	}

	@Override
	public int getNoDamageTicks() {
		return 0;
	}

	@Override
	public boolean isTargetable() {
		return false;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public void addTrigger(String name) {
	}

	@Override
	public List<String> getTriggers() {
		return null;
	}

	@Override
	public UsableItem getHeldItem() {
		return null;
	}

	@Override
	public boolean isHolding(UsableItem item) {
		return false;
	}

	@Override
	public boolean isAccelerating() {
		return false;
	}

	@Override
	public void takeWingsOff() {
	}

	@Override
	public void consumeItem(UsableItem item) {
	}

	@Override
	public Player getPlayer() {
		return null;
	}

	@Override
	public double getWeight() {
		return 0;
	}

	@Override
	public String getLanguage() {
		return null;
	}

	@Override
	public boolean isPlaying() {
		return false;
	}

	@Override
	public void setPlaying(boolean isPlaying) {
	}

	@Override
	public Kit getKit() {
		return null;
	}

	@Override
	public void updateKit() {
	}

	@Override
	public int getMoney() {
		return 0;
	}

	@Override
	public void setMoney(int amount) {
	}

	@Override
	public ChatColor getColor() {
		return null;
	}

	@Override
	public void setColor(ChatColor color) {
	}

	@Override
	public void updateColors(Map<String, ChatColor> map) {
	}

	@Override
	public List<SidebarLine> getLines() {
		return null;
	}

	@Override
	public void exitGame() {
	}

}
