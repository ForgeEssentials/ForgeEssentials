package com.forgeessentials.core.preloader.mixin.command;

import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.CommandBlockBaseLogic;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;

@Mixin(value = { EntityPlayerMP.class, MinecraftServer.class, RConConsoleSource.class, CommandBlockBaseLogic.class},
        targets = {"net/minecraft/tileentity/TileEntitySign$1", "net/minecraft/tileentity/TileEntitySign$2"})
public abstract class MixinICommandSender implements ICommandSender
{
    @Inject(method = "canUseCommand(ILjava/lang/String;)Z", at = @At("HEAD"), cancellable = true)
    private void canUseCommand(final int permissionLevel, final String commandName, final CallbackInfoReturnable<Boolean> cir) {
        String permNode;
        if (!commandName.contains("."))
        {
            permNode = "command." + commandName;
        }
        else
        {
            permNode = commandName;
        }
        cir.setReturnValue(APIRegistry.perms.checkUserPermission(UserIdent.get(this),permNode));

    }
}
