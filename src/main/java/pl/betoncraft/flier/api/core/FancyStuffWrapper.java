/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
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
