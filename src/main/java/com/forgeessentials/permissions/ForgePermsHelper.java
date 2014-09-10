package com.forgeessentials.permissions;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;
import net.minecraftforge.permissions.IContext;
import net.minecraftforge.permissions.IPermissionsProvider;
import net.minecraftforge.permissions.PermissionsManager;

// quick and dirty class to tide us over until newperms comes in
public class ForgePermsHelper implements IPermissionsProvider{
    @Override
    public boolean checkPermission(IContext context, String permissionNode)
    {
        return APIRegistry.perms.checkPermAllowed(new PermQueryPlayer(context.getPlayer(), permissionNode));
    }

    @Override
    public void registerPermission(String permissionNode, PermissionsManager.RegisteredPermValue level)
    {
        APIRegistry.permReg.registerPermissionLevel(permissionNode, RegGroup.fromForgeLevel(level));

    }
}
