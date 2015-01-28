package com.genuineflix.cc.system;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;

import com.genuineflix.cc.ClosedCaption;
import com.genuineflix.cc.caption.Caption;
import com.genuineflix.cc.caption.Caption2D;
import com.genuineflix.cc.render.Render2D;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;

public class System2D {

	private final Render2D render2D = new Render2D();
	private final List<Caption2D> messages2D = new ArrayList<Caption2D>();

	public synchronized void add(final Caption2D caption) {
		if (Loader.isModLoaded("BattleText"))
			if (caption.key.contains("game.player"))
				return;
		if (caption.isDisabled())
			return;
		for (final Caption2D old : messages2D)
			if (old.nameEquals(caption.key)) {
				old.resetTime();
				return;
			}
		messages2D.add(caption);
	}

	public synchronized void directMessage(final String message) {
		messages2D.add(new Caption2D(message));
	}

	@SubscribeEvent
	public synchronized void render(final RenderGameOverlayEvent.Post event) {
		if (event.type == ElementType.ALL)
			render2D.render(messages2D, event.resolution, event.partialTicks);
	}

	@SubscribeEvent
	public synchronized void tick(final ClientTickEvent event) {
		if (event.phase == Phase.START)
			return;
		final Minecraft mc = Minecraft.getMinecraft();
		if (mc.thePlayer == null || mc.currentScreen != null && mc.currentScreen.doesGuiPauseGame())
			return;
		for (final IMCMessage imc : FMLInterModComms.fetchRuntimeMessages(ClosedCaption.instance))
			if (imc.key.equals(Caption.DIRECT_MESSAGE_KEY))
				messages2D.add(new Caption2D(imc.getStringValue()));
		final List<Caption> removalQueue = new ArrayList<Caption>();
		for (final Caption caption : messages2D)
			if (!caption.tick() || caption.isDisabled())
				removalQueue.add(caption);
		messages2D.removeAll(removalQueue);
	}
}
