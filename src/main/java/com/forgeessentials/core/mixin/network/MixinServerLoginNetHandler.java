package com.forgeessentials.core.mixin.network;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import com.forgeessentials.core.moduleLauncher.ModuleLauncher;
import com.forgeessentials.serverNetwork.ModuleNetworking;
import com.forgeessentials.serverNetwork.utils.ServerType;
import com.mojang.authlib.GameProfile;

import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.ChatFormatting;

@Mixin(ServerLoginPacketListenerImpl.class)
public class MixinServerLoginNetHandler
{
    @Shadow
    public void disconnect(Component p_194026_1_) {}
    
    @Shadow
    private GameProfile gameProfile;

    @Inject(method = "handleAcceptedLogin()V",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/server/players/PlayerList;canPlayerLogin(Ljava/net/SocketAddress;Lcom/mojang/authlib/GameProfile;)Lnet/minecraft/network/chat/Component;",
                    shift = At.Shift.BEFORE),
            locals = LocalCapture.CAPTURE_FAILHARD, 
            cancellable = true)
    public void handlePlayerjoin(CallbackInfo ci)
    {
        if(!ModuleLauncher.getModuleList().contains(ModuleNetworking.networkModule)) {
            return;
        }
        //Fix for joining disconnected client server
        if(ModuleNetworking.getInstance().getServerType()==ServerType.CLIENTSERVER&&
                !ModuleNetworking.getInstance().getClient().isChannelOpen()&&
                ModuleNetworking.getLocalClient().isDisableConnectionsIfServerNotFound()){
            disconnect((new TextComponent("Disconnected ServerHead")).withStyle(ChatFormatting.RED));
            ci.cancel();
            return;
        }
        //Fix for joining client servers without joining root server if option enabled
        if(ModuleNetworking.getInstance().getServerType()==ServerType.CLIENTSERVER&&
                ModuleNetworking.getLocalClient().isDisableClientOnlyConnections()){
            if(!ModuleNetworking.getInstance().getTranferManager().incommongPlayers.contains(gameProfile.getId())) {
                disconnect((new TextComponent("Must join from root server")).withStyle(ChatFormatting.RED));
                ci.cancel();
                return;
            }
            ModuleNetworking.getInstance().getTranferManager().incommongPlayers.remove(gameProfile.getId());
            return;
        }
        //Fix for double logging on server network
        if(ModuleNetworking.getInstance().getTranferManager().onlinePlayers.contains(gameProfile.getId())){
            disconnect((new TextComponent("Double Login")).withStyle(ChatFormatting.RED));
            ci.cancel();
        }
    }
}
