package com.genuineflix.caption.caption;

import java.util.Comparator;

import javax.vecmath.Vector3d;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.entity.Entity;

public class CaptionWorld extends Caption {

	public Entity entity;
	public ISound sound;
	public double posX, prevPosX;
	public double posY, prevPosY;
	public double posZ, prevPosZ;
	public double size = 1;
	public static Comparator<CaptionWorld> DISTANCE = new Comparator<CaptionWorld>() {

		@Override
		public int compare(final CaptionWorld o1, final CaptionWorld o2) {
			if (Minecraft.getMinecraft().thePlayer == null)
				return 0;
			final double d1 = o1.getDistanceTo(Minecraft.getMinecraft().thePlayer);
			final double d2 = o2.getDistanceTo(Minecraft.getMinecraft().thePlayer);
			if (d1 < d2)
				return 1;
			if (d1 > d2)
				return -1;
			return 0;
		}
	};

	public CaptionWorld(final String message, final Entity entity, final float volume, final float pitch) {
		super(message, volume, pitch);
		this.attach(entity);
	}

	public CaptionWorld(final String message, final ISound sound, final float volume, final float pitch) {
		super(message, volume, pitch);
		this.attach(sound);
	}

	public void attach(final Entity entity) {
		if (this.entity != null || entity == null)
			return;
		this.entity = entity;
		posX = entity.posX;
		posY = entity.posY;
		posZ = entity.posZ;
		prevPosX = entity.prevPosX;
		prevPosY = entity.prevPosY;
		prevPosZ = entity.prevPosZ;
		size = entity.height + 0.5;
	}

	public void attach(final ISound sound) {
		if (sound == null)
			return;
		this.sound = sound;
		posX = sound.getXPosF();
		posY = sound.getYPosF();
		posZ = sound.getZPosF();
	}

	@Override
	public int compareTo(final Caption o) {
		if (o instanceof CaptionWorld)
			return (int) (getDistanceTo((CaptionWorld) o) * 10000f);
		return key.compareTo(o.key);
	}

	public boolean equalTo(final CaptionWorld caption) {
		if (!nameEquals(caption.key))
			return false;
		return getDistanceTo(caption) <= 0.1;
	}

	public double getDistanceTo(final CaptionWorld caption) {
		return getDistanceTo(caption.posX, caption.posY, caption.posZ);
	}

	public double getDistanceTo(final double posX, final double posY, final double posZ) {
		final double distanceX = this.posX - posX;
		final double distanceY = this.posY - posY;
		final double distanceZ = this.posZ - posZ;
		return Math.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);
	}

	public double getDistanceTo(final Entity entity) {
		return getDistanceTo(entity.posX, entity.posY, entity.posZ);
	}

	public double getDistanceTo(final Vector3d entity) {
		return getDistanceTo(entity.x, entity.y, entity.z);
	}

	public double getDistanceToPlayer() {
		return getDistanceTo(Minecraft.getMinecraft().thePlayer);
	}

	public float getScale() {
		if (isEntity()) {
			float out = (float) (size / 2);
			if (out < 1)
				out = 1;
			return out;
		}
		return 1;
	}

	public boolean isEntity() {
		return entity != null;
	}

	public boolean isSound() {
		return sound != null;
	}

	public boolean isWithin(final CaptionWorld caption, final double distance) {
		return entity != null && getDistanceTo(caption) <= distance;
	}

	public boolean isWithin(final Entity entity, final double distance) {
		return entity != null && getDistanceTo(entity) <= distance;
	}

	public boolean isWithinPlayer(final double distance) {
		return Minecraft.getMinecraft().thePlayer != null || getDistanceToPlayer() <= distance;
	}

	@Override
	public boolean tick() {
		if (isEntity() && entity.isDead)
			entity = null;
		updatePos();
		return super.tick();
	}

	private void updatePos() {
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
