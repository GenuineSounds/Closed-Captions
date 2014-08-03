package com.genuineminecraft.closedcaptions.captions;

import java.util.Comparator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.Entity;

public class Caption3D extends Caption {

	public Entity entity;
	public ISound sound;
	public double posX, prevPosX;
	public double posY, prevPosY;
	public double posZ, prevPosZ;
	private float scale;

	public Caption3D(String message, Entity entity, float volume, float pitch) {
		super(message, volume, pitch);
		attach(entity);
	}

	public Caption3D(String message, ISound sound, float volume, float pitch) {
		super(message, volume, pitch);
		attach(sound);
	}

	public void attach(Entity entity) {
		if (entity == null)
			return;
		this.entity = entity;
		this.posX = entity.posX;
		this.posY = entity.posY;
		this.posZ = entity.posZ;
		this.prevPosX = entity.prevPosX;
		this.prevPosY = entity.prevPosY;
		this.prevPosZ = entity.prevPosZ;
	}

	public void attach(ISound sound) {
		if (sound == null)
			return;
		this.sound = sound;
		this.posX = sound.getXPosF();
		this.posY = sound.getYPosF();
		this.posZ = sound.getZPosF();
	}

	public boolean isEntity() {
		return entity != null;
	}

	public boolean isSound() {
		return sound != null;
	}

	public float getScale() {
		if (entity != null) {
			float out = entity.getShadowSize();
			if (out < 1)
				out = 1;
			return out;
		}
		return 1.0f;
	}

	public double getDistanceToCaption(Caption3D caption) {
		return getDistanceTo(caption.posX, caption.posY, caption.posZ);
	}

	public double getDistanceToEntity(Entity entity) {
		return getDistanceTo(entity.posX, entity.posY, entity.posZ);
	}

	public double getDistanceTo(double posX, double posY, double posZ) {
		double distanceX = this.posX - posX;
		double distanceY = this.posY - posY;
		double distanceZ = this.posZ - posZ;
		return Math.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);
	}

	public double getDistanceTo(double posX, double posZ) {
		double distanceX = this.posX - posX;
		double distanceZ = this.posZ - posZ;
		return Math.sqrt(distanceX * distanceX + distanceZ * distanceZ);
	}

	private void updatePos() {
		prevPosX = posX;
		prevPosY = posY;
		prevPosZ = posZ;
		if (entity != null) {
			posX = entity.posX;
			posY = entity.posY;
			posZ = entity.posZ;
		} else if (sound != null) {
			posX = sound.getXPosF();
			posY = sound.getYPosF();
			posZ = sound.getZPosF();
		}
	}

	public boolean equalTo(Caption3D caption) {
		if (this.isEntity() && caption.isEntity())
			return this.nameEquals(caption.name) && this.entity.equals(caption.entity);
		if (isSound() && caption.isSound())
			return this.nameEquals(caption.name) && this.sound.equals(caption.sound);
		return this.nameEquals(caption.name) && this.getDistanceToCaption(caption) <= 0.01;
	}

	public boolean isWithin(Caption3D caption, double distance) {
		return this.getDistanceToCaption(caption) <= distance;
	}

	public boolean isWithin(Entity entity, double distance) {
		return this.getDistanceToEntity(entity) <= distance;
	}

	@Override
	public int compareTo(Caption o) {
		if (o instanceof Caption3D)
			return (int) (getDistanceToCaption((Caption3D) o) * 10000f);
		return name.compareTo(o.name);
	}

	@Override
	public boolean tick() {
		updatePos();
		return super.tick();
	}

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
}
