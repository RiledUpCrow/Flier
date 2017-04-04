/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import pl.betoncraft.flier.api.Flier;
import pl.betoncraft.flier.api.content.Effect;
import pl.betoncraft.flier.api.content.Game;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.Matcher;
import pl.betoncraft.flier.core.MatchingEvent;
import pl.betoncraft.flier.core.MatchingPlayerEvent;
import pl.betoncraft.flier.event.FlierClickButtonEvent;
import pl.betoncraft.flier.event.FlierCollectBonusEvent;
import pl.betoncraft.flier.event.FlierEngineUseEvent;
import pl.betoncraft.flier.event.FlierPlayerHitEvent;
import pl.betoncraft.flier.event.FlierPlayerKillEvent;
import pl.betoncraft.flier.event.FlierPlayerSpawnEvent;
import pl.betoncraft.flier.event.FlierUseEvent;

/**
 * Listens for all MatchingEvents and runs matching Effects.
 *
 * @author Jakub Sapalski
 */
public class EffectListener implements Listener {

	/**
	 * Type of the MatchingEvent.
	 */
	public enum EventType {
		USE(true),
		HIT(true),
		GET_HIT(true),
		KILL(true),
		KILLED(true),
		ENGINE(true),
		SPAWN(true),
		BONUS(true),
		BUTTON(true);

		private boolean player;

		private EventType(boolean player) {
			this.player = player;
		}

		/**
		 * @return whenever this event type involves a player.
		 */
		public boolean isPlayerInvolved() {
			return player;
		}
	}

	private final Game game;
	private final Map<EventType, List<Effect>> effects = new HashMap<>();

	/**
	 * Creates new event listener which fires Effects when a matching event is
	 * called.
	 * 
	 * @param effectNames
	 * @param game
	 *            the Game for which this EffectListener runs
	 * @throws LoadingException
	 *             when there is an error in one of the Effects
	 */
	public EffectListener(List<String> effectNames, Game game) throws LoadingException {
		// TODO create effects getter in game and remove first parameter
		this.game = game;
		Flier flier = Flier.getInstance();
		for (String effectName : effectNames) {
			try {
				Effect effect = flier.getEffect(effectName);
				effects.computeIfAbsent(effect.getType(), k -> new ArrayList<>()).add(effect);
			} catch (LoadingException e) {
				throw (LoadingException) new LoadingException(String.format("Error in '%s' effect.", effectName))
						.initCause(e);
			}
		}
		Bukkit.getPluginManager().registerEvents(this, Flier.getInstance());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onUse(FlierUseEvent event) {
		fireEffects(EventType.USE, event);
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onHit(FlierPlayerHitEvent event) {
		boolean org = event.isSwitched();
		event.setSwitched(false);
		fireEffects(EventType.GET_HIT, event);
		event.setSwitched(true);
		fireEffects(EventType.HIT, event);
		event.setSwitched(org);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onKill(FlierPlayerKillEvent event) {
		boolean org = event.isSwitched();
		event.setSwitched(false);
		fireEffects(EventType.KILLED, event);
		event.setSwitched(true);
		fireEffects(EventType.KILL, event);
		event.setSwitched(org);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onEngine(FlierEngineUseEvent event) {
		fireEffects(EventType.ENGINE, event);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onSpawn(FlierPlayerSpawnEvent event) {
		fireEffects(EventType.SPAWN, event);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onBonus(FlierCollectBonusEvent event) {
		fireEffects(EventType.BONUS, event);
	}
	
	@EventHandler(priority=EventPriority.MONITOR)
	public void onButton(FlierClickButtonEvent event) {
		fireEffects(EventType.BUTTON, event);
	}

	/**
	 * Fires Effects for specified Event type, matching the supplied event.
	 * 
	 * @param type
	 *            type of the MatchingEvent
	 * @param event
	 *            MatchingEvent to match against
	 */
	private void fireEffects(EventType type, MatchingEvent event) {
		if ((event instanceof Cancellable && ((Cancellable) event).isCancelled()) || !event.getGame().equals(game)) {
			return;
		}
		List<Effect> effects = this.effects.computeIfAbsent(type, k -> new ArrayList<>(0));
		for (Effect effect : effects) {
			if (checkEffect(effect, event)) {
				if (effect.getType().isPlayerInvolved() && event instanceof MatchingPlayerEvent) {
					effect.fire(((MatchingPlayerEvent) event).getPlayer());
				} else {
					effect.fire(null);
				}
			}
		}
	}

	/**
	 * Checks whenever the Effect matches the given MatchingEvent.
	 * 
	 * @param effect
	 *            the Effect to check
	 * @param event
	 *            the MatchingEvent to match
	 * @return whenever the Effect matches this MatchingEvent
	 */
	private boolean checkEffect(Effect effect, MatchingEvent event) {
		for (Matcher matcher : effect.getMatchers()) {
			String name = matcher.getName();
			switch (matcher.getType()) {
			case STRING:
				String string = event.getString(name);
				if (string == null || !matcher.getStrings().contains(string)) {
					return false;
				}
				break;
			case NUMBER_EXACT:
				Double number1 = event.getNumber(name);
				if (number1 == null || matcher.exactNumber() != number1) {
					return false;
				}
				break;
			case NUMBER_SECTION:
				Double number2 = event.getNumber(name); // somebody should fix this bug with duplicated variables  in switch...
														// there's a damn break, it can work!
				if (number2 == null || !(number2 > matcher.minNumber() && number2 < matcher.maxNumber())) {
					return false;
				}
				break;
			case BOOLEAN:
				Boolean bool = event.getBool(name);
				if (bool == null || bool != matcher.bool()) {
					return false;
				}
				break;
			}
		}
		return true;
	}

}
