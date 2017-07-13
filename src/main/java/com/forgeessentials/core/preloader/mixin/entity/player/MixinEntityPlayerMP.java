package com.forgeessentials.core.preloader.mixin.entity.player;

import net.minecraft.entity.player.EntityPlayerMP;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(EntityPlayerMP.class)
public class MixinEntityPlayerMP
{

    @Overwrite
    public boolean canUseCommand(int level, String command)
    {
        return true;
    }

}
