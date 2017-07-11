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
package pl.betoncraft.flier.api.core;

import org.bukkit.entity.Player;

/**
 * Wraps functionality of several plugins managing titles, action bar and tab
 * list.
 *
 * @author Jakub Sapalski
 */
public interface FancyStuffWrapper {

	/**
	 * Sends the title to the player using one of the hooked plugins. It can
	 * also send titles without any other plugins but the command message will
	 * be logged to the console. If all numeric arguments are 0 the title will
	 * be displayed with default times.
	 * 
	 * @param player
	 *            the player who should receive the title
	 * @param title
	 *            title string, must be supplied
	 * @param sub
	 *            subtitle string, can be null
	 * @param fadeIn
	 *            fading in time (in ticks)
	 * @param stay
	 *            staying time (in ticks)
	 * @param fadeOut
	 *            fading out time (in ticks)
	 */
	void sendTitle(Player player, String title, String sub, int fadeIn, int stay, int fadeOut);

	/**
	 * Sends the action bar message for specified duration.
	 * 
	 * @param player
	 *            receiver of the message
	 * @param message
	 *            message to display
	 */
	void sendActionBar(Player player, String message);

	/**
	 * Sets the tab list header and footer.
	 * 
	 * @param player
	 *            the player whose tab list will be updated
	 * @param header
	 *            header string
	 * @param footer
	 *            footer string
	 */
	void setTabList(Player player, String header, String footer);

	/**
	 * @return whenever a title handling plugin is hooked
	 */
	boolean hasTitleHandler();

	/**
	 * @return whenever an action bar handling plugin is hooked
	 */
	boolean hasActionBarHandler();

	/**
	 * @return whenever a tab list handling plugin is hooked
	 */
	boolean hasTabListHandler();

}
