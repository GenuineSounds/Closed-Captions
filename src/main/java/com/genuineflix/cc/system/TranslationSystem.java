package com.genuineflix.cc.system;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.resources.I18n;

import com.genuineflix.cc.caption.Caption;
import com.genuineflix.cc.translation.Translation;
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
		out = out.replace("~", "\u266a");
		return out;
	}

	public static final TranslationSystem instance = new TranslationSystem();
	private final List<String> keys = new ArrayList<String>();
	private List<Translation> translations = new ArrayList<Translation>();

	private TranslationSystem() {}

	public boolean contains(final Caption caption) {
		return keys.contains(caption);
	}

	public Translation get(final Caption caption) {
		for (final Translation translation : translations)
			if (translation.sound.equalsIgnoreCase(caption.key))
				return translation;
		initTranslation(caption);
		return Translation.NONE;
	}

	public List<Translation> getTranslations() {
		return translations;
	}

	public boolean hasTranslation(final Caption caption) {
		if (caption.directMessage)
			return true;
		if (!contains(caption))
			return false;
		return get(caption).hasTranslations();
	}

	private void initTranslation(final Caption caption) {
		if (!contains(caption)) {
			final Translation translation = new Translation(caption.key);
			final String formatted = I18n.format(caption.key);
			if (formatted.equals(caption.key) || formatted.isEmpty())
				return;
			translation.add(formatted);
			translations.add(translation);
			keys.add(caption.key);
		}
	}

	public void setTranslations(final List<Translation> translations) {
		this.translations = translations;
		for (final Translation translation : translations)
			keys.add(translation.sound);
	}
}
