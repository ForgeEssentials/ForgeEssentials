package com.forgeessentials.compat.worldedit;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.core.misc.PermissionManager;
import com.sk89q.worldedit.forge.ForgePermissionsProvider;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.server.permission.DefaultPermissionLevel;

public class PermissionsHandler implements ForgePermissionsProvider {

	@Override
	public boolean hasPermission(ServerPlayerEntity player, String permission) {
		return APIRegistry.perms.checkPermission(player, permission);
	}

	@Override
	public void registerPermission(String permission) {
		boolean allowForAllPlayers = permission.startsWith("worldedit.selection");

		PermissionManager.registerCommandPermission(permission.split("\\.")[1], permission,
				allowForAllPlayers ? DefaultPermissionLevel.ALL : DefaultPermissionLevel.OP);
	}

}