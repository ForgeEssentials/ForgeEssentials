package com.forgeessentials.api.permissions;

import java.util.Collection;
import java.util.List;
import java.util.SortedSet;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.permission.IPermissionProvider;
import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.commons.selections.WorldArea;
import com.forgeessentials.commons.selections.WorldPoint;

/**
 * {@link IPermissionsHelper} is the primary access-point to the permissions-system.
 */
public interface IPermissionsHelper extends IPermissionProviderBase
{

    // ---------------------------------------------------------------------------

    /**
     * Checks a permission for a player in the specified zone
     *
     * @param ident
     * @param zone
     * @param permissionNode
     */
    boolean checkUserPermission(UserIdent ident, Zone zone, String permissionNode);

    /**
     * Gets a permission-property for a player in the specified zone
     *
     * @param ident
     * @param zone
     * @param permissionNode
     * @return property, if it exists, null otherwise
     */
    String getUserPermissionProperty(UserIdent ident, Zone zone, String permissionNode);

    /**
     * Gets a permission-property for the specified group in the specified zone
     *
     * @param zone
     * @param permissionNode
     * @return property, if it exists, null otherwise
     */
    String getGroupPermissionProperty(String group, Zone zone, String permissionNode);

    /**
     * Gets a permission for the specified group in the specified zone
     *
     * @param zone
     * @param permissionNode
     * @return property, if it exists, null otherwise
     */
    boolean checkGroupPermission(String group, Zone zone, String permissionNode);

    /**
     * Gets a global permission-property from the _ALL_ group in the specified zone
     *
     * @param zone
     * @param permissionNode
     * @return property, if it exists, null otherwise
     */
    String getGlobalPermissionProperty(Zone zone, String permissionNode);

    /**
     * Gets a global permission from the _ALL_ group in the specified zone
     *
     * @param zone
     * @param permissionNode
     * @return
     */
    boolean checkGlobalPermission(Zone zone, String permissionNode);

    // ---------------------------------------------------------------------------

    /**
     * Get all registered zones
     */
    Collection<Zone> getZones();

    /**
     * Returns a zone by it's ID
     *
     * @return Zone or null
     */
    Zone getZoneById(int id);

    /**
     * Returns a zone by it's ID as string. It the string is no valid integer, it returns null.
     *
     * @return Zone or null
     */
    Zone getZoneById(String id);

    /**
     * Returns the {@link ServerZone}
     */
    ServerZone getServerZone();

    // ---------------------------------------------------------------------------

    /**
     * Get all groups the player belongs to, together with the system- and included groups. Groups are ordered by
     * priority.
     *
     * @param ident
     */
    SortedSet<GroupEntry> getPlayerGroups(UserIdent ident);

    /**
     * Get all groups the player belongs to. Groups are ordered by priority.
     *
     * @param ident
     */
    SortedSet<GroupEntry> getStoredPlayerGroups(UserIdent ident);

}
