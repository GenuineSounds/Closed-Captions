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

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ContainerHUD {

	private final List<CaptionHUD> messages = new ArrayList<CaptionHUD>();
	private long tick;

	public void add(final CaptionHUD caption) {
		if (caption.isDisabled())
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

	public void directMessage(final String message, final float amount) {
		dm(new CaptionHUD(message, amount));
	}

	public void directMessage(final String message, final float amount, final int ticks) {
		dm(new CaptionHUD(message, amount, ticks));
	}

	public void dm(final CaptionHUD caption) {
		synchronized (messages) {
			for (final CaptionHUD old : messages) {
				if (!old.getMessage().equals(caption.getMessage()))
					continue;
				old.amount += caption.amount;
				old.resetTime();
				return;
			}
			messages.add(caption);
		}
	}

	public void parseIMCMessages() {
		for (final IMCMessage imc : FMLInterModComms.fetchRuntimeMessages(ClosedCaption.instance)) {
			if (!imc.key.equals(Caption.DIRECT_MESSAGE_KEY))
				continue;
			if (imc.isNBTMessage()) {
				final NBTTagCompound tag = imc.getNBTValue();
				final float amount = tag.getFloat("amount");
				final StringBuilder message = new StringBuilder();
				final String type = tag.getString("type");
				if ("healing".equals(type)) {
					message.append("Healing: ");
					message.append(ChatFormatting.GREEN.toString());
				} else
					message.append(tag.getString("message"));
				if (tag.hasKey("ticks"))
					directMessage(message.toString(), amount, tag.getInteger("ticks"));
				else
					directMessage(message.toString(), amount);
			} else if (imc.isStringMessage())
				directMessage(imc.getStringValue(), 0);
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
}
