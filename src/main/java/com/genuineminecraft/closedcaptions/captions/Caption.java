package com.genuineminecraft.closedcaptions.captions;

public class Caption implements Comparable<Caption> {

	public static final int DEFAULT_TIMER = 60;
	public final String name;
	public final float volume;
	public final float pitch;
	protected int lifespan;
	protected int currentTick;
	protected int previousTick;

	public Caption(String message) {
		this.name = message;
		this.volume = 1;
		this.pitch = 1;
		this.lifespan = this.currentTick = this.previousTick = Caption.DEFAULT_TIMER;
	}

	public Caption(String name, float volume, float pitch) {
		this.name = name;
		this.volume = volume;
		this.pitch = pitch;
		this.lifespan = this.currentTick = this.previousTick = Caption.DEFAULT_TIMER;
	}

	public Caption(String name, float volume, float pitch, int ticksToRun) {
		this(name, volume, pitch);
		this.lifespan = ticksToRun;
		this.currentTick = ticksToRun;
		this.previousTick = ticksToRun;
	}

	public String getName() {
		return this.name;
	}

	public float getPercent() {
		return (float) this.currentTick / (float) this.lifespan;
	}

	public float getPitch() {
		return this.pitch;
	}

	public float getPreviousPercent() {
		return (float) this.previousTick / (float) this.lifespan;
	}

	public float getVolume() {
		return this.volume;
	}

	public float getPercentGuess(float partialTick) {
		return this.getPreviousPercent() + ((this.getPercent() - this.getPreviousPercent()) * partialTick);
	}

	public boolean nameEquals(String name) {
		return this.name.equalsIgnoreCase(name);
	}

	public void resetTime() {
		this.lifespan = this.currentTick = this.previousTick = Caption.DEFAULT_TIMER;
	}

	public boolean tick() {
		return (this.previousTick = this.currentTick--) > 0;
	}

	@Override
	public int compareTo(Caption o) {
		return name.compareTo(o.name);
	}
}
