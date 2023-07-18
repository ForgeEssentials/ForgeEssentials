package com.forgeessentials.core.mixin.network;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.forgeessentials.serverNetwork.ModuleNetworking;
import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.login.ServerLoginNetHandler;
import net.minecraft.util.text.StringTextComponent;

@Mixin(ServerLoginNetHandler.class)
public class MixinServerLoginNetHandler
{
    @Shadow
    public GameProfile gameProfile;

    @Inject(method = "tick",
            at = @At(value = "INVOKE",
            shift = At.Shift.AFTER,
            target =
            "Lnet/minecraft/server/management/PlayerList;getPlayer(Ljava/util/UUID;)Lnet/minecraft/entity/player/ServerPlayerEntity;"),
            locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void getPlayer(CallbackInfo ci, ServerPlayerEntity serverplayerentity, ServerLoginNetHandler _this)
    {
        if(serverplayerentity==null) {
            if(ModuleNetworking.getInstance().getTranferManager().getOnlineplayers().contains(gameProfile.getId())){
                _this.disconnect(new StringTextComponent("You"));
                ci.cancel();
            }
        }
    }
}
