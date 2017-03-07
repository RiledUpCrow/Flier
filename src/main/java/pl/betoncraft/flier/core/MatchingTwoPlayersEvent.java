/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core;

import java.util.Set;

import com.google.common.collect.Sets;

import pl.betoncraft.flier.api.core.InGamePlayer;

/**
 * Base class for MatchingPlayerEvents which also involve a second player. It
 * allows to cheaply switch the two players and reuse the event from another
 * perspective.
 *
 * @author Jakub Sapalski
 */
public class MatchingTwoPlayersEvent extends MatchingPlayerEvent {
	
	protected static final Set<String> playerMatchers = Sets.newHashSet(CLASS, COLOR, MONEY, ENGINE, FUEL, FUEL_RATIO,
			WINGS, WINGS_HEALTH, WINGS_HEALTH_RATIO); 

	protected InGamePlayer other;
	protected String otherPrefix;
	protected String playerPrefix;
	protected boolean switched = false;

	/**
	 * <p>
	 * Creates new MatchingPlayerEvent with an additional player involved. This
	 * event can be "switched" which means the main player becomes the other
	 * player and the other one takes his place. This way the
	 * MatchingTwoPlayersEvent can be reused for firing Effects for that other
	 * player.
	 * </p>
	 * 
	 * <p>
	 * The prefixes for other player and main player are also switched. The
	 * currently main player is always without any prefix, the currently other
	 * player takes the prefix of the place where he was originally.
	 * </p>
	 * 
	 * @param player
	 *            main player in the event
	 * @param other
	 *            the other player involved in this event
	 * @param otherPrefix
	 *            the prefix for the other player (initially used)
	 * @param playerPrefix
	 *            the prefix for the main player (used after switching)
	 */
	public MatchingTwoPlayersEvent(InGamePlayer player, InGamePlayer other, String otherPrefix, String playerPrefix) {
		super(player);
		this.other = other;
		this.otherPrefix = otherPrefix;
		this.playerPrefix = playerPrefix;
		parsePlayer(other, otherPrefix);
	}

	/**
	 * Switches the two players, so the event can be reused from a different
	 * perspective. This action has zero cost.
	 */
	public void setSwitched(boolean switched) {
		this.switched = switched;
	}

	/**
	 * @return whenever this event's players are switched or not
	 */
	public boolean isSwitched() {
		return switched;
	}

	@Override
	public InGamePlayer getPlayer() {
		if (switched) {
			return other;
		}
		return player;
	}

	/**
	 * @return the other player involved in this event; use
	 *         {@link #isSwitched()} to ensure that you're getting what you
	 *         want.
	 */
	public InGamePlayer getOther() {
		if (switched) {
			return player;
		}
		return other;
	}

	@Override
	public Double getNumber(String name) {
		if (switched && playerMatchers.contains(name)) {
			if (name.startsWith(playerPrefix)) {
				return super.getNumber(name.substring(playerPrefix.length()));
			}
			return super.getNumber(otherPrefix + name);
		}
		return super.getNumber(name);
	}

	@Override
	public String getString(String name) {
		if (switched && playerMatchers.contains(name)) {
			if (name.startsWith(playerPrefix)) {
				return super.getString(name.substring(playerPrefix.length()));
			}
			return super.getString(otherPrefix + name);
		}
		return super.getString(name);
	}

	@Override
	public Boolean getBool(String name) {
		if (switched && playerMatchers.contains(name)) {
			if (name.startsWith(playerPrefix)) {
				return super.getBool(name.substring(playerPrefix.length()));
			}
			return super.getBool(otherPrefix + name);
		}
		return super.getBool(name);
	}

}
