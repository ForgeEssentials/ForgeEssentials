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

@Mixin(PlayerEntity.class)
public abstract class MixinEntityPlayer
{

    @Shadow
    public GameProfile gameProfile;

    @Shadow
    public abstract boolean isSpectator();
    
    @Shadow
    public abstract boolean isCreative();

    /**
     * Custom permissions for command blocks
     * @author Maximuslotro
     * @reason stuff
     */
    @Overwrite
    public boolean canUseGameMasterBlocks()
    {
        return isCreative() && PermissionAPI.hasPermission((PlayerEntity) (Object) this, "mc.commandblock");
    }

    /**
     * Solve for noClip functionality
     * @author Maximuslotro
     * @reason stuff
     */
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "isSpectator()V"))
    public boolean onUpdate_NoClip()
    {
        return isSpectator() || PlayerInfo.get(gameProfile.getId()).isNoClip();
    }
}
