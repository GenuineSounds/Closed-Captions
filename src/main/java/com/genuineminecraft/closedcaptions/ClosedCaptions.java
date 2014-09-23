package com.genuineminecraft.closedcaptions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;

import com.genuineminecraft.closedcaptions.captions.CaptionsContainer;
import com.genuineminecraft.closedcaptions.events.RenderEvents;
import com.genuineminecraft.closedcaptions.events.SoundEvents;

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
	public static final String VERSION = "1.7.10-r2";
	private File folder;

	@EventHandler
	public void preload(FMLPreInitializationEvent event) {
		folder = new File(event.getModConfigurationDirectory(), "ClosedCaptions");
		folder.mkdirs();
		loadNames();
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(new SoundEvents());
		MinecraftForge.EVENT_BUS.register(new RenderEvents());
		MinecraftForge.EVENT_BUS.register(this);
	}

	@EventHandler
	public void disable(FMLModDisabledEvent event) {
		System.out.println(event.toString());
	}

	@SubscribeEvent
	public void saveLoad(WorldEvent.Save event) {
		loadNames();
		saveNames();
		loadNames();
	}

	public void loadNames() {
		try {
			File file = new File(folder, "captions.cfg");
			if (!file.exists())
				file.createNewFile();
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			while ((line = br.readLine()) != null) {
				if (line.isEmpty() || line.startsWith("#"))
					continue;
				String[] values = line.split("=");
				if (values.length > 1)
					CaptionsContainer.getInstance().addTranslation(values[0], values[1]);
				else if (values.length > 0)
					CaptionsContainer.getInstance().addTranslation(values[0], "");
			}
			br.close();
		} catch (Exception e) {}
	}

	public void saveNames() {
		try {
			File file = new File(folder, "captions.cfg");
			if (!file.exists())
				file.createNewFile();
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			List<String> newList = new ArrayList<String>();
			for (Entry<String, String> entry : CaptionsContainer.getInstance().getTranslationMap().entrySet()) {
				newList.add(entry.getKey() + "=" + entry.getValue());
			}
			Collections.sort(newList);
			for (String line : newList) {
				bw.append(line);
				bw.newLine();
			}
			bw.close();
		} catch (Exception e) {}
	}
}
