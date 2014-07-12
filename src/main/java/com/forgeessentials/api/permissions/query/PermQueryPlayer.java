package com.forgeessentials.api.permissions.query;

import com.forgeessentials.permissions.PermissionChecker;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Reuslts are: default, allow, deny.
 *
 * @author AbrarSyed
 */
@Event.HasResult
public class PermQueryPlayer extends PermQuery {
    public EntityPlayer doer;
    public boolean dOverride;

    /**
     * Assumes the Players position as the "doneTo" point.
     *
     * @param player
     * @param permission
     */
    public PermQueryPlayer(EntityPlayer player, String permission)
    {
        doer = player;
        checker = new PermissionChecker(permission);
        checkForward = false;
    }

    /**
     * Assumes the Players position as the "doneTo" point.
     *
     * @param player
     * @param permission
     * @param checkForward Specifies to only return allow if all the children of the
     *                     permissions are allowed.
     */
    public PermQueryPlayer(EntityPlayer player, String permission, boolean checkForward)
    {
        doer = player;
        checker = new PermissionChecker(permission);
        this.checkForward = checkForward;
    }
}
