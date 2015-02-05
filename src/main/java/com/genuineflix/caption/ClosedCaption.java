package com.genuineflix.caption;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.genuineflix.caption.config.Config;
import com.genuineflix.caption.events.SoundEvents;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ClosedCaption.MODID, name = ClosedCaption.NAME, version = ClosedCaption.VERSION, guiFactory = "com.genuineflix.caption.gui.ConfigGuiFactory")
public class ClosedCaption {

	@Instance(ClosedCaption.MODID)
	public static ClosedCaption instance;
	public static final String MODID = "ClosedCaption";
	public static final String NAME = "Closed Caption";
	public static final String VERSION = "1.0.12";
	public static final Logger log = LogManager.getLogger(MODID);
	public static final SoundEvents soundEvents = new SoundEvents();
	private static final Config config = new Config();

	public static Configuration config() {
		return config.main;
	}

	@EventHandler
	public void pre(final FMLPreInitializationEvent event) {
		config.pre(new File(event.getModConfigurationDirectory(), "ClosedCaption"));
	}

	@EventHandler
	public void init(final FMLInitializationEvent event) {
		config.init();
		MinecraftForge.EVENT_BUS.register(config);
		soundEvents.registerEvents();
	}
}
