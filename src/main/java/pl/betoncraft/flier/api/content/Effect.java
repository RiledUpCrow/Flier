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
package pl.betoncraft.flier.api.content;

import java.util.List;
import java.util.Optional;

import pl.betoncraft.flier.api.core.InGamePlayer;
import pl.betoncraft.flier.api.core.Matcher;
import pl.betoncraft.flier.api.core.Named;
import pl.betoncraft.flier.util.EffectListener.EventType;

/**
 * Effect represents some non-game-changing action which is used when a
 * specified Bukkit event fires.
 *
 * @author Jakub Sapalski
 */
public interface Effect extends Named {

	/**
	 * @return the type of MatchingEvent this Effect applies to
	 */
	public EventType getType();

	/**
	 * Fire an effect with optional player.
	 * 
	 * @param player
	 *            the optional player for whom the Effect will fire; some
	 *            Effects can't run without any player
	 */
	public void fire(Optional<InGamePlayer> player);

	/**
	 * @return the list of Matchers to match against a MatchingEvent
	 */
	public List<Matcher> getMatchers();

}
