package com.genuineflix.cc.render;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderManager;

import org.lwjgl.opengl.GL11;

import com.genuineflix.cc.caption.Caption3D;

public class Render3D {

	private final FontRenderer fr;
	private final int mainColor = 0xC0100010;
	private final int outlineColor = 0x505000FF;
	private final int secondaryColor;

	public Render3D() {
		secondaryColor = (outlineColor & 0xFEFEFE) >> 1 | outlineColor & 0xFF000000;
		fr = Minecraft.getMinecraft().fontRenderer;
	}

	public void render(final List<Caption3D> messages, final float partialTicks) {
		GL11.glPushMatrix();
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		drawAllCaptions(messages, partialTicks);
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}

	private void drawAllCaptions(final List<Caption3D> messages, final float partialTicks) {
		for (final Caption3D caption : messages) {
			if (outOfRenderDistance(caption))
				continue;
			final double x = RenderManager.instance.viewerPosX - (caption.prevPosX + (caption.posX - caption.prevPosX) * partialTicks);
			final double y = RenderManager.instance.viewerPosY - (caption.prevPosY + (caption.posY - caption.prevPosY) * partialTicks) - caption.size;
			final double z = RenderManager.instance.viewerPosZ - (caption.prevPosZ + (caption.posZ - caption.prevPosZ) * partialTicks);
			GL11.glTranslated(-x, -y, -z);
			GL11.glRotated(RenderManager.instance.playerViewY + 180, 0, -0.5, 0);
			GL11.glRotated(RenderManager.instance.playerViewX, -1, 0, 0);
			final float scale = 0.02F * caption.getScale();
			GL11.glScalef(scale, -scale, scale);
			drawCaption(caption, partialTicks);
			GL11.glScalef(1F / scale, -(1F / scale), 1F / scale);
			GL11.glRotated(RenderManager.instance.playerViewX, 1, 0, 0);
			GL11.glRotated(RenderManager.instance.playerViewY - 180, 0, 1, 0);
			GL11.glTranslated(x, y, z);
		}
	}

	private boolean outOfRenderDistance(final Caption3D caption) {
		int distance = (int) (16 * caption.getScale());
		if (distance < 16)
			distance = 16;
		if (caption.isWithin(Minecraft.getMinecraft().thePlayer, distance))
			return false;
		return true;
	}

	private void drawCaption(final Caption3D caption, final float partialTicks) {
		final int w = fr.getStringWidth(caption.message);
		final int h = 8;
		final int x = -w / 2;
		final int y = -h;
		int alpha = (int) ((1 - Math.pow(1 - caption.getPercentGuess(partialTicks), 8)) * 0xD0);
		if (alpha < 28)
			alpha = 28;
		RenderCommon.drawTooltip(x, y, w, h, alpha << 24 | mainColor & 0xFFFFFF, alpha << 24 | outlineColor & 0xFFFFFF, alpha << 24 | secondaryColor & 0xFFFFFF);
		GL11.glTranslated(0, 0, 1);
		fr.drawStringWithShadow(caption.message, x, y, alpha << 24 | 0xFFFFFF);
		GL11.glTranslated(0, 0, -1);
	}
}
