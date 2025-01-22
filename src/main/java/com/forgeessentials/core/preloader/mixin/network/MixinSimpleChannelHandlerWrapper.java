package com.forgeessentials.core.preloader.mixin.network;

import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.forgeessentials.auth.ModuleAuth;
import com.forgeessentials.core.preloader.asminjector.annotation.Shadow;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.output.LoggingHandler;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import cpw.mods.fml.common.network.simpleimpl.SimpleChannelHandlerWrapper;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.Side;
import io.netty.channel.SimpleChannelInboundHandler;

@Mixin(SimpleChannelHandlerWrapper.class)
public abstract class MixinSimpleChannelHandlerWrapper<REQ extends IMessage, REPLY extends IMessage> extends SimpleChannelInboundHandler<REQ>
{
    @Shadow
    @Final
    private IMessageHandler<? super REQ, ? extends REPLY> messageHandler;

    @Redirect(method = "channelRead0(Lio/netty/channel/ChannelHandlerContext;Lcpw/mods/fml/common/network/simpleimpl/IMessage;)V", at = @At(value = "INVOKE", target = "Lcpw/mods/fml/common/network/simpleimpl/IMessageHandler;onMessage(Lcpw/mods/fml/common/network/simpleimpl/IMessage;Lcpw/mods/fml/common/network/simpleimpl/MessageContext;)Lcpw/mods/fml/common/network/simpleimpl/IMessage;", remap = false), remap = false)
    private REPLY redirectNetworkHandler(IMessageHandler<?, ?> iMessageHandler, REQ message, MessageContext ctx) {

        EntityPlayer player = ctx.netHandler instanceof NetHandlerPlayServer ? ctx.getServerHandler().playerEntity : null;
        if (player == null && FMLLaunchHandler.side() != Side.CLIENT) {
            LoggingHandler.felog.fatal("This should never happen! nethandler is instance of client but side is Server!");
        }
        if (ctx.side == Side.CLIENT || !ModuleAuth.isEnabled() || player == null || ModuleAuth.isAuthenticated(player) || ModuleAuth.isAllowedMethod(message)) {
            return messageHandler.onMessage(message, ctx);
        }
        else {
            LoggingHandler.felog.debug("Message '{}' from user '{}' ignored because player is not authenticated!", DataManager.getGson().toJson(message), player.getDisplayName());
            return null;
        }
    }
}
