package com.forgeessentials.core.mixin.network;
//
//import net.minecraft.block.BlockState;
//import net.minecraft.entity.player.ServerPlayerEntity;
//import net.minecraft.network.play.ServerPlayNetHandler;
//import net.minecraft.network.play.client.CUpdateSignPacket;
//import net.minecraft.tileentity.SignTileEntity;
//import net.minecraft.tileentity.TileEntity;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.util.text.StringTextComponent;
//import net.minecraft.world.server.ServerWorld;
//import net.minecraftforge.common.MinecraftForge;
//import net.minecraftforge.fe.event.world.SignEditEvent;
//
//import org.apache.logging.log4j.Logger;
//import org.spongepowered.asm.mixin.Mixin;
//import org.spongepowered.asm.mixin.Shadow;
//import org.spongepowered.asm.mixin.injection.At;
//import org.spongepowered.asm.mixin.injection.Inject;
//import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
//import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
//
//@Mixin(ServerPlayNetHandler.class)
//public class MixinNetHandlerPlayServer
//{
//
//    @Shadow
//    public ServerPlayerEntity player;
//    @Shadow
//    private Logger LOGGER;
//  //TODO overwrite updateSignText from ServerPlayNetHandler void net.minecraft.network.play.ServerPlayNetHandler.updateSignText()
//    /**
//     * Post {@link SignEditEvent} to the event bus.
//     *
//     * @param packetIn
//     *            the update sign packet
//     */
//    @Inject(
//            method = "handleSignUpdate",
//            at = @At("HEAD"),
//            //at = @At(
//            //        value = "INVOKE",
//                    //target = "Lnet/minecraft/network/play/client/CPacketUpdateSign(Lnet/minecraft/network/play/IServerPlayNetHandler);handle(Lnet/minecraft/network/play/client/CUpdateSignPacket)V"),
//            require = 1,
//            locals = LocalCapture.CAPTURE_FAILHARD,
//            cancellable = true)
//    private void getLines(CUpdateSignPacket p_244542_1_, CallbackInfo ci)
//    {
//        ServerWorld serverworld = this.player.getLevel();
//        BlockPos blockpos = p_244542_1_.getPos();
//        if (serverworld.hasChunkAt(blockpos)) {
//            BlockState blockstate = serverworld.getBlockState(blockpos);
//            TileEntity tileentity = serverworld.getBlockEntity(blockpos);
//            if (!(tileentity instanceof SignTileEntity)) {
//               return;
//            }
//
//            SignTileEntity signtileentity = (SignTileEntity)tileentity;
//            SignEditEvent event = new SignEditEvent(p_244542_1_.getPos(), p_244542_1_.getLines(), this.player);
//            if (!MinecraftForge.EVENT_BUS.post(event))
//            {
//                for (int i = 0; i < event.text.length; ++i)
//                {
//                    if (event.formatted[i] == null)
//                        signtileentity.setMessage(i, new StringTextComponent(event.text[i]));
//                    else
//                        signtileentity.setMessage(i,  event.formatted[i]);
//                }
//            }
//            if (!signtileentity.isEditable() || signtileentity.getPlayerWhoMayEdit() != this.player) {
//               LOGGER.warn("Player {} just tried to change non-editable sign", (Object)this.player.getName().getString());
//               return;
//            }
//
//            for(int i = 0; i < event.text.length; ++i) {
//               signtileentity.setMessage(i, new StringTextComponent(event.text[i]));
//            }
//
//            signtileentity.setChanged();
//            serverworld.sendBlockUpdated(blockpos, blockstate, blockstate, 3);
//            ci.cancel();
//         }
//    }
//
//    /**
//     * Copy the {@link #signLines} to the {@link TileEntitySign}.
//     *
//     * @param src
//     *            the source array
//     * @param srcPos
//     *            starting position in the source array
//     * @param dest
//     *            the destination array
//     * @param destPos
//     *            starting position in the destination array
//     * @param length
//     *            the number of array elements to be copied
//     *
//     *            @Redirect( method = "processUpdateSign", at = @At( value = "INVOKE", target = "Ljava/lang/System;arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V" ), require =
//     *            1 ) private void copyLinesToBlockEntity(Object src, int srcPos, Object dest, int destPos, int length) { if (this.signLines.length == 0) return; // You may get a
//     *            warning that `dest` is not Object[] - don't change this, or Mixin will yell at you. System.arraycopy(this.signLines, srcPos, dest, destPos, length);
//     *            this.signLines = null; }
//     */
//
//}