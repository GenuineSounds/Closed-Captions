package com.genuineflix.caption.render;

import static com.genuineflix.caption.render.RenderHelper.drawTooltip;
import static com.genuineflix.caption.render.RenderHelper.fr;
import static com.genuineflix.caption.render.RenderHelper.mainColor;
import static com.genuineflix.caption.render.RenderHelper.outlineColor;
import static com.genuineflix.caption.render.RenderHelper.secondaryColor;
import static net.minecraft.client.renderer.entity.RenderManager.instance;

import java.util.List;

import net.minecraft.client.Minecraft;

import org.lwjgl.opengl.GL11;

import com.genuineflix.caption.caption.CaptionWorld;

public class RenderWorld {

	private static void drawCaption(final CaptionWorld caption, final float partialTicks) {
		if (outOfRenderDistance(caption))
			return;
		final double translateX = instance.viewerPosX - (caption.prevPosX + (caption.posX - caption.prevPosX) * partialTicks);
		final double translateY = instance.viewerPosY - (caption.prevPosY + (caption.posY - caption.prevPosY) * partialTicks) - caption.size;
		final double translateZ = instance.viewerPosZ - (caption.prevPosZ + (caption.posZ - caption.prevPosZ) * partialTicks);
		GL11.glTranslated(-translateX, -translateY, -translateZ);
		GL11.glRotated(instance.playerViewY + 180, 0, -0.5, 0);
		GL11.glRotated(instance.playerViewX, -1, 0, 0);
		final float scale = 0.02F * caption.getScale();
		GL11.glScalef(scale, -scale, scale);
		final int w = fr.getStringWidth(caption.getMessage());
		final int h = 8;
		final int x = -w / 2;
		final int y = -h;
		int alpha = (int) ((1 - Math.pow(1 - caption.getPercentGuess(partialTicks), 8)) * 0xD0);
		if (alpha < 28)
			alpha = 28;
		drawTooltip(x, y, w, h, alpha << 24 | mainColor & 0xFFFFFF, alpha << 24 | outlineColor & 0xFFFFFF, alpha << 24 | secondaryColor & 0xFFFFFF);
		GL11.glTranslated(0, 0, 1);
		fr.drawStringWithShadow(caption.getMessage(), x, y, alpha << 24 | 0xFFFFFF);
		GL11.glTranslated(0, 0, -1);
		GL11.glScalef(1F / scale, -(1F / scale), 1F / scale);
		GL11.glRotated(instance.playerViewX, 1, 0, 0);
		GL11.glRotated(instance.playerViewY - 180, 0, 1, 0);
		GL11.glTranslated(translateX, translateY, translateZ);
	}

	private static boolean outOfRenderDistance(final CaptionWorld caption) {
		int distance = (int) (16 * caption.getScale());
		if (distance < 16)
			distance = 16;
		if (caption.isWithin(Minecraft.getMinecraft().thePlayer, distance))
			return false;
		return true;
	}

	public static void render(final List<CaptionWorld> messages, final float partialTicks) {
		if (messages == null)
			return;
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		for (final CaptionWorld caption : messages)
			drawCaption(caption, partialTicks);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}
}
