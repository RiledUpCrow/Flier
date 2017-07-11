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

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;

import pl.betoncraft.flier.api.core.LoadingException;

/**
 * Plays a sound.
 *
 * @author Jakub Sapalski
 */
public abstract class SoundEffect extends DefaultEffect {

	private static final String SOUND = "sound";
	private static final String VOLUME = "volume";
	private static final String PITCH = "pitch";
	
	protected Sound sound;
	protected float volume;
	protected float pitch;

	public SoundEffect(ConfigurationSection section) throws LoadingException {
		super(section);
		playerOnly();
		sound = loader.loadEnum(SOUND, Sound.class);
		volume = (float) loader.loadPositiveDouble(VOLUME, 1.0);
		pitch = (float) loader.loadPositiveDouble(PITCH, 1.0);
	}

}
