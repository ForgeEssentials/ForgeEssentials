package com.forgeessentials.core.mixin.network;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.serverNetwork.ModuleNetworking;

import net.minecraft.network.ServerStatusResponse;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

@Mixin(MinecraftServer.class)
public class MixinMinecraftServer
{

    @ModifyArg(method = "tickServer(Ljava/util/function/BooleanSupplier;)V", 
            at = @At(value = "INVOKE", 
            target = "Lnet/minecraft/network/ServerStatusResponse;setPlayers(Lnet/minecraft/network/ServerStatusResponse$Players;)V"), 
            index = 0)
    public ServerStatusResponse.Players injected(ServerStatusResponse.Players p) {
        if(!ModuleLauncher.getModuleList().contains(ModuleNetworking.networkModule)) {
            return p;
        }
        return new ServerStatusResponse.Players(ServerLifecycleHooks.getCurrentServer().getMaxPlayers(),ModuleNetworking.getInstance().getTranferManager().onlinePlayers.size());
    }
}
