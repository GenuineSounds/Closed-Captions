package com.genuineflix.caption.caption;

import com.genuineflix.caption.translation.Translation;
import com.genuineflix.caption.translation.TranslationSystem;
import com.genuineflix.util.Titulary;

public abstract class Caption implements Titulary<Caption> {

	public static final int DEFAULT_TIMER = 60;
	public static final String DIRECT_MESSAGE_KEY = "[Direct]";
	public final String key;
	public final float volume;
	public final float pitch;
	public final boolean directMessage;
	private boolean disabled = false;
	private String message;
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
		if (!translation.isEmpty())
			message = TranslationSystem.formatTranslation(translation.getNext());
		else
			disabled = true;
	}

	@Override
	public int compareToKey(final String str) {
		return key.compareTo(str);
	}

	@Override
	public String getKey() {
		return key;
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

	@Override
	public Caption getValue() {
		return this;
	}

	public boolean isDisabled() {
		return disabled;
	}

	@Override
	public boolean nameEquals(final String str) {
		return key.equalsIgnoreCase(str);
	}

	public void resetTime() {
		lifespan = currentTick = previousTick = Caption.DEFAULT_TIMER;
	}

	@Override
	public Caption setValue(final Caption value) {
		final Caption old = this;
		disabled = value.disabled;
		message = value.message;
		lifespan = value.lifespan;
		currentTick = value.currentTick;
		previousTick = value.previousTick;
		return old;
	}

	public boolean tick() {
		return (previousTick = currentTick--) > 0;
	}
}
