/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.betoncraft.flier.api.core.Damager;
import pl.betoncraft.flier.api.core.Usage;

/**
 * A dummy Damager.
 *
 * @author Jakub Sapalski
 */
public class DummyDamager implements Damager {
	
	public static final DummyDamager DUMMY = new DummyDamager();
	private static final List<Usage> emptyUsages = Collections.unmodifiableList(new ArrayList<>(0));
	
	private DummyDamager() {}
	
	@Override
	public int getNoDamageTicks() {
		return 0;
	}

	@Override
	public List<Usage> getSubUsages() {
		return emptyUsages;
	}

	@Override
	public boolean causesFriendlyFire() {
		return true;
	}

	@Override
	public boolean isSuicidal() {
		return true;
	}
	
	@Override
	public boolean isFinalHit() {
		return false;
	}

}
