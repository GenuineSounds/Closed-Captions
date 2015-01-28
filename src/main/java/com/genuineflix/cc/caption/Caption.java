package com.genuineflix.cc.caption;

import com.genuineflix.cc.system.TranslationSystem;
import com.genuineflix.cc.translation.Translation;

public class Caption implements Comparable<Caption> {

	public static final int DEFAULT_TIMER = 60;
	public static final String DIRECT_MESSAGE_KEY = "[Direct]";
	public final String key;
	public final float volume;
	public final float pitch;
	public final boolean directMessage;
	public boolean disabled = false;
	public String message;
	protected int lifespan;
	protected int currentTick;
	protected int previousTick;

	public Caption(final String message) {
		key = Caption.DIRECT_MESSAGE_KEY;
		directMessage = true;
		this.message = message;
		volume = 1;
		pitch = 1;
		lifespan = currentTick = previousTick = Caption.DEFAULT_TIMER;
	}

	public Caption(final String key, final float volume, final float pitch) {
		this(key, volume, pitch, Caption.DEFAULT_TIMER);
	}

	public Caption(final String key, final float volume, final float pitch, final int ticksToRun) {
		this.key = key;
		this.volume = volume;
		this.pitch = pitch;
		lifespan = currentTick = previousTick = ticksToRun;
		directMessage = false;
		assignTranslation();
	}

	private void assignTranslation() {
		final Translation translation = TranslationSystem.instance.get(this);
		if (translation.hasTranslations())
			message = TranslationSystem.formatTranslation(translation.getNext());
		else
			disabled = true;
	}

	@Override
	public int compareTo(final Caption o) {
		return key.compareTo(o.key);
	}

	public String getMessage() {
		return message;
	}

	public float getPercent() {
		return (float) currentTick / (float) lifespan;
	}

	public float getPercentGuess(final float partialTick) {
		return getPreviousPercent() + (getPercent() - getPreviousPercent()) * partialTick;
	}

	public float getPreviousPercent() {
		return (float) previousTick / (float) lifespan;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public boolean nameEquals(final String key) {
		return this.key.equalsIgnoreCase(key);
	}

	public void resetTime() {
		lifespan = currentTick = previousTick = Caption.DEFAULT_TIMER;
	}

	public boolean tick() {
		return (previousTick = currentTick--) > 0;
	}
}
