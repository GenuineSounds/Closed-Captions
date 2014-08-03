package com.genuineminecraft.closedcaptions.captions;

public class Caption2D extends Caption {

	public final boolean directMessage;

	public Caption2D(String message) {
		super(message);
		directMessage = true;
	}

	public Caption2D(String name, float volume, float pitch) {
		super(name, volume, pitch);
		directMessage = false;
	}
}
