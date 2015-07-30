package com.genuineflix.caption;

import java.io.File;

import com.genuineflix.caption.config.Config;
import com.genuineflix.caption.events.SoundEvents;
import com.genuineflix.caption.translation.TranslationSystem;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = ClosedCaption.MODID, name = ClosedCaption.NAME, version = ClosedCaption.VERSION)
public class ClosedCaption {

	@Instance(ClosedCaption.MODID)
	public static ClosedCaption instance;
	public static final String MODID = "ClosedCaption";
	public static final String NAME = "Closed Caption";
	public static final String VERSION = "1.0.15";
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
