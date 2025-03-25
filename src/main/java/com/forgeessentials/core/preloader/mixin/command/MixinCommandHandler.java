package com.forgeessentials.core.preloader.mixin.command;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.permission.PermissionAPI;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.core.misc.PermissionManager;
import com.forgeessentials.util.DoAsCommandSender;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Mixin(CommandHandler.class)
public class MixinCommandHandler
{

    /**
     * Check if the sender has permission for the command.
     *
     * @param command the command
     * @param sender the sender
     * @return {@code true} if the sender has permission
     */
    @Redirect(
            method = "getTabCompletions(Lnet/minecraft/command/ICommandSender;Ljava/lang/String;Lnet/minecraft/util/math/BlockPos;)Ljava/util/List;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/command/ICommand;checkPermission(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/command/ICommandSender;)Z"
            ),
            require = 1
    )
    private boolean tabComplete(ICommand command, MinecraftServer server, ICommandSender sender)
    {
        return checkPerms(command, sender);
    }

    /**
     * Check if the sender has permission for the command.
     *
     * @param command the command
     * @param sender the sender
     * @return {@code true} if the sender has permission
     */
    @Redirect(
            method = "getPossibleCommands(Lnet/minecraft/command/ICommandSender;)Ljava/util/List;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/command/ICommand;checkPermission(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/command/ICommandSender;)Z"
            ),
            require = 1
    )
    private boolean hasPermissionAll(ICommand command, MinecraftServer server, ICommandSender sender)
    {
        return checkPerms(command, sender);
    }

    @Redirect(
            method = "executeCommand(Lnet/minecraft/command/ICommandSender;Ljava/lang/String;)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/command/ICommand;checkPermission(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/command/ICommandSender;)Z"
            ),
            require = 1
    )
    private boolean hasPermission(ICommand command, MinecraftServer server, ICommandSender sender)
    {
        return true;
    }

    public boolean checkPerms(ICommand command, ICommandSender sender) {
        String node = PermissionManager.getCommandPermission(command);
        if (sender instanceof DoAsCommandSender) {
            if (!((DoAsCommandSender) sender).getIdent().isPlayer()) {
                if (((DoAsCommandSender) sender).getIdent().isNpc()) {
                    return PermissionAPI.hasPermission(((DoAsCommandSender) sender).getIdent().getGameProfile(), node, null);
                }
                else
                {
                    return true;
                }
            } else {
                if (((DoAsCommandSender) sender).getIdent().hasPlayer())
                {
                    return PermissionAPI.hasPermission(((DoAsCommandSender) sender).getIdent().getPlayer(), node);
                } else {
                    return PermissionAPI.hasPermission(((DoAsCommandSender) sender).getIdent().getGameProfile(), node,  null);
                }
            }
        }

        if (sender instanceof EntityPlayer)
        {
            return PermissionAPI.hasPermission((EntityPlayer) sender, node);
        } else {
            UserIdent ident = UserIdent.get(sender);
            return PermissionAPI.hasPermission(ident.getGameProfile(), node, null);
        }
    }
}