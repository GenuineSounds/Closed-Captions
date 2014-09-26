package com.genuineminecraft.closedcaptions;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Map;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import com.genuineminecraft.closedcaptions.system.ClosedCaptionSystem;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLModDisabledEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = ClosedCaptions.MODID, name = ClosedCaptions.NAME, version = ClosedCaptions.VERSION)
public class ClosedCaptions {

	@Instance(ClosedCaptions.MODID)
	public static ClosedCaptions instance;
	public static final String MODID = "ClosedCaptions";
	public static final String NAME = "Closed Captions";
	public static final String VERSION = "1.7.10-r3";
	public static final Type TYPE = new TypeToken<Map<String, ArrayList<String>>>() {}.getType();
	private File folder;

	@EventHandler
	public void preload(FMLPreInitializationEvent event) {
		folder = new File(event.getModConfigurationDirectory(), "ClosedCaptions");
		folder.mkdirs();
		loadTranslations();
		saveTranslations();
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(ClosedCaptionSystem.getInstance());
		MinecraftForge.EVENT_BUS.register(this);
	}

	@EventHandler
	public void disable(FMLModDisabledEvent event) {
		System.out.println(event.toString());
	}

	@SubscribeEvent
	public void saveLoad(WorldEvent.Save event) {
		// loadTranslations();
		saveTranslations();
	}

	public void loadTranslations() {
		File file = new File(folder, "captions.json");
		if (!file.exists())
			return;
		Gson gson = new GsonBuilder().create();
		FileReader fr = null;
		try {
			fr = new FileReader(file);
			Map<String, ArrayList<String>> tc = gson.fromJson(fr, TYPE);
			if (tc != null)
				ClosedCaptionSystem.getInstance().translationSystem.translations = tc;
		} catch (Exception e) {} finally {
			try {
				if (fr != null)
					fr.close();
			} catch (Exception e) {}
		}
	}

	public void saveTranslations() {
		File file = new File(folder, "captions.json");
		GsonBuilder builder = new GsonBuilder();
		builder.setPrettyPrinting();
		Gson gson = builder.create();
		FileWriter rw = null;
		try {
			if (!file.exists())
				file.createNewFile();
			rw = new FileWriter(file);
			gson.toJson(ClosedCaptionSystem.getInstance().translationSystem.translations, TYPE, rw);
		} catch (Exception e) {} finally {
			try {
				if (rw != null)
					rw.close();
			} catch (Exception e) {}
		}
	}
}
