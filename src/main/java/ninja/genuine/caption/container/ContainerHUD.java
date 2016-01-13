package ninja.genuine.caption.container;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import ninja.genuine.caption.ClosedCaption;
import ninja.genuine.caption.caption.Caption;
import ninja.genuine.caption.caption.CaptionHUD;
import ninja.genuine.caption.render.RenderHUD;

import com.google.common.collect.ImmutableList;
import com.mojang.realmsclient.gui.ChatFormatting;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ContainerHUD {

	private final List<CaptionHUD> messages = new ArrayList<CaptionHUD>();
	private ImmutableList<CaptionHUD> renderMessages;
	private long tick;

	@SubscribeEvent
	public void render(final RenderGameOverlayEvent.Post event) {
		if (event.type != ElementType.ALL || !ClosedCaption.enabled)
			return;
		tick();
		RenderHUD.render(renderMessages, event.resolution, event.partialTicks);
	}

	private void tick() {
		final long tick = RenderManager.instance.worldObj.getTotalWorldTime();
		if (this.tick == tick)
			return;
		this.tick = tick;
		synchronized (messages) {
			final List<Caption> removalQueue = new ArrayList<Caption>();
			for (final Caption caption : messages)
				if (!caption.tick() || caption.isDisabled())
					removalQueue.add(caption);
			parseIMCMessages();
			messages.removeAll(removalQueue);
			renderMessages = ImmutableList.copyOf(messages);
		}
	}

	public void message(final CaptionHUD caption) {
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

	private void directMessage(final CaptionHUD caption) {
		for (final CaptionHUD old : messages) {
			if (!old.getMessage().equals(caption.getMessage()))
				continue;
			old.amount += caption.amount;
			old.resetTime();
			return;
		}
		messages.add(caption);
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
					directMessage(new CaptionHUD(message.toString(), amount, tag.getInteger("ticks")));
				else
					directMessage(new CaptionHUD(message.toString(), amount));
			} else if (imc.isStringMessage())
				directMessage(new CaptionHUD(imc.getStringValue(), 0));
		}
	}
}
