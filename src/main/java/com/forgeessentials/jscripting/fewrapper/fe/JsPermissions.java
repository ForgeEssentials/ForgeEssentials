package com.forgeessentials.jscripting.fewrapper.fe;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.minecraftforge.server.permission.DefaultPermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.jscripting.wrapper.mc.entity.JsEntityPlayer;

/**
 * @tsd.static Permissions
 */
public class JsPermissions
{

    /**
     * @tsd.ignore
     */
    public static JsServerZone<?> serverZone;

    public static boolean checkBooleanPermission(String permissionValue)
    {
        return APIRegistry.perms.checkBooleanPermission(permissionValue);
    }

    public static String getPermission(JsUserIdent ident, JsWorldPoint<?> point, JsWorldArea<?> area, String[] groups, String permissionNode, boolean isProperty)
    {
        return APIRegistry.perms.getPermission(ident.getThat(), point.getThat(), area.getThat(), Arrays.asList(groups), permissionNode, isProperty);
    }

    public static boolean checkPermission(JsEntityPlayer player, String permissionNode)
    {
        return APIRegistry.perms.checkPermission(player.getThat(), permissionNode);
    }

    public static String getPermissionProperty(JsEntityPlayer player, String permissionNode)
    {
        return APIRegistry.perms.getPermissionProperty(player.getThat(), permissionNode);
    }

    public static void registerPermissionDescription(String permissionNode, String description)
    {
        APIRegistry.perms.registerPermissionDescription(permissionNode, description);
    }

    public static String getPermissionDescription(String permissionNode)
    {
        return APIRegistry.perms.getPermissionDescription(permissionNode);
    }

    public static void registerPermission(String permissionNode, DefaultPermissionLevel level, String description)
    {
        APIRegistry.perms.registerPermission(permissionNode, level, description);
    }

    public static void registerPermissionProperty(String permissionNode, String defaultValue)
    {
        APIRegistry.perms.registerPermissionProperty(permissionNode, defaultValue);
    }

    public static void registerPermissionProperty(String permissionNode, String defaultValue, String description)
    {
        APIRegistry.perms.registerPermissionProperty(permissionNode, defaultValue, description);
    }

    public static void registerPermissionPropertyOp(String permissionNode, String defaultValue)
    {
        APIRegistry.perms.registerPermissionPropertyOp(permissionNode, defaultValue);
    }

    public static void registerPermissionPropertyOp(String permissionNode, String defaultValue, String description)
    {
        APIRegistry.perms.registerPermissionPropertyOp(permissionNode, defaultValue, description);
    }

    public static boolean checkUserPermission(JsUserIdent ident, String permissionNode)
    {
        return APIRegistry.perms.checkUserPermission(ident.getThat(), permissionNode);
    }

    public static String getUserPermissionProperty(JsUserIdent ident, String permissionNode)
    {
        return APIRegistry.perms.getUserPermissionProperty(ident.getThat(), permissionNode);
    }

    public static int getUserPermissionPropertyInt(JsUserIdent ident, String permissionNode)
    {
        return APIRegistry.perms.getUserPermissionPropertyInt(ident.getThat(), permissionNode);
    }

    public static boolean checkUserPermission(JsUserIdent ident, JsWorldPoint<?> targetPoint, String permissionNode)
    {
        return APIRegistry.perms.checkUserPermission(ident.getThat(), targetPoint.getThat(), permissionNode);
    }

    public static String getUserPermissionProperty(JsUserIdent ident, JsWorldPoint<?> targetPoint, String permissionNode)
    {
        return APIRegistry.perms.getUserPermissionProperty(ident.getThat(), targetPoint.getThat(), permissionNode);
    }

    public static boolean checkUserPermission(JsUserIdent ident, JsWorldArea<?> targetArea, String permissionNode)
    {
        return APIRegistry.perms.checkUserPermission(ident.getThat(), targetArea.getThat(), permissionNode);
    }

    public static String getUserPermissionProperty(JsUserIdent ident, JsWorldArea<?> targetArea, String permissionNode)
    {
        return APIRegistry.perms.getUserPermissionProperty(ident.getThat(), targetArea.getThat(), permissionNode);
    }

    public static boolean checkUserPermission(JsUserIdent ident, JsZone<?> zone, String permissionNode)
    {
        return APIRegistry.perms.checkUserPermission(ident.getThat(), zone.getThat(), permissionNode);
    }

    public static String getUserPermissionProperty(JsUserIdent ident, JsZone<?> zone, String permissionNode)
    {
        return APIRegistry.perms.getUserPermissionProperty(ident.getThat(), zone.getThat(), permissionNode);
    }

    public static String getGroupPermissionProperty(String group, String permissionNode)
    {
        return APIRegistry.perms.getGroupPermissionProperty(group, permissionNode);
    }

    public static String getGroupPermissionProperty(String group, JsZone<?> zone, String permissionNode)
    {
        return APIRegistry.perms.getGroupPermissionProperty(group, zone.getThat(), permissionNode);
    }

    public static boolean checkGroupPermission(String group, String permissionNode)
    {
        return APIRegistry.perms.checkGroupPermission(group, permissionNode);
    }

    public static boolean checkGroupPermission(String group, JsZone<?> zone, String permissionNode)
    {
        return APIRegistry.perms.checkGroupPermission(group, zone.getThat(), permissionNode);
    }

    public static String getGroupPermissionProperty(String group, JsWorldPoint<?> point, String permissionNode)
    {
        return APIRegistry.perms.getGroupPermissionProperty(group, point.getThat(), permissionNode);
    }

    public static boolean checkGroupPermission(String group, JsWorldPoint<?> point, String permissionNode)
    {
        return APIRegistry.perms.checkGroupPermission(group, point.getThat(), permissionNode);
    }

    public static String getGlobalPermissionProperty(String permissionNode)
    {
        return APIRegistry.perms.getGlobalPermissionProperty(permissionNode);
    }

    public static String getGlobalPermissionProperty(JsZone<?> zone, String permissionNode)
    {
        return APIRegistry.perms.getGlobalPermissionProperty(zone.getThat(), permissionNode);
    }

    public static boolean checkGlobalPermission(String permissionNode)
    {
        return APIRegistry.perms.checkGlobalPermission(permissionNode);
    }

    public static boolean checkGlobalPermission(JsZone<?> zone, String permissionNode)
    {
        return APIRegistry.perms.checkGlobalPermission(zone.getThat(), permissionNode);
    }

    public static void setPlayerPermission(JsUserIdent ident, String permissionNode, boolean value)
    {
        APIRegistry.perms.setPlayerPermission(ident.getThat(), permissionNode, value);
    }

    public static void setPlayerPermissionProperty(JsUserIdent ident, String permissionNode, String value)
    {
        APIRegistry.perms.setPlayerPermissionProperty(ident.getThat(), permissionNode, value);
    }

    public static void setGroupPermission(String group, String permissionNode, boolean value)
    {
        APIRegistry.perms.setGroupPermission(group, permissionNode, value);
    }

    public static void setGroupPermissionProperty(String group, String permissionNode, String value)
    {
        APIRegistry.perms.setGroupPermissionProperty(group, permissionNode, value);
    }

    public static JsZone<?>[] getZones()
    {
        Collection<Zone> zones = APIRegistry.perms.getZones();
        JsZone<?>[] jsZones = new JsZone<?>[zones.size()];
        int i = 0;
        for (Zone zone : zones) {
            jsZones[i++] = JsZone.get(zone);
        }
        return jsZones;
    }

    public static JsZone<?> getZoneById(int id)
    {
        return JsZone.get(APIRegistry.perms.getZoneById(id));
    }

    public static JsZone<?> getZoneById(String id)
    {
        return JsZone.get(APIRegistry.perms.getZoneById(id));
    }

    public static JsServerZone<?> getServerZone()
    {
        if (serverZone == null)
            serverZone = new JsServerZone<>(APIRegistry.perms.getServerZone());
        return serverZone;
    }

    public static boolean isSystemGroup(String group)
    {
        return APIRegistry.perms.isSystemGroup(group);
    }

    public static boolean groupExists(String groupName)
    {
        return APIRegistry.perms.groupExists(groupName);
    }

    public static boolean createGroup(String groupName)
    {
        return APIRegistry.perms.createGroup(groupName);
    }

    public static void addPlayerToGroup(JsUserIdent ident, String group)
    {
        APIRegistry.perms.addPlayerToGroup(ident.getThat(), group);
    }

    public static void removePlayerFromGroup(JsUserIdent ident, String group)
    {
        APIRegistry.perms.removePlayerFromGroup(ident.getThat(), group);
    }

    public static String getPrimaryGroup(JsUserIdent ident)
    {
        return APIRegistry.perms.getPrimaryGroup(ident.getThat());
    }

    public static JsZone getZoneAt(JsWorldPoint<?> worldPoint)
    {
        return getServerZone().getZoneAt(worldPoint);
    }

    public static List<JsZone<?>> getZonesAt(JsWorldPoint<?> worldPoint)
    {
        return getServerZone().getZonesAt(worldPoint);
    }

}
