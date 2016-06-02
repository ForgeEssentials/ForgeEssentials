package com.forgeessentials.core.preloader.mixin.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;
import net.minecraftforge.permission.PermissionManager;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.forgeessentials.util.ServerUtil;

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
        return PermissionManager.checkPermission(sender, command);
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
                    target = "Lnet/minecraft/command/ICommand;canCommandSenderUseCommand(Lnet/minecraft/command/ICommandSender;)Z"
            ),
            require = 1
    )
    private boolean hasPermissionAll(ICommand command, ICommandSender sender)
    {
        return PermissionManager.checkPermission(sender, command);
    }

}