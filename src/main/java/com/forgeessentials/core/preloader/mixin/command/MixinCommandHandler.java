package com.forgeessentials.core.preloader.mixin.command;

import com.forgeessentials.util.ServerUtil;
import net.minecraft.command.CommandHandler;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraftforge.permission.PermissionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

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

    /**
     * Drop the first element from the command arguments array.
     *
     * @param args the command arguments
     * @return the command arguments with the first element dropped
     */
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
