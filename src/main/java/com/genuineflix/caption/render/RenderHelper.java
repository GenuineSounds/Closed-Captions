package com.genuineflix.caption.render;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;

public class RenderHelper {

	public static void drawGradientRect(final int x, final int y, int w, int h, final int color1, final int color2) {
		w += x;
		h += y;
		final float alpha1 = (color1 >> 24 & 0xFF) / 255F;
		final float red1 = (color1 >> 16 & 0xFF) / 255F;
		final float green1 = (color1 >> 8 & 0xFF) / 255F;
		final float blue1 = (color1 >> 0 & 0xFF) / 255F;
		final float alpha2 = (color2 >> 24 & 0xFF) / 255F;
		final float red2 = (color2 >> 16 & 0xFF) / 255F;
		final float green2 = (color2 >> 8 & 0xFF) / 255F;
		final float blue2 = (color2 >> 0 & 0xFF) / 255F;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		// GL11.glEnable(GL11.GL_BLEND);
		// GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glShadeModel(GL11.GL_SMOOTH);
		GL11.glBegin(GL11.GL_QUADS);
		GL11.glColor4f(red1, green1, blue1, alpha1);
		GL11.glVertex2i(w, y);
		GL11.glVertex2i(x, y);
		GL11.glColor4f(red2, green2, blue2, alpha2);
		GL11.glVertex2i(x, h);
		GL11.glVertex2i(w, h);
		GL11.glColor4f(1, 1, 1, 1);
		GL11.glEnd();
		GL11.glShadeModel(GL11.GL_FLAT);
		// GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	public static void drawTooltip(final int x, final int y, final int w, final int h, final int color1,
			final int color2, final int color3) {
		// Main
		RenderHelper.drawGradientRect(x - 3, y - 3, w + 6, h + 6, color1, color1);
		// Top bar
		RenderHelper.drawGradientRect(x - 3, y - 4, w + 6, 1, color1, color1);
		// Right Bar
		RenderHelper.drawGradientRect(x + w + 3, y - 3, 1, h + 6, color1, color1);
		// Bottom Bar
		RenderHelper.drawGradientRect(x - 3, y + h + 3, w + 6, 1, color1, color1);
		// Left Bar
		RenderHelper.drawGradientRect(x - 4, y - 3, 1, h + 6, color1, color1);
		GL11.glTranslated(0, 0, 0.01);
		// Top Line
		RenderHelper.drawGradientRect(x - 3, y - 3, w + 6, 1, color2, color2);
		// Right Line
		RenderHelper.drawGradientRect(x + w + 2, y - 2, 1, h + 4, color2, color3);
		// Bottom Line
		RenderHelper.drawGradientRect(x - 3, y + h + 2, w + 6, 1, color3, color3);
		// Left Line
		RenderHelper.drawGradientRect(x - 3, y - 2, 1, h + 4, color2, color3);
		GL11.glTranslated(0, 0, -0.01);
	}

	public static ScaledResolution res;
	public static final FontRenderer fr;
	public static final int mainColor = 0xC0100010;
	public static final int outlineColor = 0x505000FF;
	public static final int secondaryColor;

	static {
		secondaryColor = (RenderHelper.outlineColor & 0xFEFEFE) >> 1 | RenderHelper.outlineColor & 0xFF000000;
		fr = Minecraft.getMinecraft().getRenderManager().getFontRenderer();
	}
}
