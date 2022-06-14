package com.forgeessentials.core.preloader.mixin.entity.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.world.World;
import net.minecraftforge.server.permission.PermissionAPI;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.forgeessentials.util.PlayerInfo;

@Mixin(PlayerEntity.class)
public abstract class MixinEntityPlayer extends Entity
{
    @Shadow
    public PlayerInteractionManager capabilities;

    public MixinEntityPlayer(EntityType<?>PlayerEntity,World p_i48580_2_)
    {
        super(PlayerEntity, p_i48580_2_);
    }

    @Shadow public abstract boolean isSpectator();

    @Overwrite
    public boolean canUseCommandBlock()
    {
        return this.capabilities.isCreative() && PermissionAPI.hasPermission((PlayerEntity)(Object)this, "mc.commandblock");
    }
    
    @Redirect(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSpectator()Z"))
    public boolean onUpdate_NoClip(PlayerEntity _this) {
        return isSpectator() || PlayerInfo.get(_this).isNoClip();
    }
}
