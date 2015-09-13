package com.forgeessentials.compat.worldedit;

import net.minecraft.command.ICommand;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.permission.PermissionLevel;
import net.minecraftforge.permission.PermissionManager;

import com.sk89q.worldedit.forge.ForgePermissionsProvider;

public class PermissionsHandler implements ForgePermissionsProvider
{

    @Override
    public boolean hasPermission(EntityPlayerMP player, String permission)
    {
        return PermissionManager.checkPermission(player, permission);
    }

    @Override
    public void registerPermission(ICommand command, String permission)
    {
        boolean isOp = permission.startsWith("worldedit.selection");
        PermissionManager.registerPermission(permission, isOp ? PermissionLevel.OP : PermissionLevel.TRUE);
    }

}
