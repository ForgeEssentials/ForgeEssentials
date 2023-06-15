package com.forgeessentials.core.mixin.entity.player;

import net.minecraft.entity.player.PlayerEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At;

import com.forgeessentials.api.APIRegistry;
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
        return isCreative() && APIRegistry.perms.checkPermission((PlayerEntity) (Object) this, "mc.commandblock");
    }

    /**
     * Solve for noClip functionality
     * @author Maximuslotro
     * @reason stuff
     */
    @Redirect(method = "tick",
            at = @At(value = "INVOKE", 
            target = "Lnet/minecraft/entity/player/PlayerEntity;isSpectator()Z")
    )
    public boolean onUpdate_NoClip(PlayerEntity _this) {
        return isSpectator() || PlayerInfo.get(_this).isNoClip();
    }
}
