package com.forgeessentials.core.preloader.mixin.network;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.permission.PermissionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NetHandlerPlayServer.class)
public class MixinNetHandlerPlayServer {


    /**
     * Check if the player has permission to use command blocks.
     *
     * @param player  the player
     * @param level   the permission level
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
    private boolean checkCommandBlockPermission(EntityPlayerMP player, int level, String command) {
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
    private boolean isCreativeMode(PlayerCapabilities capabilities) {
        // It's safe to always return true here because we only want to check if the player has
        // permission, which we've done above.
        return true;
    }

}
