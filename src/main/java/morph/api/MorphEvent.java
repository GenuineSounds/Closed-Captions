package morph.api;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import cpw.mods.fml.common.eventhandler.Cancelable;

@Cancelable
public class MorphEvent extends PlayerEvent {

	//Can be null
	public final EntityLivingBase prevMorph;
	//Will never be null
	public final EntityLivingBase morph;

	public MorphEvent(final EntityPlayer player, final EntityLivingBase prevMorph, final EntityLivingBase morph) {
		super(player);
		this.prevMorph = prevMorph;
		this.morph = morph;
	}
}