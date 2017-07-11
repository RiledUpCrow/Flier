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
package pl.betoncraft.flier.effect;

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
				} else {
					throw new LoadingException(String.format("Matcher '%s' has incorrect type.", key));
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
