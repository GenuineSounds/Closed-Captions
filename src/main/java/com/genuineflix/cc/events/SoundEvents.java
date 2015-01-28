package com.genuineflix.cc.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;

import com.genuineflix.cc.caption.Caption2D;
import com.genuineflix.cc.caption.Caption3D;
import com.genuineflix.cc.system.System2D;
import com.genuineflix.cc.system.System3D;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class SoundEvents {

	public final System2D system2D = new System2D();
	public final System3D system3D = new System3D();

	public void registerEvents() {
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(system2D);
		FMLCommonHandler.instance().bus().register(system2D);
		MinecraftForge.EVENT_BUS.register(system3D);
		FMLCommonHandler.instance().bus().register(system3D);
	}

	@SubscribeEvent
	public void eventEntity(final PlaySoundAtEntityEvent event) {
		if (event == null || event.name == null || event.name.isEmpty() || event.name.equalsIgnoreCase("none") || event.name.endsWith(":none") || event.entity instanceof EntityItem)
			return;
		createCaption(event.name, event.entity, event.volume, event.pitch);
	}

	@SubscribeEvent
	public void eventISound(final PlaySoundEvent17 event) {
		if (event.category == null || event.sound == null || event.name == null || event.name.isEmpty() || event.name.equals("none"))
			return;
		switch (event.category) {
			case PLAYERS:
			case ANIMALS:
			case MOBS:
				return;
			default:
				createCaption(event.name, event.sound);
				break;
		}
	}

	public void createCaption(final String name, final Entity entity, final float volume, final float pitch) {
		if (entity == null || Minecraft.getMinecraft().thePlayer != null && entity.equals(Minecraft.getMinecraft().thePlayer))
			system2D.add(new Caption2D(name, volume, pitch));
		else
			system3D.add(new Caption3D(name, entity, volume, pitch));
	}

	public void createCaption(final String name, final ISound sound) {
		final Caption2D c2d = new Caption2D(name, sound.getVolume(), sound.getPitch());
		final Caption3D c3d = new Caption3D(name, sound, sound.getVolume(), sound.getPitch());
		if (sound.getXPosF() == 0 && sound.getYPosF() == 0 && sound.getZPosF() == 0)
			system2D.add(c2d);
		else if (c3d.isWithin(Minecraft.getMinecraft().thePlayer, 5))
			system2D.add(c2d);
		else
			system3D.add(c3d);
	}
}
