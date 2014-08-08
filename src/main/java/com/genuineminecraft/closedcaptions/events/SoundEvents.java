package com.genuineminecraft.closedcaptions.events;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;

import com.genuineminecraft.closedcaptions.captions.CaptionsContainer;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class SoundEvents {

	@SubscribeEvent
	public void eventEntity(PlaySoundAtEntityEvent event) {
		if (event == null || event.entity == null || event.name == null || event.name.isEmpty() || event.name.equals("none"))
			return;
		if (Minecraft.getMinecraft().thePlayer == null)
			return;
		if (event.entity.getDistanceToEntity(Minecraft.getMinecraft().thePlayer) > 32)
			return;
		process(event.name, event.entity, event.volume, event.pitch);
	}

	@SubscribeEvent
	public void eventISound(PlaySoundEvent17 event) {
		if (event == null || event.sound == null || event.name == null || event.name.isEmpty() || event.name.equals("none"))
			return;
		if (Minecraft.getMinecraft().thePlayer == null || event.category == null)
			return;
		switch (event.category) {
			case PLAYERS:
			case ANIMALS:
			case MOBS:
				break;
			case MASTER:
			case AMBIENT:
			case BLOCKS:
			case MUSIC:
			case RECORDS:
			case WEATHER:
			default:
				process(event.name, event.sound);
				break;
		}
	}

	private void process(String name, ISound sound) {
		CaptionsContainer.getInstance().createCaption(name, sound);
	}

	private void process(String name, Entity entity, float volume, float pitch) {
		CaptionsContainer.getInstance().createCaption(name, entity, volume, pitch);
	}
}
