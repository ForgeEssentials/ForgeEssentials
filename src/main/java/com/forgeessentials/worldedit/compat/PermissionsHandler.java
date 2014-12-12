package com.forgeessentials.worldedit.compat;

import com.sk89q.worldedit.forge.ForgePermissionsProvider;
import net.minecraft.command.ICommand;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permissions.PermissionsManager;
import net.minecraftforge.permissions.PermissionsManager.RegisteredPermValue;
import net.minecraftforge.server.CommandHandlerForge;

public class PermissionsHandler implements ForgePermissionsProvider
{
    @Override
    public boolean hasPermission(EntityPlayerMP player, String permission)
    {
        return PermissionsManager.checkPermission(player, permission);
    }

    @Override
    public void registerPermission(ICommand command, String permission)
    {
        CommandHandlerForge.registerCommand(command, permission, RegisteredPermValue.OP);
    }
}
