package com.genuineflix.caption.caption;

public class CaptionHUD extends Caption {

	public float amount;

	public CaptionHUD(final String message, final float amount) {
		super(message);
		this.amount = amount;
	}

	public CaptionHUD(final String name, final float volume, final float pitch) {
		super(name, volume, pitch);
	}

	@Override
	public int compareTo(final Caption t) {
		if (t instanceof CaptionHUD)
			return Float.compare(amount, ((CaptionHUD) t).amount);
		return 0;
	}
}
