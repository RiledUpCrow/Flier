/** This program is free software. It comes without any warranty, to
 * the extent permitted by applicable law. You can redistribute it
 * and/or modify it under the terms of the Do What The Fuck You Want
 * To Public License, Version 2, as published by Sam Hocevar. See
 * http://www.wtfpl.net/ for more details.
 */
package pl.betoncraft.flier.core.defaults;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.content.Effect;
import pl.betoncraft.flier.api.core.LoadingException;
import pl.betoncraft.flier.api.core.Matcher;
import pl.betoncraft.flier.core.DefaultMatcher;
import pl.betoncraft.flier.util.ValueLoader;
import pl.betoncraft.flier.util.EffectListener.EventType;

/**
 * Default implementation of the Effect.
 *
 * @author Jakub Sapalski
 */
public abstract class DefaultEffect implements Effect {
	
	private static final String TYPE = "event_type";
	private static final String MATCHERS = "matchers";
	
	protected final String id;
	protected final ValueLoader loader;
	
	protected final EventType type;
	protected final List<Matcher> matchers;
	
	public DefaultEffect(ConfigurationSection section) throws LoadingException {
		id = section.getName();
		loader = new ValueLoader(section);
		type = loader.loadEnum(TYPE, EventType.class);
		ConfigurationSection values = section.getConfigurationSection(MATCHERS);
		if (values != null) {
			matchers = new ArrayList<>(values.getKeys(false).size());
			for (String key : values.getKeys(false)) {
				if (values.isList(key)) {
					matchers.add(new DefaultMatcher(key, values.getStringList(key)));
				} else if (values.isBoolean(key)) {
					matchers.add(new DefaultMatcher(key, values.getBoolean(key)));
				} else if (values.isDouble(key) || values.isInt(key)) {
					matchers.add(new DefaultMatcher(key, values.getDouble(key)));
				} else if (values.isString(key)) {
					String value = values.getString(key);
					List<String> parts = value.contains(",") ?
							Arrays.asList(value.split(",")).stream().map(s -> s.trim()).collect(Collectors.toList()) :
							Arrays.asList(new String[]{value});
					boolean number = false;
					double min = -Double.MAX_VALUE;
					double max = Double.MAX_VALUE;
					for (String part : parts) {
						if (part.startsWith("<(") && part.endsWith(")")) {
							try {
								max = Double.parseDouble(part.substring(2, part.length() - 1));
								number = true;
							} catch (NumberFormatException e) {
								// not a number, huh
							}
						} else if (part.startsWith(">(") && part.endsWith(")")) {
							try {
								min = Double.parseDouble(part.substring(2, part.length() - 1));
								number = true;
							} catch (NumberFormatException e) {
								// not a number, huh
							}
						}
					}
					if (number) {
						matchers.add(new DefaultMatcher(key, min, max));
					} else {
						matchers.add(new DefaultMatcher(key, Arrays.asList(new String[]{value})));
					}
				}
			}
		} else {
			matchers = new ArrayList<>(0);
		}
	}

	@Override
	public String getID() {
		return id;
	}

	@Override
	public EventType getType() {
		return type;
	}

	@Override
	public List<Matcher> getMatchers() {
		return matchers;
	}
	
	protected void playerOnly() throws LoadingException {
		if (!type.isPlayerInvolved()) {
			throw new LoadingException("This effect requires the player, but the chosen event type does not involve any particular player.");
		}
	}

}
