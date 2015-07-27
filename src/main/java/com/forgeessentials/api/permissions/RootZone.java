package com.forgeessentials.api.permissions;

import com.forgeessentials.api.permissions.ServerZone.PermissionDebugger;
import com.forgeessentials.commons.selections.WorldArea;
import com.forgeessentials.commons.selections.WorldPoint;

/**
 * {@link RootZone} is the root of the permission tree and has the lowest priority of all zones. It's purpose is to hold
 * default permissions, which have been set by {@link IPermissionsHelper#registerPermissionProperty(String, String)}
 */
public class RootZone extends Zone
{

    protected ServerZone serverZone;

    protected IPermissionsHelper permissionHelper;

    protected PermissionDebugger permissionDebugger;

    public RootZone(IPermissionsHelper permissionHelper)
    {
        super(0);
        this.permissionHelper = permissionHelper;
    }

    @Override
    public boolean isInZone(WorldPoint point)
    {
        return true;
    }

    @Override
    public boolean isInZone(WorldArea point)
    {
        return true;
    }

    @Override
    public boolean isPartOfZone(WorldArea point)
    {
        return true;
    }

    @Override
    public String getName()
    {
        return "_ROOT_";
    }

    @Override
    public Zone getParent()
    {
        return null;
    }

    @Override
    public ServerZone getServerZone()
    {
        return serverZone;
    }

    public void setServerZone(ServerZone serverZone)
    {
        this.serverZone = serverZone;
        if (serverZone != null)
            serverZone.setRootZone(this);
    }

    public IPermissionsHelper getPermissionHelper()
    {
        return permissionHelper;
    }

    public void setPermissionDebugger(PermissionDebugger permissionDebugger)
    {
        this.permissionDebugger = permissionDebugger;
    }

    public PermissionDebugger getPermissionDebugger()
    {
        return permissionDebugger;
    }

    @Override
    public void setDirty()
    {
        permissionHelper.setDirty(true);
    }

}
