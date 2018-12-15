package com.forgeessentials.core.preloader.mixin.command;

import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraftforge.server.permission.PermissionAPI;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.UserIdent.NpcUserIdent;
import com.forgeessentials.core.misc.PermissionManager;
import com.forgeessentials.util.DoAsCommandSender;

@SuppressWarnings({ "rawtypes", "unchecked" })
@Mixin(CommandHandler.class)
public class MixinCommandHandler
{

    /**
     * TODO do we still need this
     * Check if the sender has permission for the command.
     *
     * @param command the command
     * @param sender the sender
     * @return {@code true} if the sender has permission
     *
    @Redirect(
            method = "getPossibleCommands(Lnet/minecraft/command/ICommandSender;Ljava/lang/String;)Ljava/util/List;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/command/ICommand;canCommandSenderUseCommand(Lnet/minecraft/command/ICommandSender;)Z"
            ),
            require = 1
    )
    private boolean hasPermissionBeginWith(ICommand command, ICommandSender sender)
    {
        return PermissionAPI.hasPermission(sender, command);
    }
    */

    /**
     * Drop the first element from the command arguments array.
     *
     * @param args the command arguments
     * @return the command arguments with the first element dropped
    @Redirect(
            method = "getPossibleCommands(Lnet/minecraft/command/ICommandSender;Ljava/lang/String;)Ljava/util/List;",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/command/CommandHandler;dropFirstString([Ljava/lang/String;)[Ljava/lang/String;"
            ),
            require = 1
    )
    private String[] dropFirstArg(String[] args)
    {
        return ServerUtil.dropFirst(args);
    }
    */

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
        if (sender instanceof MinecraftServer || sender instanceof CommandBlockBaseLogic)
            return true;
        return PermissionAPI.hasPermission((EntityPlayer) sender, PermissionManager.getCommandPermission(command));
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
                return PermissionAPI.hasPermission(((DoAsCommandSender) sender).getIdent().getPlayer(), node);
            }
        }
        if (sender instanceof MinecraftServer || sender instanceof CommandBlockBaseLogic)
            return true;
        if (sender instanceof EntityPlayer)
        {
            return PermissionAPI.hasPermission((EntityPlayer) sender, node);
        } else {
            NpcUserIdent ident = UserIdent.getNpc(sender.getName());
            return PermissionAPI.hasPermission(ident.getGameProfile(), node, null);
        }
    }

}