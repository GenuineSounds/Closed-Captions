package ninja.genuine.caption.render;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import ninja.genuine.caption.caption.CaptionWorld;

import org.lwjgl.opengl.GL11;

public class RenderWorld {

	private static void drawCaption(final CaptionWorld caption, final float partialTicks) {
		if (RenderWorld.outOfRenderDistance(caption))
			return;
		final double translateX = RenderManager.instance.viewerPosX - (caption.prevPosX + (caption.posX - caption.prevPosX) * partialTicks);
		final double translateY = RenderManager.instance.viewerPosY - (caption.prevPosY + (caption.posY - caption.prevPosY) * partialTicks) - caption.size;
		final double translateZ = RenderManager.instance.viewerPosZ - (caption.prevPosZ + (caption.posZ - caption.prevPosZ) * partialTicks);
		GL11.glTranslated(-translateX, -translateY, -translateZ);
		GL11.glRotated(RenderManager.instance.playerViewY + 180, 0, -0.5, 0);
		GL11.glRotated(RenderManager.instance.playerViewX, -1, 0, 0);
		final double scale = 0.02 * caption.getScale();
		GL11.glScaled(scale, -scale, scale);
		final int w = RenderHelper.fr.getStringWidth(caption.getMessage());
		final int h = 8;
		final int x = -w / 2;
		final int y = -h;
		int alpha = (int) ((1 - Math.pow(1 - caption.getPercentGuess(partialTicks), 8)) * 0xFF) + 1;
		if (alpha < 5)
			alpha = 5;
		RenderHelper.drawTooltip(x, y, w, h, (alpha << 24) | (RenderHelper.mainColor & 0xFFFFFF), (alpha << 24) | (RenderHelper.outlineColor & 0xFFFFFF), (alpha << 24) | (RenderHelper.secondaryColor & 0xFFFFFF));
		GL11.glTranslated(0, 0, 0.01);
		RenderHelper.fr.drawString(caption.getMessage(), x + 1, y, (alpha << 24) + 0xFFFFFF);
		GL11.glTranslated(0, 0, -0.01);
		GL11.glScaled(1D / scale, -(1D / scale), 1D / scale);
		GL11.glRotated(RenderManager.instance.playerViewX, 1, 0, 0);
		GL11.glRotated(RenderManager.instance.playerViewY - 180, 0, 1, 0);
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
		//GL11.glDisable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		for (final CaptionWorld caption : messages)
			RenderWorld.drawCaption(caption, partialTicks);
		GL11.glDisable(GL11.GL_BLEND);
		//GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}
}
