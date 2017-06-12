package com.forgeessentials.core.preloader.mixin.entity.player;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraftforge.server.permission.PermissionAPI;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer
{
    @Shadow
    public PlayerCapabilities capabilities;

    @Overwrite
    public boolean canUseCommandBlock()
    {
        return this.capabilities.isCreativeMode && PermissionAPI.hasPermission((EntityPlayer)(Object)this, "mc.commandblock");
    }
}
