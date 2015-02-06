package com.genuineflix.caption.config;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.world.WorldEvent;

import com.genuineflix.caption.translation.TranslationSystem;
import com.google.common.base.Charsets;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class Config {

	private static final GsonBuilder GSON_BUILDER = new GsonBuilder();
	private static final Type GSON_TYPE = new TypeToken<Map<String, List<String>>>() {}.getType();
	public File folder;
	public File save;
	public Configuration main;

	public void pre(final File folder) {
		this.folder = folder;
		save = new File(folder, "translations.json");
		main = new Configuration(new File(folder, "Main.cfg"));
		loadEnableSave(getClass().getClassLoader().getResourceAsStream("assets/closedcaption/defaults.json"), save);
	}

	public void init() {
		loadEnableSave(save, save);
	}

	@SubscribeEvent
	public void saveLoad(final WorldEvent.Save event) {
		loadEnableSave(save, save);
	}

	private void loadEnableSave(final InputStream input, final File output) {
		loadTranslations(input);
		saveTranslations(output);
	}

	private void loadEnableSave(final File input, final File output) {
		loadTranslations(input);
		saveTranslations(output);
	}

	public void saveTranslations(final File file) {
		final Gson gson = GSON_BUILDER.create();
		Writer wr = null;
		try {
			if (!file.exists())
				file.createNewFile();
			wr = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8);
			gson.toJson(TranslationSystem.instance.getMap(), GSON_TYPE, wr);
		}
		catch (final Exception e) {}
		finally {
			try {
				if (wr != null)
					wr.close();
			}
			catch (final Exception e) {}
		}
	}

	public void loadTranslations(final File file) {
		if (!file.exists())
			return;
		final Gson gson = GSON_BUILDER.create();
		FileReader fr = null;
		try {
			fr = new FileReader(file);
			final Map<String, List<String>> ts = gson.fromJson(fr, GSON_TYPE);
			if (ts != null)
				TranslationSystem.instance.setMap(ts);
		}
		catch (final Exception e) {}
		finally {
			try {
				if (fr != null)
					fr.close();
			}
			catch (final Exception e) {}
		}
	}

	public void loadTranslations(final InputStream is) {
		final Gson gson = GSON_BUILDER.create();
		InputStreamReader fr = null;
		try {
			fr = new InputStreamReader(is);
			final Map<String, List<String>> ts = gson.fromJson(fr, GSON_TYPE);
			if (ts != null)
				TranslationSystem.instance.setMap(ts);
		}
		catch (final Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (fr != null)
					fr.close();
			}
			catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	static {
		GSON_BUILDER.setPrettyPrinting();
		GSON_BUILDER.disableHtmlEscaping();
		GSON_BUILDER.enableComplexMapKeySerialization();
	}
}
