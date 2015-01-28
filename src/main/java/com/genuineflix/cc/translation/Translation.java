package com.genuineflix.cc.translation;

import java.util.ArrayList;
import java.util.List;

public class Translation {

	public static final Translation NONE = new Translation("NONE");
	private static int index = 0;
	public final String sound;
	public String message;
	private final List<String> list = new ArrayList<String>();

	public Translation(final String sound) {
		this.sound = sound;
	}

	public void add(final String trans) {
		list.add(trans);
	}

	public void clear() {
		list.clear();
	}

	public String get() {
		return get(Translation.index % list.size());
	}

	public String get(final int index) {
		return list.get(0);
	}

	public String getNext() {
		return get(++Translation.index % list.size());
	}

	public String getPrevious() {
		return get(--Translation.index % list.size());
	}

	@Override
	public int hashCode() {
		return sound.hashCode();
	}

	public boolean hasTranslations() {
		return !(this == Translation.NONE || list.isEmpty());
	}
}