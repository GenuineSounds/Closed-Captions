package com.genuineminecraft.closedcaptions.captions;

import java.util.Comparator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;

public class Caption3D extends Caption {

	public Entity entity;
	public ISound sound;
	public double posX, prevPosX;
	public double posY, prevPosY;
	public double posZ, prevPosZ;
	private float scale;
	public static Comparator<Caption3D> DISTANCE = new Comparator<Caption3D>() {

		@Override
		public int compare(Caption3D o1, Caption3D o2) {
			if (Minecraft.getMinecraft().thePlayer == null)
				return 0;
			double d1 = o1.getDistanceToEntity(Minecraft.getMinecraft().thePlayer);
			double d2 = o2.getDistanceToEntity(Minecraft.getMinecraft().thePlayer);
			if (d1 < d2)
				return 1;
			if (d1 > d2)
				return -1;
			return 0;
		}
	};

	public Caption3D(String message, Entity entity, float volume, float pitch) {
		super(message, volume, pitch);
		this.attach(entity);
	}

	public Caption3D(String message, ISound sound, float volume, float pitch) {
		super(message, volume, pitch);
		this.attach(sound);
	}

	public void attach(Entity entity) {
		if (entity == null)
			return;
		this.entity = entity;
		posX = entity.posX;
		posY = entity.posY;
		posZ = entity.posZ;
		prevPosX = entity.prevPosX;
		prevPosY = entity.prevPosY;
		prevPosZ = entity.prevPosZ;
	}

	public void attach(ISound sound) {
		if (sound == null)
			return;
		this.sound = sound;
		posX = sound.getXPosF();
		posY = sound.getYPosF();
		posZ = sound.getZPosF();
	}

	@Override
	public int compareTo(Caption o) {
		if (o instanceof Caption3D)
			return (int) (getDistanceToCaption((Caption3D) o) * 10000f);
		return key.compareTo(o.key);
	}

	public boolean equalTo(Caption3D caption) {
		if (isEntity() && caption.isEntity())
			return nameEquals(caption.key) && entity.equals(caption.entity);
		if (isSound() && caption.isSound())
			return nameEquals(caption.key) && sound.equals(caption.sound);
		return nameEquals(caption.key) && getDistanceToCaption(caption) <= 0.01;
	}

	public double getDistanceTo(double posX, double posZ) {
		double distanceX = this.posX - posX;
		double distanceZ = this.posZ - posZ;
		return Math.sqrt(distanceX * distanceX + distanceZ * distanceZ);
	}

	public double getDistanceTo(double posX, double posY, double posZ) {
		double distanceX = this.posX - posX;
		double distanceY = this.posY - posY;
		double distanceZ = this.posZ - posZ;
		return Math.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);
	}

	public double getDistanceToCaption(Caption3D caption) {
		return this.getDistanceTo(caption.posX, caption.posY, caption.posZ);
	}

	public double getDistanceToEntity(Entity entity) {
		return this.getDistanceTo(entity.posX, entity.posY, entity.posZ);
	}

	public float getScale() {
		if (isEntity()) {
			float out = entity.getShadowSize();
			if (out < 1)
				out = 1;
			return out;
		}
		return 1;
	}

	public boolean is2D() {
		return (entity instanceof EntityItem || entity instanceof EntityXPOrb || sound instanceof PositionedSoundRecord) && isWithin(Minecraft.getMinecraft().thePlayer, 4);
	}

	public boolean isEntity() {
		return entity != null;
	}

	public boolean isSound() {
		return sound != null;
	}

	public boolean isWithin(Caption3D caption, double distance) {
		return getDistanceToCaption(caption) <= distance;
	}

	public boolean isWithin(Entity entity, double distance) {
		return getDistanceToEntity(entity) <= distance;
	}

	@Override
	public boolean tick() {
		updatePos();
		return super.tick();
	}

	private void updatePos() {
		if (isEntity() && entity.isDead)
			entity = null;
		if (isEntity()) {
			prevPosX = entity.prevPosX;
			prevPosY = entity.prevPosY;
			prevPosZ = entity.prevPosZ;
			posX = entity.posX;
			posY = entity.posY;
			posZ = entity.posZ;
		} else if (isSound()) {
			prevPosX = posX;
			prevPosY = posY;
			prevPosZ = posZ;
			posX = sound.getXPosF();
			posY = sound.getYPosF();
			posZ = sound.getZPosF();
		} else {
			prevPosX = posX;
			prevPosY = posY;
			prevPosZ = posZ;
		}
	}
}
