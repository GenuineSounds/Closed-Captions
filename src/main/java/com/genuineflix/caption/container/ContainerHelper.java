package com.genuineflix.caption.container;

import java.util.ArrayList;
import java.util.List;

import com.genuineflix.caption.ClosedCaption;
import com.genuineflix.caption.caption.Caption;
import com.genuineflix.caption.caption.CaptionHUD;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;

public class ContainerHelper {

	public static void passIMCMessagesToList(final List<CaptionHUD> messages) {
		for (final IMCMessage imc : FMLInterModComms.fetchRuntimeMessages(ClosedCaption.instance))
			if (imc.key.equals(Caption.DIRECT_MESSAGE_KEY))
				messages.add(new CaptionHUD(imc.getStringValue()));
	}

	public static void removeOldCaptions(final List<? extends Caption> list) {
		final List<Caption> removalQueue = new ArrayList<Caption>();
		for (final Caption caption : list)
			if (!caption.tick() || caption.isDisabled())
				removalQueue.add(caption);
		list.removeAll(removalQueue);
	}
}
