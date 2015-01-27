package morph.api;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import cpw.mods.fml.common.eventhandler.Cancelable;

@Cancelable
public class MorphAcquiredEvent extends PlayerEvent {

	public final EntityLivingBase acquiredMorph;

	public MorphAcquiredEvent(final EntityPlayer player, final EntityLivingBase acquired) {
		super(player);
		acquiredMorph = acquired;
	}
}
