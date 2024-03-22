package com.forgeessentials.compat.worldedit;

import com.forgeessentials.api.APIRegistry;
import com.sk89q.worldedit.forge.ForgePermissionsProvider;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class PermissionsHandler implements ForgePermissionsProvider
{

    @Override
    public boolean hasPermission(ServerPlayer player, String permission)
    {
    	System.out.println("Checking We Permission: "+permission);
        return APIRegistry.perms.checkPermission(player, permission);
    }

    @Override
    public void registerPermission(String permission)
    {
    	System.out.println("Registering We Permission: "+permission);
        APIRegistry.perms.registerPermission(permission, DefaultPermissionLevel.OP, "");
        // boolean allowForAllPlayers = permission.startsWith("worldedit.selection");
        // PermissionManager.registerCommandPermission(permission.split("\\.")[1], permission,
        // allowForAllPlayers ? DefaultPermissionLevel.ALL : DefaultPermissionLevel.OP);
    }

}