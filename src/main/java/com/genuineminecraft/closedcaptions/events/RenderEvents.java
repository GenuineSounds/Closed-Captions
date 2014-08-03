package com.genuineminecraft.closedcaptions.events;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import com.genuineminecraft.closedcaptions.captions.CaptionsContainer;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class RenderEvents {

	@SubscribeEvent
	public void onTickInGame(RenderGameOverlayEvent.Post event) {
		if (event.type == ElementType.ALL)
			CaptionsContainer.getInstance().render2D(event.resolution, event.partialTicks);
	}

	@SubscribeEvent
	public void render3D(RenderWorldLastEvent event) {
		CaptionsContainer.getInstance().render3D(event.partialTicks);
	}
}
