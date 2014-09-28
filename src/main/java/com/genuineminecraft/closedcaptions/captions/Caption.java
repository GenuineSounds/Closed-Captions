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
		this.key = Caption.DIRECT_MESSAGE;
		this.directMessage = true;
		this.message = message;
		this.volume = 1;
		this.pitch = 1;
		this.lifespan = this.currentTick = this.previousTick = Caption.DEFAULT_TIMER;
	}

	public Caption(String key, float volume, float pitch) {
		this.key = key;
		this.directMessage = false;
		this.volume = volume;
		this.pitch = pitch;
		this.lifespan = this.currentTick = this.previousTick = Caption.DEFAULT_TIMER;
	}

	public Caption(String key, float volume, float pitch, int ticksToRun) {
		this(key, volume, pitch);
		this.lifespan = ticksToRun;
		this.currentTick = ticksToRun;
		this.previousTick = ticksToRun;
	}

	@Override
	public int compareTo(Caption o) {
		return this.key.compareTo(o.key);
	}

	public String getMessage() {
		return this.message;
	}

	public float getPercent() {
		return (float) this.currentTick / (float) this.lifespan;
	}

	public float getPercentGuess(float partialTick) {
		return this.getPreviousPercent() + ((this.getPercent() - this.getPreviousPercent()) * partialTick);
	}

	public float getPreviousPercent() {
		return (float) this.previousTick / (float) this.lifespan;
	}

	public boolean isDisabled() {
		return this.disabled;
	}

	public boolean nameEquals(String key) {
		return this.key.equalsIgnoreCase(key);
	}

	public void resetTime() {
		this.lifespan = this.currentTick = this.previousTick = Caption.DEFAULT_TIMER;
	}

	public boolean tick() {
		return (this.previousTick = this.currentTick--) > 0;
	}
}
