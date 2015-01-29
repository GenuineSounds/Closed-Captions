package com.genuineflix.caption.container;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import com.genuineflix.caption.caption.Caption;
import com.genuineflix.caption.caption.CaptionHUD;
import com.genuineflix.caption.render.RenderHUD;
import com.google.common.collect.ImmutableList;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ContainerHUD {

	protected List<CaptionHUD> messages = new ArrayList<CaptionHUD>();
	private ImmutableList<CaptionHUD> renderMessages;
	private long tick;

	public void add(final CaptionHUD caption) {
		if (caption.isDisabled())
			return;
		if (Loader.isModLoaded("BattleText") && caption.key.contains("game.player"))
			return;
		synchronized (messages) {
			for (int i = 0; i < messages.size(); i++) {
				final Caption old = messages.get(i);
				if (old.nameEquals(caption.key)) {
					old.resetTime();
					return;
				}
			}
			messages.add(caption);
		}
	}

	public void directMessage(final String message) {
		synchronized (messages) {
			messages.add(new CaptionHUD(message));
		}
	}

	@SubscribeEvent
	public void render(final RenderGameOverlayEvent.Post event) {
		if (event.type != ElementType.ALL)
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
			ContainerHelper.passIMCMessagesToList(messages);
			ContainerHelper.removeOldCaptions(messages);
			renderMessages = ImmutableList.copyOf(messages);
		}
	}
}
