/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api;

import java.util.List;

/**
 * A category of ItemSets.
 *
 * @author Jakub Sapalski
 */
public interface Category {
	
	/**
	 * @return the name of this Category
	 */
	public String getName();
	
	/**
	 * @return the list of ItemSets currently in this Category.
	 */
	public List<ItemSet> getItemSets();
	
	/**
	 * @return the maximum amount of ItemSets in this Category.
	 */
	public int getMaxAmount();

}
