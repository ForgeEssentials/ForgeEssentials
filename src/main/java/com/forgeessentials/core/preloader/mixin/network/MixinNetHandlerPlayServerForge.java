package com.forgeessentials.core.preloader.mixin.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fe.event.world.SignEditEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServerForge {
    @Shadow
    public EntityPlayerMP playerEntity;

    private String[] signLines;

    /**
     * Post {@link SignEditEvent} to the event bus.
     *
     * @param packet the update sign packet
     * @param ci the callback info
     */
    @Inject(
            method = "processUpdateSign",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/play/client/C12PacketUpdateSign;getLines()[Lnet/minecraft/util/IChatComponent;"
            ),
            require = 1,
            cancellable = true
    )
    private void getLines(C12PacketUpdateSign packet, CallbackInfo ci, WorldServer worldserver, BlockPos blockpos, TileEntity entity, TileEntitySign tileentitysign)
    {
        SignEditEvent event = new SignEditEvent(packet.getPosition(), packet.getLines(), this.playerEntity);
        if (!MinecraftForge.EVENT_BUS.post(event))
        {
            for (int i = 0; i < event.text.length; ++i)
            {
                tileentitysign.signText[i] = event.text[i];
            }
        }

        tileentitysign.markDirty();
        worldserver.markBlockForUpdate(blockpos);
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
     */
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
        // You may get a warning that `dest` is not Object[] - don't change this, or Mixin will yell at you.
        System.arraycopy(this.signLines, srcPos, dest, destPos, length);
        this.signLines = null;
    }
}
