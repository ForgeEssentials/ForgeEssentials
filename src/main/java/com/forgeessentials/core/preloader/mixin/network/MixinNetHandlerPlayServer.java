package com.forgeessentials.core.preloader.mixin.network;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.CPacketUpdateSign;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fe.event.world.SignEditEvent;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServer
{

    @Shadow
    public EntityPlayerMP player;

    /**
     * Post {@link SignEditEvent} to the event bus.
     *
     * @param packetIn the update sign packet
     */
    @Inject(
            method = "processUpdateSign",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/play/client/CPacketUpdateSign;getLines()[Ljava/lang/String;"
            ),
            require = 1,
            locals = LocalCapture.CAPTURE_FAILHARD,
            cancellable = true
    )
    private void getLines(CPacketUpdateSign packetIn, CallbackInfo ci, WorldServer worldserver, BlockPos blockpos, IBlockState iblockstate, TileEntity tileentity, TileEntitySign tileentitysign)
    {
        SignEditEvent event = new SignEditEvent(packetIn.getPosition(), packetIn.getLines(), this.player);
        if (!MinecraftForge.EVENT_BUS.post(event))
        {
            for (int i = 0; i < event.text.length; ++i)
            {
                if (event.formatted[i] == null)
                tileentitysign.signText[i] = new TextComponentString(event.text[i]);
                else tileentitysign.signText[i] = event.formatted[i];
            }
        }

        tileentitysign.markDirty();
        worldserver.notifyBlockUpdate(blockpos, iblockstate, iblockstate, 3);
        ci.cancel();
    }

    /**
     * Copy the {@link #signLines} to the {@link TileEntitySign}.
     *
     * @param src the source array
     * @param srcPos starting position in the source array
     * @param dest the destination array
     * @param destPos starting position in the destination array
     * @param length the number of array elements to be copied
     *
    @Redirect(
            method = "processUpdateSign",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/System;arraycopy(Ljava/lang/Object;ILjava/lang/Object;II)V"
            ),
            require = 1
    )
    private void copyLinesToBlockEntity(Object src, int srcPos, Object dest, int destPos, int length)
    {
        if (this.signLines.length == 0)
            return;
        // You may get a warning that `dest` is not Object[] - don't change this, or Mixin will yell at you.
        System.arraycopy(this.signLines, srcPos, dest, destPos, length);
        this.signLines = null;
    }
    */

}