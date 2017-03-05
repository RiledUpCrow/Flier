/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.api;

/**
 * Holds information about how and when the ItemSet should be applied.
 *
 * @author Jakub Sapalski
 */
public interface SetApplier {

	/**
	 * How to add items from this ItemSet.
	 */
	public enum AddType {
		INCREASE, DECREASE, FILL
	}

	/**
	 * What to do when the conflict between item types in the same category
	 * arises.
	 */
	public enum ConflicAction {
		REPLACE, SKIP
	}

	/**
	 * @return the ItemSet which is applied by this applier
	 */
	public ItemSet getItemSet();

	/**
	 * Category is used to group the ItemSets together and handle adding new
	 * ItemSets.
	 *
	 * @return the category name of this ItemSet
	 */
	public String getCategory();
	
	/**
	 * @return the ID of the ItemSet applied by this SetApplier
	 */
	public String getID();

	/**
	 * Checks whenever this ItemSet is being saved to the "stored" items.
	 */
	public boolean isSaving();

	/**
	 * @return how many times this ItemSet should be applied
	 */
	public int getAmount();

	/**
	 * @return the type of adding operation
	 */
	public AddType getAddType();

	/**
	 * @return the action when there's a conflict
	 */
	public ConflicAction getConflictAction();

	/**
	 * Checks if the two appliers are similar - have the same ItemSet and
	 * amount. Other properties can be different.
	 * 
	 * @param applier
	 *            the applier to check
	 * @return whenever these two are the same
	 */
	public boolean isSimilar(SetApplier applier);

}
