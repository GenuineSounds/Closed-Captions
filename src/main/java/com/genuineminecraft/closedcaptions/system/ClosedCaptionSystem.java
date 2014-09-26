package com.genuineminecraft.closedcaptions.system;

import static org.lwjgl.opengl.GL11.GL_ALPHA_TEST;
import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_FLAT;
import static org.lwjgl.opengl.GL11.GL_LIGHTING;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SMOOTH;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glDisable;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glScalef;
import static org.lwjgl.opengl.GL11.glShadeModel;
import static org.lwjgl.opengl.GL11.glTranslated;
import static org.lwjgl.opengl.GL11.glVertex2i;

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

	public static ClosedCaptionSystem getInstance() {
		if (instance == null)
			instance = new ClosedCaptionSystem();
		return instance;
	}

	public static void createDirectMessage(String message) {
		synchronized (getInstance().messages2D) {
			getInstance().messages2D.add(new Caption2D(message));
		}
	}

	public static boolean btIsLoaded() {
		return Loader.isModLoaded(BT_MOD_NAME);
	}

	private List<Caption2D> messages2D = Collections.synchronizedList(new ArrayList<Caption2D>());
	private List<Caption3D> messages3D = Collections.synchronizedList(new ArrayList<Caption3D>());
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
	public void onTickInGame(RenderGameOverlayEvent.Post event) {
		if (event.type == ElementType.ALL)
			ClosedCaptionSystem.getInstance().render2D(event.resolution, event.partialTicks);
	}

	@SubscribeEvent
	public void render3D(RenderWorldLastEvent event) {
		ClosedCaptionSystem.getInstance().render3D(event.partialTicks);
	}

	@SubscribeEvent
	public void eventEntity(PlaySoundAtEntityEvent event) {
		if (event == null || event.entity == null || event.name == null || event.name.isEmpty() || event.name.equals("none"))
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
				createCaption(event.name, event.sound);
				break;
		}
	}

	public void createCaption(String name, float volume, float pitch) {
		if (btIsLoaded()) {
			if (name.contains("game.player"))
				return;
		}
		synchronized (messages2D) {
			for (Caption2D caption : messages2D) {
				if (caption.nameEquals(name)) {
					caption.resetTime();
					return;
				}
			}
			Caption2D caption = new Caption2D(name, volume, pitch);
			translationSystem.assignTranslation(caption);
			if (!caption.isDisabled())
				messages2D.add(caption);
		}
	}

	public void createCaption(String name, Entity entity, float volume, float pitch) {
		if (entity == null || entity.equals(Minecraft.getMinecraft().thePlayer)) {
			createCaption(name, volume, pitch);
			return;
		}
		Caption3D caption = new Caption3D(name, entity, volume, pitch);
		translationSystem.assignTranslation(caption);
		process(caption);
	}

	public void createCaption(String name, ISound sound) {
		if (sound.getXPosF() == 0 && sound.getYPosF() == 0 && sound.getZPosF() == 0) {
			createCaption(name, sound.getVolume(), sound.getPitch());
			return;
		}
		Caption3D caption = new Caption3D(name, sound, sound.getVolume(), sound.getPitch());
		translationSystem.assignTranslation(caption);
		process(caption);
	}

	private void process(Caption3D caption) {
		if (caption.is2D()) {
			createCaption(caption.key, caption.volume, caption.pitch);
			return;
		}
		List<Caption> removal = new ArrayList<Caption>();
		synchronized (messages3D) {
			for (Caption3D cap : messages3D) {
				if (caption.isEntity()) {
					if (cap.isEntity() && caption.entity.equals(cap.entity))
						removal.add(cap);
				} else if (caption.isSound()) {
					if (caption.isWithin(cap, 0.1))
						removal.add(cap);
				} else if (caption.isWithin(cap, 0.1))
					removal.add(cap);
			}
			messages3D.removeAll(removal);
			if (!caption.isDisabled())
				messages3D.add(caption);
			Collections.sort(messages3D, Caption3D.DISTANCE);
		}
	}

	public void render2D(ScaledResolution resolution, float deltaTime) {
		if (RenderManager.instance == null || RenderManager.instance.worldObj == null)
			return;
		synchronized (messages2D) {
			for (IMCMessage imc : FMLInterModComms.fetchRuntimeMessages(ClosedCaptions.instance))
				messages2D.add(new Caption2D(imc.getStringValue()));
			long tick = RenderManager.instance.worldObj.getTotalWorldTime();
			List<Caption> removalQueue = new ArrayList<Caption>();
			if (this.tick2D != tick) {
				for (Caption caption : messages2D)
					if (!caption.tick())
						removalQueue.add(caption);
				this.tick2D = tick;
			}
			messages2D.removeAll(removalQueue);
		}
		glDisable(GL_LIGHTING);
		glEnable(GL_ALPHA_TEST);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glPushMatrix();
		glTranslated(resolution.getScaledWidth(), resolution.getScaledHeight() - 32, 100);
		drawCaptions2D(resolution, deltaTime);
		glTranslated(-(resolution.getScaledWidth()), -resolution.getScaledHeight() - 32, -100);
		glPopMatrix();
	}

	private void drawCaptions2D(ScaledResolution resolution, float deltaTime) {
		if (resolution == null)
			return;
		double template = Math.ceil(resolution.getScaledWidth_double() / 2) - 98;
		if (template > 180)
			template = 180;
		FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
		int w = 0;
		int size = 0;
		double mostPercent = 0;
		synchronized (messages2D) {
			for (Caption2D caption : messages2D) {
				if (!translationSystem.hasTranslation(caption))
					continue;
				int swidth = fr.getStringWidth(caption.message) + 2;
				if (swidth < template)
					swidth = (int) template;
				if (w < swidth)
					w = swidth;
				size++;
				double percent = caption.getPercentGuess(deltaTime);
				if (mostPercent < percent)
					mostPercent = percent;
			}
		}
		if (size < 1) {
			size = 1;
		}
		int h = size * 10 - 2;
		int x = -w;
		int y = -h;
		double moveInTips = Math.pow(1 - mostPercent * 4, 4) * w;
		if (mostPercent < 0.25)
			glTranslated(moveInTips, 0, 0);
		drawTooltip(x, y, w, h, mainColor, outlineColor, secondaryColor);
		if (mostPercent < 0.25)
			glTranslated(-moveInTips, 0, 0);
		glTranslated(0, 0, 1);
		glEnable(GL_BLEND);
		synchronized (messages2D) {
			for (Caption2D caption : messages2D) {
				if (!translationSystem.hasTranslation(caption))
					continue;
				double action = Math.pow(1 - caption.getPercentGuess(deltaTime), 8);
				int alpha = (int) ((1 - action) * 0xFF);
				if (alpha < 28)
					alpha = 28;
				double fadeMove = action * w;
				glTranslated(fadeMove, 0, 0);
				fr.drawStringWithShadow(caption.message, x + 1, y, alpha << 24 | 0xFFFFFF);
				glTranslated(-fadeMove, 0, 0);
				y += 10;
			}
		}
		glTranslated(0, 0, -1);
	}

	public void render3D(float deltaTime) {
		if (RenderManager.instance == null || RenderManager.instance.worldObj == null)
			return;
		synchronized (messages3D) {
			long tick = RenderManager.instance.worldObj.getTotalWorldTime();
			List<Caption> removalQueue = new ArrayList<Caption>();
			if (this.tick3D != tick) {
				for (Caption caption : messages3D)
					if (!caption.tick())
						removalQueue.add(caption);
				this.tick3D = tick;
			}
			messages3D.removeAll(removalQueue);
		}
		glPushMatrix();
		glDisable(GL_LIGHTING);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		synchronized (messages3D) {
			for (Caption3D caption : messages3D) {
				if (!translationSystem.hasTranslation(caption))
					continue;
				int distance = (int) (16 * caption.getScale());
				if (distance < 16)
					distance = 16;
				if (caption.isEntity() && caption.getDistanceToEntity(Minecraft.getMinecraft().thePlayer) > distance)
					continue;
				double x = RenderManager.instance.viewerPosX - (caption.prevPosX + ((caption.posX - caption.prevPosX) * deltaTime));
				double y = RenderManager.instance.viewerPosY - (caption.prevPosY + ((caption.posY - caption.prevPosY) * deltaTime));
				if (caption.isEntity())
					y -= caption.entity.height + 0.5;
				else
					y -= 1;
				double z = RenderManager.instance.viewerPosZ - (caption.prevPosZ + ((caption.posZ - caption.prevPosZ) * deltaTime));
				glTranslated(-x, -y, -z);
				glRotatef(-RenderManager.instance.playerViewY + 180, 0.0F, 1.0F, 0.0F);
				glRotatef(-RenderManager.instance.playerViewX, 1.0F, 0.0F, 0.0F);
				float scale = 0.02F * caption.getScale();
				glScalef(scale, -scale, scale);
				drawCaptions3D(caption, deltaTime);
				glScalef(1F / scale, -(1F / scale), 1F / scale);
				glRotatef(RenderManager.instance.playerViewX, 1.0F, 0.0F, 0.0F);
				glRotatef(RenderManager.instance.playerViewY - 180, 0.0F, 1.0F, 0.0F);
				glTranslated(x, y, z);
			}
		}
		glDisable(GL_BLEND);
		glEnable(GL_LIGHTING);
		glPopMatrix();
	}

	private void drawCaptions3D(Caption caption, float deltaTime) {
		FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
		if (messages3D.size() > 0) {
			int w = fr.getStringWidth(caption.message);
			int h = 8;
			int x = -w / 2;
			int y = -h;
			int alpha = (int) (((1 - Math.pow(1 - caption.getPercentGuess(deltaTime), 8)) * 0xD0));
			if (alpha < 28)
				alpha = 28;
			drawTooltip(x, y, w, h, alpha << 24 | (mainColor & 0xFFFFFF), alpha << 24 | (outlineColor & 0xFFFFFF), alpha << 24 | (secondaryColor & 0xFFFFFF));
			glTranslated(0, 0, 1);
			fr.drawStringWithShadow(caption.message, x, y, alpha << 24 | 0xFFFFFF);
			glTranslated(0, 0, -1);
		}
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
		float alpha1 = (float) ((color1 >> 24) & 0xFF) / 255F;
		float red1 = (float) ((color1 >> 16) & 0xFF) / 255F;
		float green1 = (float) ((color1 >> 8) & 0xFF) / 255F;
		float blue1 = (float) ((color1 >> 0) & 0xFF) / 255F;
		float alpha2 = (float) ((color2 >> 24) & 0xFF) / 255F;
		float red2 = (float) ((color2 >> 16) & 0xFF) / 255F;
		float green2 = (float) ((color2 >> 8) & 0xFF) / 255F;
		float blue2 = (float) ((color2 >> 0) & 0xFF) / 255F;
		glDisable(GL_TEXTURE_2D);
		glDisable(GL_ALPHA_TEST);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glShadeModel(GL_SMOOTH);
		glBegin(GL_QUADS);
		glColor4f(red1, green1, blue1, alpha1);
		glVertex2i(w, y);
		glVertex2i(x, y);
		glColor4f(red2, green2, blue2, alpha2);
		glVertex2i(x, h);
		glVertex2i(w, h);
		glColor4f(1, 1, 1, 1);
		glEnd();
		glShadeModel(GL_FLAT);
		glDisable(GL_BLEND);
		glEnable(GL_ALPHA_TEST);
		glEnable(GL_TEXTURE_2D);
	}
}
