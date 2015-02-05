package com.genuineflix.caption.translation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import net.minecraft.client.resources.I18n;

import com.genuineflix.caption.caption.Caption;
import com.mojang.realmsclient.gui.ChatFormatting;

public class TranslationSystem {

	public static String formatTranslation(final String translation) {
		String out = translation;
		out = out.replace("(", ChatFormatting.BOLD.toString());
		out = out.replace("[", ChatFormatting.ITALIC.toString());
		out = out.replace("{", ChatFormatting.STRIKETHROUGH.toString());
		out = out.replace("<", ChatFormatting.OBFUSCATED.toString());
		out = out.replace(")", ChatFormatting.RESET.toString());
		out = out.replace("]", ChatFormatting.RESET.toString());
		out = out.replace("}", ChatFormatting.RESET.toString());
		out = out.replace(">", ChatFormatting.RESET.toString());
		out = out.replace('~', '\u266a');
		out = out.replace('|', '\n');
		return out;
	}

	public static final TranslationSystem instance = new TranslationSystem();
	private final List<Translation> translations = new ArrayList<Translation>();

	private TranslationSystem() {}

	public synchronized boolean contains(final Caption caption) {
		for (Translation translation : translations) {
			if (translation.equals(caption.key))
				return true;
		}
		return false;
	}

	public synchronized Translation get(final Caption caption) {
		if (caption.directMessage)
			return Translation.DIRECT;
		if (!contains(caption))
			init(caption);
		for (final Translation translation : translations)
			if (translation.equals(caption.key))
				return translation;
		return Translation.NONE;
	}

	public synchronized Map<String, List<String>> getMap() {
		final Map<String, List<String>> map = new TreeMap<String, List<String>>();
		for (final Translation translation : translations) {
			final List<String> list = translation.getValue();
			Collections.sort(list);
			map.put(translation.getKey(), list);
		}
		return map;
	}

	public synchronized boolean hasTranslation(final Caption caption) {
		if (caption.directMessage)
			return true;
		if (!contains(caption))
			return false;
		return get(caption).isEmpty();
	}

	private void init(final Caption caption) {
		final Translation tran = new Translation(caption);
		translations.add(tran);
		final String formatted = I18n.format(caption.key);
		if (formatted.equals(caption.key) || formatted.isEmpty())
			return;
		tran.add(formatted);
	}

	public synchronized void setMap(final Map<String, List<String>> map) {
		for (final Entry<String, List<String>> entry : map.entrySet()) {
			final Translation tran = new Translation(entry.getKey(), entry.getValue());
			translations.remove(tran);
			translations.add(tran);
		}
	}
}
