package com.genuineflix.caption.container;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import com.genuineflix.caption.caption.Caption;
import com.genuineflix.caption.caption.CaptionWorld;
import com.genuineflix.caption.render.RenderWorld;
import com.google.common.collect.ImmutableList;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ContainerWorld {

	protected List<CaptionWorld> messages = new ArrayList<CaptionWorld>();
	private ImmutableList<CaptionWorld> renderMessages;
	private long tick;

	public void add(final CaptionWorld caption) {
		if (caption.isDisabled())
			return;
		final List<Caption> removal = new ArrayList<Caption>();
		boolean simpleReset = false;
		synchronized (messages) {
			for (int i = 0; i < messages.size(); i++) {
				final CaptionWorld old = messages.get(i);
				if (old.equalTo(caption)) {
					simpleReset = true;
					old.resetTime();
					continue;
				}
				if (caption.isWithin(old, 0.1))
					removal.add(old);
			}
			messages.removeAll(removal);
			if (simpleReset)
				return;
			messages.add(caption);
			Collections.sort(messages, CaptionWorld.DISTANCE);
		}
	}

	@SubscribeEvent
	public void render3D(final RenderWorldLastEvent event) {
		tick();
		RenderWorld.render(renderMessages, event.partialTicks);
	}

	public void tick() {
		final long tick = RenderManager.instance.worldObj.getTotalWorldTime();
		if (this.tick == tick)
			return;
		this.tick = tick;
		synchronized (messages) {
			ContainerHelper.removeOldCaptions(messages);
			renderMessages = ImmutableList.copyOf(messages);
		}
	}
}