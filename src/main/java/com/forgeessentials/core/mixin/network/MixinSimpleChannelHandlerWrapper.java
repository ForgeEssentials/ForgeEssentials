package com.forgeessentials.core.mixin.network;
//
//import net.minecraft.entity.player.ServerPlayerEntity;
//import net.minecraft.network.INetHandler;
//import net.minecraft.network.IPacket;
//import net.minecraft.network.NetworkManager;
//import net.minecraft.network.ThreadQuickExitException;
//import net.minecraft.network.play.ServerPlayNetHandler;
//import net.minecraftforge.fml.server.ServerLifecycleHooks;
//
//import org.spongepowered.asm.mixin.Final;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//
//import com.forgeessentials.auth.ModuleAuth;
//import com.forgeessentials.data.v2.DataManager;
//import com.forgeessentials.util.output.LoggingHandler;
//
//import io.netty.channel.Channel;
//import io.netty.channel.ChannelHandlerContext;
//import io.netty.channel.SimpleChannelInboundHandler;
//
//@Mixin(NetworkManager.class)
//public abstract class MixinSimpleChannelHandlerWrapper extends SimpleChannelInboundHandler<IPacket<?>>
//{
//    @Shadow
//    @Final
//    private INetHandler packetListener;
//    @Shadow
//    @Final
//    private Channel channel;
//    @Shadow
//    @Final
//    private int receivedPackets;
//    @Shadow
//    protected <T extends INetHandler> void genericsFtw(IPacket<T> p_197664_0_, INetHandler p_197664_1_) {}
//
//    @Inject(at = @At("HEAD"), 
//            method = "channelRead0", 
//            cancellable = true)
//    //@Overwrite//(method = "channelRead0", at = @At("HEAD"), cancellable = true)
//    //private void onChannelRead(ChannelHandlerContext context, IPacket<?> packet, CallbackInfo callbackInfo)
//    protected void channelRead0(ChannelHandlerContext context, IPacket<?> packet, CallbackInfo callbackInfo) throws Exception
//    {
//        if (this.channel.isOpen()) {
//            try {
//                ServerPlayerEntity player = packetListener instanceof ServerPlayNetHandler ? ((ServerPlayNetHandler)packetListener).player : null;
//                if (ServerLifecycleHooks.getCurrentServer().isSingleplayer()||!ModuleAuth.isEnabled() || ModuleAuth.isAuthenticated(player) || ModuleAuth.isAllowedMethod(packet))
//                {
//                    genericsFtw(packet, packetListener);
//                }
//                else
//                {
//                    LoggingHandler.felog.debug("Message '{}' from user '{}' ignored because player is not authenticated!", DataManager.getGson().toJson(packet),
//                            player.getDisplayName());
//                    callbackInfo.cancel();
//                }
//            } catch (ThreadQuickExitException threadquickexitexception) {
//            }
//
//            ++receivedPackets;
//            callbackInfo.cancel();
//         }
//    }
//
//  /*  @SuppressWarnings("unchecked")
//    @Inject(method = "genericsFtw", at = @At("HEAD"), cancellable = true)
//    private static <T extends INetHandler> void genericsFtw(IPacket<T> p_197664_0_, INetHandler p_197664_1_, CallbackInfo callbackInfo)
//    {
//        ServerPlayerEntity player = p_197664_1_ instanceof ServerPlayNetHandler ? ((ServerPlayNetHandler)p_197664_1_).player : null;
//        if (!ModuleAuth.isEnabled() || ModuleAuth.isAuthenticated(player) || ModuleAuth.isAllowedMethod(p_197664_0_))
//        {
//            p_197664_0_.handle((T)p_197664_1_);
//        }
//        else
//        {
//            LoggingHandler.felog.debug("Message '{}' from user '{}' ignored because player is not authenticated!", DataManager.getGson().toJson(p_197664_0_), player.getDisplayName());
//            callbackInfo.cancel();
//        }
//     }*/
//
//}
