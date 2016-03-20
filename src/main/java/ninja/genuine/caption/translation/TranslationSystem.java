package ninja.genuine.caption.translation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.mojang.realmsclient.gui.ChatFormatting;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import net.minecraft.client.resources.I18n;
import ninja.genuine.caption.ClosedCaption;
import ninja.genuine.caption.caption.Caption;

public class TranslationSystem {

	private static final ChatFormatting[] FORMATS = new ChatFormatting[] { ChatFormatting.BOLD, ChatFormatting.ITALIC, ChatFormatting.STRIKETHROUGH, ChatFormatting.OBFUSCATED, ChatFormatting.UNDERLINE };

	public static String formatTranslation(final String translation) {
		final StringBuilder out = new StringBuilder(translation.length());
		final int[] activeFormats = new int[TranslationSystem.FORMATS.length];
		for (int index = 0; index < translation.length(); index++) {
			final char ch = translation.charAt(index);
			boolean reformat = false;
			switch (ch) {
				case '(':
					activeFormats[0]++;
					out.append(TranslationSystem.FORMATS[0].toString());
					break;
				case '[':
					activeFormats[1]++;
					out.append(TranslationSystem.FORMATS[1].toString());
					break;
				case '{':
					activeFormats[2]++;
					out.append(TranslationSystem.FORMATS[2].toString());
					break;
				case '<':
					activeFormats[3]++;
					out.append(TranslationSystem.FORMATS[3].toString());
					break;
				case ')':
					if (activeFormats[0] > 0)
						activeFormats[0]--;
					reformat = true;
					break;
				case ']':
					if (activeFormats[1] > 0)
						activeFormats[1]--;
					reformat = true;
					break;
				case '}':
					if (activeFormats[2] > 0)
						activeFormats[2]--;
					reformat = true;
					break;
				case '>':
					if (activeFormats[3] > 0)
						activeFormats[3]--;
					reformat = true;
					break;
				case '~':
					out.append('\u266A');
					break;
				default:
					out.append(ch);
					break;
			}
			if (reformat) {
				out.append(ChatFormatting.RESET.toString());
				out.append(TranslationSystem.reformat(activeFormats));
			}
		}
		return out.toString();
	}

	private static String reformat(final int[] activeFormats) {
		final StringBuilder out = new StringBuilder();
		for (int i = 0; i < activeFormats.length; i++)
			if (activeFormats[i] > 0)
				out.append(TranslationSystem.FORMATS[i].toString());
		return out.toString();
	}

	public static final TranslationSystem instance = new TranslationSystem();
	private final List<Translation> translations = new ArrayList<Translation>();

	private TranslationSystem() {}

	public synchronized boolean contains(final Caption caption) {
		return caption != null && contains(caption.key);
	}

	public synchronized boolean contains(final String key) {
		for (final Translation translation : translations)
			if (translation.equals(key))
				return true;
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

	public List<Translation> getList() {
		return translations;
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

	public synchronized void handleIMCMessages() {
		final List<IMCMessage> imcMessages = FMLInterModComms.fetchRuntimeMessages(ClosedCaption.MODID);
		for (final IMCMessage message : imcMessages) {
			final String messageKey = message.key.toLowerCase();
			boolean register = false;
			if (messageKey.equalsIgnoreCase("register"))
				register = true;
			try {
				String captionKey = message.getStringValue().split("=")[0].trim();
				String captionMessage = message.getStringValue().split("=")[1].trim();
				Translation translation = null;
				if (register)
					for (final Translation newTranslation : this.translations) {
						if (newTranslation.equals(captionKey))
							translation = newTranslation;
					}
				else
					translation = new Translation(captionKey);
				translation.add(captionMessage);
				this.translations.add(translation);
			} catch (Exception e) {}
		}
	}
}
