package com.forgeessentials.permissions.core;

import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.ServerZone;

public abstract class ZonePersistenceProvider
{

    public abstract void save(ServerZone serverZone);

    public abstract ServerZone load();

    public static void writeUserGroupPermissions(ServerZone serverZone)
    {
        // Clear groups from players (leftovers, if player was removed from all groups)
        for (UserIdent ident : serverZone.getPlayerPermissions().keySet())
            serverZone.clearPlayerPermission(ident, FEPermissions.PLAYER_GROUPS);

        // Add groups to players
        for (Entry<UserIdent, Set<String>> entry : serverZone.getPlayerGroups().entrySet())
            serverZone.setPlayerPermissionProperty(entry.getKey(), FEPermissions.PLAYER_GROUPS, StringUtils.join(entry.getValue(), ","));
    }

    public static void readUserGroupPermissions(ServerZone serverZone)
    {
        for (UserIdent ident : serverZone.getPlayerPermissions().keySet())
        {
            serverZone.registerPlayer(ident);
            String groupList = serverZone.getPlayerPermission(ident, FEPermissions.PLAYER_GROUPS);
            serverZone.clearPlayerPermission(ident, FEPermissions.PLAYER_GROUPS);
            if (groupList == null)
                continue;
            for (String group : groupList.replace(" ", "").split(","))
            {
                serverZone.addPlayerToGroup(ident, group);
            }
        }
    }

}
