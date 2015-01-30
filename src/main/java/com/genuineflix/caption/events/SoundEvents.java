package com.genuineflix.caption.events;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;

import com.genuineflix.caption.caption.CaptionHUD;
import com.genuineflix.caption.caption.CaptionWorld;
import com.genuineflix.caption.container.ContainerHUD;
import com.genuineflix.caption.container.ContainerWorld;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class SoundEvents {

	private static boolean captionObjectCheck(final CaptionWorld caption) {
		return caption.entity instanceof EntityItem || caption.entity instanceof EntityXPOrb || caption.sound instanceof PositionedSoundRecord;
	}

	private static boolean isCaptionBroken(final CaptionWorld caption) {
		return captionObjectCheck(caption) || ((int) caption.posX | (int) caption.posY | (int) caption.posZ) == 0;
	}

	private static boolean isNameBroken(final String name) {
		return name == null || name.isEmpty() || name.equalsIgnoreCase("none") || name.endsWith(":none");
	}

	private static boolean isRemote(final PlaySoundAtEntityEvent event) {
		return event == null || event.entity == null || event.entity.worldObj == null || event.entity.worldObj.isRemote;
	}

	public final ContainerHUD hud = new ContainerHUD();
	public final ContainerWorld world = new ContainerWorld();

	public void createCaption(final String name, final Entity entity, final float volume, final float pitch) {
		final CaptionWorld cap3d = new CaptionWorld(name, entity, volume, pitch);
		if (isCaptionBroken(cap3d))
			hud.add(new CaptionHUD(name, volume, pitch));
		else
			world.add(cap3d);
	}

	public void createCaption(final String name, final ISound sound) {
		final CaptionWorld caption = new CaptionWorld(name, sound, sound.getVolume(), sound.getPitch());
		if (isCaptionBroken(caption))
			hud.add(new CaptionHUD(name, sound.getVolume(), sound.getPitch()));
		else
			world.add(caption);
	}

	@SubscribeEvent
	public void eventEntity(final PlaySoundAtEntityEvent event) {
		if (isRemote(event))
			return;
		if (isNameBroken(event.name))
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

	public void registerEvents() {
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(hud);
		FMLCommonHandler.instance().bus().register(hud);
		MinecraftForge.EVENT_BUS.register(world);
		FMLCommonHandler.instance().bus().register(world);
	}
}
