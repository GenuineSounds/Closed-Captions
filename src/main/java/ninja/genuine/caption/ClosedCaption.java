package ninja.genuine.caption;

import java.io.File;

import net.minecraftforge.common.MinecraftForge;
import ninja.genuine.caption.config.Config;
import ninja.genuine.caption.events.SoundEvents;
import ninja.genuine.caption.translation.TranslationSystem;
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
	public static final String VERSION = "1.0.16";
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
