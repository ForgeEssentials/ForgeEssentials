package com.forgeessentials.core.mixin.entity.player;

import net.minecraft.entity.player.PlayerEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.At;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.util.PlayerInfo;

@Mixin(PlayerEntity.class)
public abstract class MixinEntityPlayer
{

    /**
     * Custom permissions for command blocks
     * @author Maximuslotro
     * @reason stuff
     */
    @Overwrite
    public boolean canUseGameMasterBlocks()
    {
        return ((PlayerEntity) (Object) this).isCreative() && APIRegistry.perms.checkPermission((PlayerEntity) (Object) this, "mc.commandblock");
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
        return _this.isSpectator() || PlayerInfo.get(_this).isNoClip();
    }
}
