package com.forgeessentials.core.preloader.mixin.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.client.C12PacketUpdateSign;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fe.event.world.SignEditEvent;
import net.minecraftforge.permission.PermissionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServer
{

    @Shadow
    public EntityPlayerMP playerEntity;

    /**
     * Post {@link SignEditEvent} to the event bus.
     *
     * @param packet the update sign packet
     */
    @Inject(
            method = "processUpdateSign",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/network/play/client/C12PacketUpdateSign;getLines()[Lnet/minecraft/util/IChatComponent;"
            ),
            require = 1,
            locals = LocalCapture.CAPTURE_FAILHARD,
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

    /**
     * Check if the player has permission to use command blocks.
     *
     * @param player the player
     * @param level the permission level
     * @param command the command
     * @return {@code true} if the player has permission
     */
    @Redirect(
            method = "processVanilla250Packet",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/EntityPlayerMP;canCommandSenderUseCommand(ILjava/lang/String;)Z"
            ),
            require = 1
    )
    private boolean checkCommandBlockPermission(EntityPlayerMP player, int level, String command)
    {
        return PermissionManager.checkPermission(player, PermissionManager.PERM_COMMANDBLOCK);
    }

    /**
     * Check if the player is in {@link WorldSettings.GameType#CREATIVE}.
     *
     * @param capabilities the player capabilities
     * @return always {@code true}
     */
    @Redirect(
            method = "processVanilla250Packet",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/entity/player/PlayerCapabilities;isCreativeMode:Z",
                    ordinal = 0
            ),
            require = 1
    )
    private boolean isCreativeMode(PlayerCapabilities capabilities)
    {
        // It's safe to always return true here because we only want to check if the player has
        // permission, which we've done above.
        return true;
    }

}