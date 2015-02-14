package com.genuineflix.caption.render;

import java.util.List;

import net.minecraft.client.gui.ScaledResolution;

import org.lwjgl.opengl.GL11;

import com.genuineflix.caption.caption.CaptionHUD;
import com.mojang.realmsclient.gui.ChatFormatting;

public class RenderHUD {

	private static void drawCaptions(final List<CaptionHUD> messages2D, final float deltaTime) {
		double template = Math.ceil(RenderHelper.res.getScaledWidth_double() / 2) - 98;
		if (template > 180)
			template = 180;
		int w = 0;
		int size = 0;
		double mostPercent = 0;
		for (final CaptionHUD caption : messages2D) {
			int swidth = RenderHelper.fr.getStringWidth(caption.getMessage() + (caption.amount > 0 ? caption.amount : "")) + 3;
			if (swidth < template)
				swidth = (int) template;
			if (w < swidth)
				w = swidth;
			size++;
			final double percent = caption.getPercentGuess(deltaTime);
			if (mostPercent < percent)
				mostPercent = percent;
		}
		if (size < 1)
			size = 1;
		final int h = size * 10 - 2;
		final int x = -w;
		int y = -h;
		final double moveInTips = Math.pow(1 - mostPercent * 4, 4) * w;
		if (mostPercent < 0.25)
			GL11.glTranslated(moveInTips, 0, 0);
		RenderHelper.drawTooltip(x, y, w, h, RenderHelper.mainColor, RenderHelper.outlineColor, RenderHelper.secondaryColor);
		if (mostPercent < 0.25)
			GL11.glTranslated(-moveInTips, 0, 0);
		GL11.glTranslated(0, 0, 1);
		GL11.glEnable(GL11.GL_BLEND);
		for (final CaptionHUD caption : messages2D) {
			final double action = Math.pow(1 - caption.getPercentGuess(deltaTime), 8);
			int alpha = (int) ((1 - action) * 0xFF);
			if (alpha < 28)
				alpha = 28;
			final double fadeMove = action * w;
			GL11.glTranslated(fadeMove, 0, 0);
			RenderHelper.fr.drawStringWithShadow(caption.getMessage() + (caption.amount > 0 ? caption.amount : "") + ChatFormatting.RESET, x + 1, y, alpha << 24 | 0xFFFFFF);
			GL11.glTranslated(-fadeMove, 0, 0);
			y += 10;
		}
		GL11.glTranslated(0, 0, -1);
	}

	public static void render(final List<CaptionHUD> messages, final ScaledResolution resolution, final float partialTicks) {
		if (messages == null)
			return;
		RenderHelper.res = resolution;
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glPushMatrix();
		GL11.glTranslated(RenderHelper.res.getScaledWidth(), RenderHelper.res.getScaledHeight() - 32, 100);
		RenderHUD.drawCaptions(messages, partialTicks);
		GL11.glTranslated(-RenderHelper.res.getScaledWidth(), -(RenderHelper.res.getScaledHeight() - 32), -100);
		GL11.glPopMatrix();
	}
}
