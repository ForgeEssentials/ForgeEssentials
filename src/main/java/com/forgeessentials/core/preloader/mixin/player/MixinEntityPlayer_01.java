package com.forgeessentials.core.preloader.mixin.player;

import net.minecraft.entity.player.EntityPlayerMP;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EntityPlayerMP.class)
public abstract class MixinEntityPlayer_01
{
    
    @Overwrite
    public boolean canCommandSenderUseCommand(int p_70003_1_, String p_70003_2_)
    {
        return true;
    }
    
}
