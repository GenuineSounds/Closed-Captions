package com.genuineflix.caption;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.genuineflix.caption.config.Config;
import com.genuineflix.caption.events.SoundEvents;
import com.genuineflix.caption.translation.TranslationSystem;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ClosedCaption.MODID, name = ClosedCaption.NAME, version = ClosedCaption.VERSION)
public class ClosedCaption {

	@Instance(ClosedCaption.MODID)
	public static ClosedCaption instance;
	public static final String MODID = "ClosedCaption";
	public static final String NAME = "Closed Caption";
	public static final String VERSION = "1.0.13";
	public static final Logger log = LogManager.getLogger(ClosedCaption.MODID);
	public static final SoundEvents soundEvents = new SoundEvents();
	public static final Config config = new Config();
	public static boolean enabled = true;

	@EventHandler
	public void pre(final FMLPreInitializationEvent event) {
		ClosedCaption.config.pre(new File(event.getModConfigurationDirectory(), "ClosedCaption"));
		MinecraftForge.EVENT_BUS.register(ClosedCaption.config);
		ClosedCaption.soundEvents.registerEvents();
	}

	@EventHandler
	public void init(final FMLInitializationEvent event) {
		ClosedCaption.config.init();
	}

	@EventHandler
	public void post(final FMLPostInitializationEvent event) {
		TranslationSystem.instance.handleIMCMessages();
	}
}
