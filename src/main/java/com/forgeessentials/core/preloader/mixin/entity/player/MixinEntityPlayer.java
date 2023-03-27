package com.forgeessentials.core.preloader.mixin.entity.player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.world.GameType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.server.permission.PermissionAPI;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.forgeessentials.util.PlayerInfo;
import com.mojang.authlib.GameProfile;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinEntityPlayer extends ServerPlayerEntity
{


    public MixinEntityPlayer(MinecraftServer p_i45285_1_, ServerWorld p_i45285_2_, GameProfile p_i45285_3_, PlayerInteractionManager p_i45285_4_)
    {
        super(p_i45285_1_, p_i45285_2_, p_i45285_3_, p_i45285_4_);
        // TODO Auto-generated constructor stub
    }

    @Shadow
    public PlayerInteractionManager gameMode;

    @Shadow
    public abstract boolean isSpectator();

    @Overwrite
    public boolean canUseGameMasterBlocks()
    {
        return gameMode.getGameModeForPlayer() == GameType.SPECTATOR && PermissionAPI.hasPermission((PlayerEntity) (Object) this, "mc.commandblock");
    }

    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/ServerPlayerEntity;isSpectator()Z"))
    public boolean onUpdate_NoClip()
    {
        return isSpectator() || PlayerInfo.get(this.getUUID()).isNoClip();
    }
}
