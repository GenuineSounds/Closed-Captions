package com.genuineflix.caption.container;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import com.genuineflix.caption.ClosedCaption;
import com.genuineflix.caption.caption.Caption;
import com.genuineflix.caption.caption.CaptionHUD;
import com.genuineflix.caption.render.RenderHUD;
import com.google.common.collect.ImmutableList;
import com.mojang.realmsclient.gui.ChatFormatting;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ContainerHUD {

	private List<CaptionHUD> messages = new ArrayList<CaptionHUD>();
	private long tick;

	public void add(final CaptionHUD caption) {
		if (caption.isDisabled())
			return;
		if (Loader.isModLoaded("BattleText") && caption.key.contains("game.player"))
			return;
		synchronized (messages) {
			for (final CaptionHUD old : messages) {
				if (!old.nameEquals(caption.key))
					continue;
				old.resetTime();
				return;
			}
			messages.add(caption);
		}
	}

	public void directMessage(final String message, float amount) {
		final CaptionHUD caption = new CaptionHUD(message, amount);
		synchronized (messages) {
			for (final CaptionHUD old : messages) {
				if (!old.message.equals(caption.message))
					continue;
				old.amount += amount;
				old.resetTime();
				return;
			}
			messages.add(caption);
		}
	}

	@SubscribeEvent
	public void render(final RenderGameOverlayEvent.Post event) {
		if (event.type != ElementType.ALL)
			return;
		tick();
		RenderHUD.render(ImmutableList.copyOf(messages), event.resolution, event.partialTicks);
	}

	private void tick() {
		final long tick = RenderManager.instance.worldObj.getTotalWorldTime();
		if (this.tick == tick)
			return;
		this.tick = tick;
		final List<Caption> removalQueue = new ArrayList<Caption>();
		synchronized (messages) {
			for (final Caption caption : messages)
				if (!caption.tick() || caption.isDisabled())
					removalQueue.add(caption);
			parseIMCMessages();
			messages.removeAll(removalQueue);
		}
	}

	public void parseIMCMessages() {
		for (final IMCMessage imc : FMLInterModComms.fetchRuntimeMessages(ClosedCaption.instance)) {
			if (!imc.key.equals(Caption.DIRECT_MESSAGE_KEY))
				continue;
			final NBTTagCompound tag = imc.getNBTValue();
			float amount = tag.getFloat("amount");
			final StringBuilder message = new StringBuilder();
			final String type = tag.getString("type");
			if ("healing".equals(type)) {
				message.append("Healing: ");
				message.append(ChatFormatting.GREEN.toString());
			}
			if ("damage".equals(type))
				message.append(tag.getString("message"));
			directMessage(message.toString(), amount);
		}
	}
}
