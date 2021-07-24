package com.forgeessentials.client.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends Entity
{
    public MixinEntityPlayer(World p_i1582_1_)
    {
        super(p_i1582_1_);
    }

    @Shadow public abstract boolean isSpectator();

    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;isSpectator()Z"))
    private boolean onUpdate_NoClip(EntityPlayer _this) {
        return isSpectator() || noClip;
    }
}
