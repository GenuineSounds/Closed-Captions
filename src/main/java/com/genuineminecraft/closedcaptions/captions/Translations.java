package com.genuineminecraft.closedcaptions.captions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.minecraft.client.resources.I18n;

import com.mojang.realmsclient.gui.ChatFormatting;

public class Translations {

	public static String formatTranslation(String translation) {
		String out = translation;
		out = out.replace("(", ChatFormatting.BOLD.toString());
		out = out.replace("[", ChatFormatting.ITALIC.toString());
		out = out.replace("{", ChatFormatting.STRIKETHROUGH.toString());
		out = out.replace("<", ChatFormatting.OBFUSCATED.toString());
		out = out.replace(")", ChatFormatting.RESET.toString());
		out = out.replace("]", ChatFormatting.RESET.toString());
		out = out.replace("}", ChatFormatting.RESET.toString());
		out = out.replace(">", ChatFormatting.RESET.toString());
		out = out.replace("~", "\u266a");
		return out;
	}

	public Map<String, ArrayList<String>> translations;

	public Translations() {
		translations = Collections.synchronizedMap(new HashMap<String, ArrayList<String>>());
	}

	public void assignTranslation(Caption caption) {
		initTranslation(caption.key);
		List<String> trans = translations.get(caption.key);
		if (!(caption.disabled = trans.isEmpty()))
			caption.message = Translations.formatTranslation(trans.get(new Random().nextInt(trans.size())));
	}

	public boolean hasTranslation(Caption caption) {
		initTranslation(caption.key);
		if (caption.directMessage)
			return true;
		return !translations.get(caption.key).isEmpty();
	}

	private void initTranslation(String key) {
		synchronized (translations) {
			if (!translations.containsKey(key)) {
				translations.put(key, new ArrayList<String>());
				String first = I18n.format(key);
				if (!first.equals(key) && !first.isEmpty())
					translations.get(key).add(first);
			}
		}
	}
}
