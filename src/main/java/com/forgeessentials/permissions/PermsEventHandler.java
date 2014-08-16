package com.forgeessentials.permissions;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Group;
import com.forgeessentials.api.permissions.query.PropQueryPlayerZone;
import com.forgeessentials.util.ChatUtils;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.events.PlayerChangedZone;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;

import java.util.Arrays;
import java.util.List;

public class PermsEventHandler {

    protected PermsEventHandler()
    {

    }
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onZoneChange(PlayerChangedZone event)
    {
        PropQueryPlayerZone query1 = new PropQueryPlayerZone(event.entityPlayer, "fe.perm.zone.exit", event.beforeZone, false);
        PropQueryPlayerZone query2 = new PropQueryPlayerZone(event.entityPlayer, "fe.perm.zone.entry", event.afterZone, false);

        APIRegistry.perms.getPermissionProp(query1);
        if (query1.hasValue())
        {
            ChatUtils.sendMessage(event.entityPlayer, FunctionHelper.formatColors(query1.getStringValue()));
        }

        APIRegistry.perms.getPermissionProp(query2);
        if (query2.hasValue())
        {
            ChatUtils.sendMessage(event.entityPlayer, FunctionHelper.formatColors(query2.getStringValue()));
        }

    }

    private static String[] defgroups;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void assignUserDefaults(PlayerEvent.PlayerLoggedInEvent e)
    {
        if (SqlHelper.doesPlayerExist(e.player.getPersistentID().toString())){ return;}

        for (String group : defgroups)
        {
            Group g = APIRegistry.getAsFEGroup(group);
            APIRegistry.perms.addPlayerToGroup(group, e.player.getPersistentID(), g.zoneName);
        }

    }

    public static void addDefaultGroup(String groupName)
    {
        final int n = defgroups.length;
        String[] defgroups1 = Arrays.copyOf(defgroups, n + 1);
        defgroups1[n] = groupName;
        defgroups = defgroups1;
    }

    public static void setDefaultGroup(String groupName)
    {
        defgroups = new String[1];
        defgroups[0] = groupName;
    }
    public static List<String> getDefaultGroups()
    {
        return Arrays.asList(defgroups);
    }
}
