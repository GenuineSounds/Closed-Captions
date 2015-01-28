package com.genuineflix.cc.caption;

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
	public double size = 1;
	private float scale;
	public static Comparator<Caption3D> DISTANCE = new Comparator<Caption3D>() {

		@Override
		public int compare(final Caption3D o1, final Caption3D o2) {
			if (Minecraft.getMinecraft().thePlayer == null)
				return 0;
			final double d1 = o1.getDistanceToEntity(Minecraft.getMinecraft().thePlayer);
			final double d2 = o2.getDistanceToEntity(Minecraft.getMinecraft().thePlayer);
			if (d1 < d2)
				return 1;
			if (d1 > d2)
				return -1;
			return 0;
		}
	};

	public Caption3D(final String message, final Entity entity, final float volume, final float pitch) {
		super(message, volume, pitch);
		this.attach(entity);
	}

	public Caption3D(final String message, final ISound sound, final float volume, final float pitch) {
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
		if (o instanceof Caption3D)
			return (int) (getDistanceToCaption((Caption3D) o) * 10000f);
		return key.compareTo(o.key);
	}

	public boolean equalTo(final Caption3D caption) {
		if (!nameEquals(caption.key))
			return false;
		return getDistanceToCaption(caption) <= 0.1;
	}

	public double getDistanceIgnoringHeight(final double posX, final double posZ) {
		final double distanceX = this.posX - posX;
		final double distanceZ = this.posZ - posZ;
		return Math.sqrt(distanceX * distanceX + distanceZ * distanceZ);
	}

	public double getDistanceTo(final double posX, final double posY, final double posZ) {
		final double distanceX = this.posX - posX;
		final double distanceY = this.posY - posY;
		final double distanceZ = this.posZ - posZ;
		return Math.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);
	}

	public double getDistanceToCaption(final Caption3D caption) {
		return getDistanceTo(caption.posX, caption.posY, caption.posZ);
	}

	public double getDistanceToEntity(final Entity entity) {
		return getDistanceTo(entity.posX, entity.posY, entity.posZ);
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

	public boolean is2D() {
		return (entity instanceof EntityItem || entity instanceof EntityXPOrb || sound instanceof PositionedSoundRecord) && isWithin(Minecraft.getMinecraft().thePlayer, 4);
	}

	public boolean isEntity() {
		return entity != null;
	}

	public boolean isSound() {
		return sound != null;
	}

	public boolean isWithin(final Caption3D caption, final double distance) {
		return getDistanceToCaption(caption) <= distance;
	}

	public boolean isWithin(final Entity entity, final double distance) {
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
