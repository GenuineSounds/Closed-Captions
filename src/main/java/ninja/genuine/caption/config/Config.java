package ninja.genuine.caption.config;

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

import com.google.common.base.Charsets;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.event.world.WorldEvent;
import ninja.genuine.caption.translation.TranslationSystem;

public class Config {

	@SuppressWarnings("serial")
	private static final Type GSON_TYPE = new TypeToken<Map<String, List<String>>>() {}.getType();
	private static final GsonBuilder GSON_BUILDER = new GsonBuilder();
	public File save;

	public void pre(final File folder) {
		folder.mkdirs();
		save = new File(folder, "translations.json");
		if (!save.exists())
			loadSave(getClass().getClassLoader().getResourceAsStream("assets/closedcaption/defaults.json"), save);
	}

	public void init() {
		loadSave(save);
	}

	@SubscribeEvent
	public void saveLoad(final WorldEvent.Save event) {
		loadSave(save);
	}

	private void loadSave(final InputStream input, final File output) {
		Config.loadTranslations(input);
		Config.saveTranslations(output);
	}

	private void loadSave(final File save) {
		Config.loadTranslations(save);
		Config.saveTranslations(save);
	}

	public static void saveTranslations(final File file) {
		final Gson gson = Config.GSON_BUILDER.create();
		Writer wr = null;
		try {
			if (!file.exists())
				file.createNewFile();
			wr = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8);
			gson.toJson(TranslationSystem.instance.getMap(), Config.GSON_TYPE, wr);
		} catch (final Exception e) {} finally {
			try {
				if (wr != null)
					wr.close();
			} catch (final Exception e) {}
		}
	}

	public static void loadTranslations(final File file) {
		if (!file.exists())
			return;
		final Gson gson = Config.GSON_BUILDER.create();
		FileReader fr = null;
		try {
			fr = new FileReader(file);
			final Map<String, List<String>> ts = gson.fromJson(fr, Config.GSON_TYPE);
			if (ts != null)
				TranslationSystem.instance.setMap(ts);
		} catch (final Exception e) {} finally {
			try {
				if (fr != null)
					fr.close();
			} catch (final Exception e) {}
		}
	}

	public static void loadTranslations(final InputStream is) {
		final Gson gson = Config.GSON_BUILDER.create();
		InputStreamReader fr = null;
		try {
			fr = new InputStreamReader(is);
			final Map<String, List<String>> ts = gson.fromJson(fr, Config.GSON_TYPE);
			if (ts != null)
				TranslationSystem.instance.setMap(ts);
		} catch (final Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (fr != null)
					fr.close();
			} catch (final Exception e) {
				e.printStackTrace();
			}
		}
	}

	static {
		Config.GSON_BUILDER.setPrettyPrinting();
		Config.GSON_BUILDER.disableHtmlEscaping();
		Config.GSON_BUILDER.enableComplexMapKeySerialization();
	}
}
