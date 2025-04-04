package com.forgeessentials.core.preloader.mixin.entity.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.world.World;
import net.minecraftforge.server.permission.PermissionAPI;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.forgeessentials.util.PlayerInfo;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends Entity
{
    @Shadow
    public PlayerCapabilities capabilities;

    public MixinEntityPlayer(World p_i1582_1_)
    {
        super(p_i1582_1_);
    }

    @Shadow public abstract boolean isSpectator();

    @Overwrite
    public boolean canUseCommandBlock()
    {
        return this.capabilities.isCreativeMode && ( this.world.isRemote || PermissionAPI.hasPermission((EntityPlayer)(Object)this, "mc.commandblock") );
    }
    
    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;isSpectator()Z"))
    public boolean onUpdate_NoClip(EntityPlayer _this) {
        return isSpectator() || (!_this.world.isRemote && PlayerInfo.get(_this).isNoClip());
    }
}
