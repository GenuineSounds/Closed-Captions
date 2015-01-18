package com.genuineminecraft.closedcaptions.captions;

public class Caption implements Comparable<Caption> {

	public static final int DEFAULT_TIMER = 60;
	public static final String DIRECT_MESSAGE = "direct-message";
	public final String key;
	public final float volume;
	public final float pitch;
	public final boolean directMessage;
	public boolean disabled = false;
	public String message;
	protected int lifespan;
	protected int currentTick;
	protected int previousTick;

	public Caption(String message) {
		key = Caption.DIRECT_MESSAGE;
		directMessage = true;
		this.message = message;
		volume = 1;
		pitch = 1;
		lifespan = currentTick = previousTick = Caption.DEFAULT_TIMER;
	}

	public Caption(String key, float volume, float pitch) {
		this.key = key;
		directMessage = false;
		this.volume = volume;
		this.pitch = pitch;
		lifespan = currentTick = previousTick = Caption.DEFAULT_TIMER;
	}

	public Caption(String key, float volume, float pitch, int ticksToRun) {
		this(key, volume, pitch);
		lifespan = ticksToRun;
		currentTick = ticksToRun;
		previousTick = ticksToRun;
	}

	@Override
	public int compareTo(Caption o) {
		return key.compareTo(o.key);
	}

	public String getMessage() {
		return message;
	}

	public float getPercent() {
		return (float) currentTick / (float) lifespan;
	}

	public float getPercentGuess(float partialTick) {
		return getPreviousPercent() + (getPercent() - getPreviousPercent()) * partialTick;
	}

	public float getPreviousPercent() {
		return (float) previousTick / (float) lifespan;
	}

	public boolean isDisabled() {
		return disabled;
	}

	public boolean nameEquals(String key) {
		return this.key.equalsIgnoreCase(key);
	}

	public void resetTime() {
		lifespan = currentTick = previousTick = Caption.DEFAULT_TIMER;
	}

	public boolean tick() {
		return (previousTick = currentTick--) > 0;
	}
}
