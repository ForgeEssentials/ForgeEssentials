package com.forgeessentials.core.mixin.entity.player;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.protection.ModuleProtection;
import com.forgeessentials.util.PlayerInfo;

import net.minecraft.entity.player.PlayerEntity;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity
{

    /**
     * Custom permissions for command blocks
     * 
     * @author Maximuslotro
     * @reason stuff
     */
    @Inject(method = "canUseGameMasterBlocks", at = @At("HEAD"), cancellable = true)
    public void canUseGameMasterBlocks2(CallbackInfoReturnable<Boolean> cir)
    {
        cir.setReturnValue(((PlayerEntity) (Object) this).isCreative()
                && APIRegistry.perms.checkPermission((PlayerEntity) (Object) this, ModuleProtection.COMMANDBLOCK_PERM));
    }

    /**
     * Solve for noClip functionality
     * 
     * @author Maximuslotro
     * @reason stuff
     */
    @Redirect(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;isSpectator()Z"))
    public boolean onUpdate_NoClip(PlayerEntity _this)
    {
        return _this.isSpectator() || PlayerInfo.get(_this).isNoClip();
    }
}
