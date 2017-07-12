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
	 * What to do when the conflict between ItemSets in the same category
	 * arises.
	 */
	public enum ConflicAction {
		REPLACE, SKIP
	}

	/**
	 * @param owner
	 *            the Owner of the returned ItemSet
	 * @return the ItemSet which is applied by this applier
	 */
	public ItemSet getItemSet(InGamePlayer owner);

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
