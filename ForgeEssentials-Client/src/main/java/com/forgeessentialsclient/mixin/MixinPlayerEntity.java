package com.forgeessentialsclient.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends Entity
{
    public MixinPlayerEntity(EntityType<?>PlayerEntity,World p_i48580_2_)
    {
        super(PlayerEntity, p_i48580_2_);
    }
/*
    @Shadow public abstract boolean isSpectator();
    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSpectator()Z"))
    private boolean onUpdate_NoClip(PlayerEntity _this) {
        return isSpectator() || noPhysics;
    }*/
}
