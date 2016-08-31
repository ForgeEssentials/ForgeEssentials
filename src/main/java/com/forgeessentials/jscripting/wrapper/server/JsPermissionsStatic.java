package com.forgeessentials.jscripting.wrapper.server;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;

import net.minecraftforge.permission.PermissionLevel;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.jscripting.wrapper.JsZone;
import com.forgeessentials.jscripting.wrapper.entity.JsEntityPlayer;
import com.forgeessentials.jscripting.wrapper.world.JsWorldArea;
import com.forgeessentials.jscripting.wrapper.world.JsWorldPoint;

public class JsPermissionsStatic
{
    private static JsServerZone<?> serverZone;
    private static Map<Zone, JsZone<?>> cache = new WeakHashMap<>();

    public boolean checkBooleanPermission(String permissionValue)
    {
        return APIRegistry.perms.checkBooleanPermission(permissionValue);
    }

    public String getPermission(JsUserIdent ident, JsWorldPoint<?> point, JsWorldArea<?> area, String[] groups, String permissionNode, boolean isProperty)
    {
        return APIRegistry.perms.getPermission(ident.getThat(), point.getThat(), area.getThat(), Arrays.asList(groups), permissionNode, isProperty);
    }

    public boolean checkPermission(JsEntityPlayer player, String permissionNode)
    {
        return APIRegistry.perms.checkPermission(player.getThat(), permissionNode);
    }

    public String getPermissionProperty(JsEntityPlayer player, String permissionNode)
    {
        return APIRegistry.perms.getPermissionProperty(player.getThat(), permissionNode);
    }

    public void registerPermissionDescription(String permissionNode, String description)
    {
        APIRegistry.perms.registerPermissionDescription(permissionNode, description);
    }

    public String getPermissionDescription(String permissionNode)
    {
        return APIRegistry.perms.getPermissionDescription(permissionNode);
    }

    public void registerPermission(String permission, int level)
    {
        APIRegistry.perms.registerPermission(permission, PermissionLevel.fromInteger(level));
    }

    public void registerPermission(String permissionNode, int level, String description)
    {
        APIRegistry.perms.registerPermission(permissionNode, PermissionLevel.fromInteger(level), description);
    }

    public void registerPermissionProperty(String permissionNode, String defaultValue)
    {
        APIRegistry.perms.registerPermissionProperty(permissionNode, defaultValue);
    }

    public void registerPermissionProperty(String permissionNode, String defaultValue, String description)
    {
        APIRegistry.perms.registerPermissionProperty(permissionNode, defaultValue, description);
    }

    public void registerPermissionPropertyOp(String permissionNode, String defaultValue)
    {
        APIRegistry.perms.registerPermissionPropertyOp(permissionNode, defaultValue);
    }

    public void registerPermissionPropertyOp(String permissionNode, String defaultValue, String description)
    {
        APIRegistry.perms.registerPermissionPropertyOp(permissionNode, defaultValue, description);
    }

    public boolean checkUserPermission(JsUserIdent ident, String permissionNode)
    {
        return APIRegistry.perms.checkUserPermission(ident.getThat(), permissionNode);
    }

    public String getUserPermissionProperty(JsUserIdent ident, String permissionNode)
    {
        return APIRegistry.perms.getUserPermissionProperty(ident.getThat(), permissionNode);
    }

    public int getUserPermissionPropertyInt(JsUserIdent ident, String permissionNode)
    {
        return APIRegistry.perms.getUserPermissionPropertyInt(ident.getThat(), permissionNode);
    }

    public boolean checkUserPermission(JsUserIdent ident, JsWorldPoint<?> targetPoint, String permissionNode)
    {
        return APIRegistry.perms.checkUserPermission(ident.getThat(), targetPoint.getThat(), permissionNode);
    }

    public String getUserPermissionProperty(JsUserIdent ident, JsWorldPoint<?> targetPoint, String permissionNode)
    {
        return APIRegistry.perms.getUserPermissionProperty(ident.getThat(), targetPoint.getThat(), permissionNode);
    }

    public boolean checkUserPermission(JsUserIdent ident, JsWorldArea<?> targetArea, String permissionNode)
    {
        return APIRegistry.perms.checkUserPermission(ident.getThat(), targetArea.getThat(), permissionNode);
    }

    public String getUserPermissionProperty(JsUserIdent ident, JsWorldArea<?> targetArea, String permissionNode)
    {
        return APIRegistry.perms.getUserPermissionProperty(ident.getThat(), targetArea.getThat(), permissionNode);
    }

    public boolean checkUserPermission(JsUserIdent ident, JsZone<?> zone, String permissionNode)
    {
        return APIRegistry.perms.checkUserPermission(ident.getThat(), zone.getThat(), permissionNode);
    }

    public String getUserPermissionProperty(JsUserIdent ident, JsZone<?> zone, String permissionNode)
    {
        return APIRegistry.perms.getUserPermissionProperty(ident.getThat(), zone.getThat(), permissionNode);
    }

    public String getGroupPermissionProperty(String group, String permissionNode)
    {
        return APIRegistry.perms.getGroupPermissionProperty(group, permissionNode);
    }

    public String getGroupPermissionProperty(String group, JsZone<?> zone, String permissionNode)
    {
        return APIRegistry.perms.getGroupPermissionProperty(group, zone.getThat(), permissionNode);
    }

    public boolean checkGroupPermission(String group, String permissionNode)
    {
        return APIRegistry.perms.checkGroupPermission(group, permissionNode);
    }

    public boolean checkGroupPermission(String group, JsZone<?> zone, String permissionNode)
    {
        return APIRegistry.perms.checkGroupPermission(group, zone.getThat(), permissionNode);
    }

    public String getGroupPermissionProperty(String group, JsWorldPoint<?> point, String permissionNode)
    {
        return APIRegistry.perms.getGroupPermissionProperty(group, point.getThat(), permissionNode);
    }

    public boolean checkGroupPermission(String group, JsWorldPoint<?> point, String permissionNode)
    {
        return APIRegistry.perms.checkGroupPermission(group, point.getThat(), permissionNode);
    }

    public String getGlobalPermissionProperty(String permissionNode)
    {
        return APIRegistry.perms.getGlobalPermissionProperty(permissionNode);
    }

    public String getGlobalPermissionProperty(JsZone<?> zone, String permissionNode)
    {
        return APIRegistry.perms.getGlobalPermissionProperty(zone.getThat(), permissionNode);
    }

    public boolean checkGlobalPermission(String permissionNode)
    {
        return APIRegistry.perms.checkGlobalPermission(permissionNode);
    }

    public boolean checkGlobalPermission(JsZone<?> zone, String permissionNode)
    {
        return APIRegistry.perms.checkGlobalPermission(zone.getThat(), permissionNode);
    }

    public void setPlayerPermission(JsUserIdent ident, String permissionNode, boolean value)
    {
        APIRegistry.perms.setPlayerPermission(ident.getThat(), permissionNode, value);
    }

    public void setPlayerPermissionProperty(JsUserIdent ident, String permissionNode, String value)
    {
        APIRegistry.perms.setPlayerPermissionProperty(ident.getThat(), permissionNode, value);
    }

    public void setGroupPermission(String group, String permissionNode, boolean value)
    {
        APIRegistry.perms.setGroupPermission(group, permissionNode, value);
    }

    public void setGroupPermissionProperty(String group, String permissionNode, String value)
    {
        APIRegistry.perms.setGroupPermissionProperty(group, permissionNode, value);
    }

    public JsZone<?>[] getZones()
    {
        Collection<Zone> zones = APIRegistry.perms.getZones();
        JsZone<?>[] jsZones = new JsZone<?>[zones.size()];
        int i = 0;
        for (Zone zone : zones) {
            jsZones[i++] = getCachedZone(zone);
        }
        return jsZones;
    }

    public JsZone<?> getZoneById(int id)
    {
        return getCachedZone(APIRegistry.perms.getZoneById(id));
    }

    public JsZone<?> getZoneById(String id)
    {
        return getCachedZone(APIRegistry.perms.getZoneById(id));
    }

    public JsServerZone<?> getServerZone()
    {
        if (serverZone == null)
            serverZone = new JsServerZone<>(APIRegistry.perms.getServerZone());
        return serverZone;
    }

    public boolean isSystemGroup(String group)
    {
        return APIRegistry.perms.isSystemGroup(group);
    }

    public boolean groupExists(String groupName)
    {
        return APIRegistry.perms.groupExists(groupName);
    }

    public boolean createGroup(String groupName)
    {
        return APIRegistry.perms.createGroup(groupName);
    }

    public void addPlayerToGroup(JsUserIdent ident, String group)
    {
        APIRegistry.perms.addPlayerToGroup(ident.getThat(), group);
    }

    public void removePlayerFromGroup(JsUserIdent ident, String group)
    {
        APIRegistry.perms.removePlayerFromGroup(ident.getThat(), group);
    }

    public String getPrimaryGroup(JsUserIdent ident)
    {
        return APIRegistry.perms.getPrimaryGroup(ident.getThat());
    }

    private static JsZone<?> getCachedZone(Zone zone) {
        if (cache.containsKey(zone))
            return cache.get(zone);
        JsZone<?> jsZone = new JsZone<>(zone);
        cache.put(zone, jsZone);
        return jsZone;
    }

}
