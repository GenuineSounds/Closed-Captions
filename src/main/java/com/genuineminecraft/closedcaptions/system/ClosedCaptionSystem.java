package com.genuineminecraft.closedcaptions.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.event.sound.PlaySoundEvent17;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;

import org.lwjgl.opengl.GL11;

import com.genuineminecraft.closedcaptions.ClosedCaptions;
import com.genuineminecraft.closedcaptions.captions.Caption;
import com.genuineminecraft.closedcaptions.captions.Caption2D;
import com.genuineminecraft.closedcaptions.captions.Caption3D;
import com.genuineminecraft.closedcaptions.captions.Translations;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLInterModComms.IMCMessage;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;

public class ClosedCaptionSystem {

	private static ClosedCaptionSystem instance;
	public static final String BT_MOD_NAME = "BattleText";
	private static FontRenderer fr;

	public static boolean btIsLoaded() {
		return Loader.isModLoaded(ClosedCaptionSystem.BT_MOD_NAME);
	}

	public static void createDirectMessage(String message) {
		synchronized (ClosedCaptionSystem.getInstance().messages2D) {
			ClosedCaptionSystem.getInstance().messages2D.add(new Caption2D(message));
		}
	}

	public static ClosedCaptionSystem getInstance() {
		if (ClosedCaptionSystem.instance == null)
			ClosedCaptionSystem.instance = new ClosedCaptionSystem();
		return ClosedCaptionSystem.instance;
	}

	private List<Caption2D> messages2D = new ArrayList<Caption2D>();
	private List<Caption3D> messages3D = new ArrayList<Caption3D>();
	public Translations translationSystem = new Translations();
	private long tick2D = 0L;
	private long tick3D = 0L;
	private boolean enabled2D;
	private boolean enabled3D;
	private int mainColor = 0xC0100010;
	private int outlineColor = 0x505000FF;
	private int secondaryColor;

	private ClosedCaptionSystem() {
		secondaryColor = (outlineColor & 0xFEFEFE) >> 1 | outlineColor & 0xFF000000;
	}

	@SubscribeEvent
	public void eventEntity(PlaySoundAtEntityEvent event) {
		if (event == null || event.entity == null || event.name == null || event.name.isEmpty() || event.name.equalsIgnoreCase("none") || event.name.endsWith(":none"))
			return;
		if (Minecraft.getMinecraft().thePlayer == null)
			return;
		if (event.entity.getDistanceToEntity(Minecraft.getMinecraft().thePlayer) > 32)
			return;
		createCaption(event.name, event.entity, event.volume, event.pitch);
	}

	@SubscribeEvent
	public void eventISound(PlaySoundEvent17 event) {
		if (event == null || event.sound == null || event.name == null || event.name.isEmpty() || event.name.equals("none"))
			return;
		if (Minecraft.getMinecraft().thePlayer == null || event.category == null)
			return;
		switch (event.category) {
			case PLAYERS:
			case ANIMALS:
			case MOBS:
				break;
			case MASTER:
			case AMBIENT:
			case BLOCKS:
			case MUSIC:
			case RECORDS:
			case WEATHER:
			default:
				this.createCaption(event.name, event.sound);
				break;
		}
	}

	private void createCaption(String name, Entity entity, float volume, float pitch) {
		if (ClosedCaptionSystem.fr == null)
			ClosedCaptionSystem.fr = Minecraft.getMinecraft().fontRenderer;
		if (entity == null || entity.equals(Minecraft.getMinecraft().thePlayer)) {
			createCaption(name, volume, pitch);
			return;
		}
		Caption3D caption = new Caption3D(name, entity, volume, pitch);
		translationSystem.assignTranslation(caption);
		process(caption);
	}

	private void createCaption(String name, ISound sound) {
		if (ClosedCaptionSystem.fr == null)
			ClosedCaptionSystem.fr = Minecraft.getMinecraft().fontRenderer;
		if (sound.getXPosF() == 0 && sound.getYPosF() == 0 && sound.getZPosF() == 0) {
			createCaption(name, sound.getVolume(), sound.getPitch());
			return;
		}
		Caption3D caption = new Caption3D(name, sound, sound.getVolume(), sound.getPitch());
		translationSystem.assignTranslation(caption);
		process(caption);
	}

	private void createCaption(String name, float volume, float pitch) {
		if (ClosedCaptionSystem.fr == null)
			ClosedCaptionSystem.fr = Minecraft.getMinecraft().fontRenderer;
		if (ClosedCaptionSystem.btIsLoaded())
			if (name.contains("game.player"))
				return;
		synchronized (messages2D) {
			for (Caption2D caption : messages2D)
				if (caption.nameEquals(name)) {
					caption.resetTime();
					return;
				}
			Caption2D caption = new Caption2D(name, volume, pitch);
			translationSystem.assignTranslation(caption);
			if (!caption.isDisabled())
				messages2D.add(caption);
		}
	}

	private void process(Caption3D caption) {
		if (caption.is2D()) {
			createCaption(caption.key, caption.volume, caption.pitch);
			return;
		}
		List<Caption> removal = new ArrayList<Caption>();
		synchronized (messages3D) {
			for (Caption3D cap : messages3D)
				if (caption.isEntity()) {
					if (cap.isEntity() && caption.entity.equals(cap.entity))
						removal.add(cap);
				} else if (caption.isSound()) {
					if (caption.isWithin(cap, 0.1))
						removal.add(cap);
				} else if (caption.isWithin(cap, 0.1))
					removal.add(cap);
			messages3D.removeAll(removal);
			if (!caption.isDisabled())
				messages3D.add(caption);
			Collections.sort(messages3D, Caption3D.DISTANCE);
		}
	}

	@SubscribeEvent
	public void onTickInGame(RenderGameOverlayEvent.Post event) {
		if (event.type != ElementType.ALL || RenderManager.instance == null || RenderManager.instance.worldObj == null)
			return;
		synchronized (messages2D) {
			for (IMCMessage imc : FMLInterModComms.fetchRuntimeMessages(ClosedCaptions.instance))
				messages2D.add(new Caption2D(imc.getStringValue()));
			long tick = RenderManager.instance.worldObj.getTotalWorldTime();
			List<Caption> removalQueue = new ArrayList<Caption>();
			if (tick2D != tick) {
				for (Caption caption : messages2D)
					if (!caption.tick())
						removalQueue.add(caption);
				tick2D = tick;
			}
			messages2D.removeAll(removalQueue);
		}
		ScaledResolution res = event.resolution;
		GL11.glDisable(GL11.GL_LIGHTING);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GL11.glPushMatrix();
		GL11.glTranslated(res.getScaledWidth(), res.getScaledHeight() - 32, 100);
		drawCaptions2D(res, event.partialTicks);
		GL11.glTranslated(-res.getScaledWidth(), -(res.getScaledHeight() - 32), -100);
		GL11.glPopMatrix();
	}

	private void drawCaptions2D(ScaledResolution resolution, float deltaTime) {
		if (resolution == null)
			return;
		double template = Math.ceil(resolution.getScaledWidth_double() / 2) - 98;
		if (template > 180)
			template = 180;
		int w = 0;
		int size = 0;
		double mostPercent = 0;
		synchronized (messages2D) {
			for (Caption2D caption : messages2D) {
				if (!translationSystem.hasTranslation(caption))
					continue;
				int swidth = ClosedCaptionSystem.fr.getStringWidth(caption.message) + 2;
				if (swidth < template)
					swidth = (int) template;
				if (w < swidth)
					w = swidth;
				size++;
				double percent = caption.getPercentGuess(deltaTime);
				if (mostPercent < percent)
					mostPercent = percent;
			}
			if (size < 1)
				size = 1;
			int h = size * 10 - 2;
			int x = -w;
			int y = -h;
			double moveInTips = Math.pow(1 - mostPercent * 4, 4) * w;
			if (mostPercent < 0.25)
				GL11.glTranslated(moveInTips, 0, 0);
			drawTooltip(x, y, w, h, mainColor, outlineColor, secondaryColor);
			if (mostPercent < 0.25)
				GL11.glTranslated(-moveInTips, 0, 0);
			GL11.glTranslated(0, 0, 1);
			GL11.glEnable(GL11.GL_BLEND);
			for (Caption2D caption : messages2D) {
				if (!translationSystem.hasTranslation(caption))
					continue;
				double action = Math.pow(1 - caption.getPercentGuess(deltaTime), 8);
				int alpha = (int) ((1 - action) * 0xFF);
				if (alpha < 28)
					alpha = 28;
				double fadeMove = action * w;
				GL11.glTranslated(fadeMove, 0, 0);
				ClosedCaptionSystem.fr.drawStringWithShadow(caption.message, x + 1, y, alpha << 24 | 0xFFFFFF);
				GL11.glTranslated(-fadeMove, 0, 0);
				y += 10;
			}
		}
		GL11.glTranslated(0, 0, -1);
	}

	private void drawTooltip(int x, int y, int w, int h, int color1, int color2, int color3) {
		// Main
		drawGradientRect(x - 3, y - 3, w + 6, h + 6, color1, color1);
		// Top bar
		drawGradientRect(x - 3, y - 4, w + 6, 1, color1, color1);
		// Right Bar
		drawGradientRect(x + w + 3, y - 3, 1, h + 6, color1, color1);
		// Bottom Bar
		drawGradientRect(x - 3, y + h + 3, w + 6, 1, color1, color1);
		// Left Bar
		drawGradientRect(x - 4, y - 3, 1, h + 6, color1, color1);
		// Top Line
		drawGradientRect(x - 3, y - 3, w + 6, 1, color2, color2);
		// Right Line
		drawGradientRect(x + w + 2, y - 2, 1, h + 4, color2, color3);
		// Bottom Line
		drawGradientRect(x - 3, y + h + 2, w + 6, 1, color3, color3);
		// Left Line
		drawGradientRect(x - 3, y - 2, 1, h + 4, color2, color3);
	}

	private void drawGradientRect(int x, int y, int w, int h, int color1, int color2) {
		w += x;
		h += y;
		float alpha1 = (color1 >> 24 & 0xFF) / 255F;
		float red1 = (color1 >> 16 & 0xFF) / 255F;
		float green1 = (color1 >> 8 & 0xFF) / 255F;
		float blue1 = (color1 >> 0 & 0xFF) / 255F;
		float alpha2 = (color2 >> 24 & 0xFF) / 255F;
		float red2 = (color2 >> 16 & 0xFF) / 255F;
		float green2 = (color2 >> 8 & 0xFF) / 255F;
		float blue2 = (color2 >> 0 & 0xFF) / 255F;
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		GL11.glDisable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
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
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_TEXTURE_2D);
	}

	@SubscribeEvent
	public void render3D(RenderWorldLastEvent event) {
		if (RenderManager.instance == null || RenderManager.instance.worldObj == null)
			return;
		float deltaTime = event.partialTicks;
		long tick = RenderManager.instance.worldObj.getTotalWorldTime();
		List<Caption> removalQueue = new ArrayList<Caption>();
		synchronized (messages3D) {
			if (tick3D != tick) {
				for (Caption caption : messages3D)
					if (!caption.tick())
						removalQueue.add(caption);
				tick3D = tick;
			}
			messages3D.removeAll(removalQueue);
			GL11.glPushMatrix();
			GL11.glDisable(GL11.GL_LIGHTING);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			for (Caption3D caption : messages3D) {
				if (!translationSystem.hasTranslation(caption))
					continue;
				int distance = (int) (8 * caption.getScale());
				if (distance < 8)
					distance = 8;
				if (caption.getDistanceToEntity(Minecraft.getMinecraft().thePlayer) > distance)
					continue;
				double x = RenderManager.instance.viewerPosX - (caption.prevPosX + (caption.posX - caption.prevPosX) * deltaTime);
				double y = RenderManager.instance.viewerPosY - (caption.prevPosY + (caption.posY - caption.prevPosY) * deltaTime);
				if (caption.isEntity())
					y -= caption.entity.height + 0.5;
				else
					y -= 1;
				double z = RenderManager.instance.viewerPosZ - (caption.prevPosZ + (caption.posZ - caption.prevPosZ) * deltaTime);
				GL11.glTranslated(-x, -y, -z);
				GL11.glRotated(RenderManager.instance.playerViewY + 180, 0, -0.5, 0);
				GL11.glRotated(RenderManager.instance.playerViewX, -1, 0, 0);
				float scale = 0.02F * caption.getScale();
				GL11.glScalef(scale, -scale, scale);
				drawCaptions3D(caption, deltaTime);
				GL11.glScalef(1F / scale, -(1F / scale), 1F / scale);
				GL11.glRotated(RenderManager.instance.playerViewX, 1, 0, 0);
				GL11.glRotated(RenderManager.instance.playerViewY - 180, 0, 1, 0);
				GL11.glTranslated(x, y, z);
			}
		}
		GL11.glDisable(GL11.GL_BLEND);
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glEnable(GL11.GL_LIGHTING);
		GL11.glPopMatrix();
	}

	private void drawCaptions3D(Caption caption, float deltaTime) {
		if (messages3D.size() > 0) {
			int w = ClosedCaptionSystem.fr.getStringWidth(caption.message);
			int h = 8;
			int x = -w / 2;
			int y = -h;
			int alpha = (int) ((1 - Math.pow(1 - caption.getPercentGuess(deltaTime), 8)) * 0xD0);
			if (alpha < 28)
				alpha = 28;
			drawTooltip(x, y, w, h, alpha << 24 | mainColor & 0xFFFFFF, alpha << 24 | outlineColor & 0xFFFFFF, alpha << 24 | secondaryColor & 0xFFFFFF);
			GL11.glTranslated(0, 0, 1);
			ClosedCaptionSystem.fr.drawStringWithShadow(caption.message, x, y, alpha << 24 | 0xFFFFFF);
			GL11.glTranslated(0, 0, -1);
		}
	}
}
