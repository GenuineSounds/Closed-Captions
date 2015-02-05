package com.genuineflix.caption;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.genuineflix.caption.events.SoundEvents;
import com.genuineflix.caption.translation.TranslationSystem;
import com.google.common.base.Charsets;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = ClosedCaption.MODID, name = ClosedCaption.NAME, version = ClosedCaption.VERSION)
public class ClosedCaption {

	@Instance(ClosedCaption.MODID)
	public static ClosedCaption instance;
	public static final String MODID = "ClosedCaption";
	public static final String NAME = "Closed Caption";
	public static final String VERSION = "1.0.12";
	public static final Logger log = LogManager.getLogger(MODID);
	private static final GsonBuilder GSON_BUILDER = new GsonBuilder();
	private static final Type GSON_TYPE = new TypeToken<Map<String, List<String>>>() {}.getType();
	private File folder;
	static {
		GSON_BUILDER.setPrettyPrinting();
		GSON_BUILDER.disableHtmlEscaping();
		GSON_BUILDER.enableComplexMapKeySerialization();
	}

	@EventHandler
	public void init(final FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
		new SoundEvents().registerEvents();
	}

	public void loadTranslations() {
		final File file = new File(folder, "translations.json");
		if (!file.exists())
			return;
		final Gson gson = GSON_BUILDER.create();
		FileReader fr = null;
		try {
			fr = new FileReader(file);
			final Map<String, List<String>> ts = gson.fromJson(fr, ClosedCaption.GSON_TYPE);
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

	@EventHandler
	public void pre(final FMLPreInitializationEvent event) {
		folder = new File(event.getModConfigurationDirectory(), "ClosedCaption");
		folder.mkdirs();
		loadTranslations();
		saveTranslations();
	}

	@SubscribeEvent
	public void saveLoad(final WorldEvent.Save event) {
		loadTranslations();
		saveTranslations();
	}

	public void saveTranslations() {
		final File file = new File(folder, "translations.json");
		final Gson gson = GSON_BUILDER.create();
		Writer wr = null;
		try {
			if (!file.exists())
				file.createNewFile();
			wr = new OutputStreamWriter(new FileOutputStream(file), Charsets.UTF_8);
			gson.toJson(TranslationSystem.instance.getMap(), ClosedCaption.GSON_TYPE, wr);
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
}
