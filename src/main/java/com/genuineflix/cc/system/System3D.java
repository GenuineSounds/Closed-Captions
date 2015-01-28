package com.genuineflix.cc.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import com.genuineflix.cc.caption.Caption;
import com.genuineflix.cc.caption.Caption3D;
import com.genuineflix.cc.render.Render3D;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class System3D {

	private final Render3D render3D = new Render3D();
	private final List<Caption3D> messages3D = new ArrayList<Caption3D>();

	public synchronized void add(final Caption3D caption) {
		if (caption.isDisabled())
			return;
		final List<Caption> removal = new ArrayList<Caption>();
		boolean simpleReset = false;
		for (final Caption3D old : messages3D) {
			if (old.equalTo(caption)) {
				simpleReset = true;
				old.resetTime();
				continue;
			}
			if (caption.isWithin(old, 0.1))
				removal.add(old);
		}
		messages3D.removeAll(removal);
		if (simpleReset)
			return;
		messages3D.add(caption);
		Collections.sort(messages3D, Caption3D.DISTANCE);
	}

	@SubscribeEvent
	public synchronized void render3D(final RenderWorldLastEvent event) {
		render3D.render(messages3D, event.partialTicks);
	}

	@SubscribeEvent
	public synchronized void tick(final ClientTickEvent event) {
		if (event.phase == Phase.START)
			return;
		final Minecraft mc = Minecraft.getMinecraft();
		if (mc.thePlayer == null || mc.currentScreen != null && mc.currentScreen.doesGuiPauseGame())
			return;
		final List<Caption> removalQueue = new ArrayList<Caption>();
		for (final Caption caption : messages3D)
			if (!caption.tick() || caption.isDisabled())
				removalQueue.add(caption);
		messages3D.removeAll(removalQueue);
	}
}
