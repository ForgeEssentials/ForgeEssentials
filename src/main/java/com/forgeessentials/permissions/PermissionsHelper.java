package com.forgeessentials.permissions;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Group;
import com.forgeessentials.api.permissions.IPermissionsHelper;
import com.forgeessentials.api.permissions.RegGroup;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.api.permissions.query.PermQuery;
import com.forgeessentials.api.permissions.query.PermQuery.PermResult;
import com.forgeessentials.api.permissions.query.PermQueryPlayer;
import com.forgeessentials.api.permissions.query.PropQuery;
import com.forgeessentials.util.selections.WorldPoint;
import com.forgeessentials.util.events.ModifyPlayerGroupEvent.AddPlayerGroupEvent;
import com.forgeessentials.util.events.ModifyPlayerGroupEvent.RemovePlayerGroupEvent;
import com.forgeessentials.util.events.ModifyPlayerGroupEvent.SetPlayerGroupEvent;
import com.forgeessentials.util.events.PermissionPropSetEvent;
import com.forgeessentials.util.events.PermissionSetEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.UUID;

@SuppressWarnings("rawtypes")
public class PermissionsHelper implements IPermissionsHelper{
    private Group DEFAULT = new Group(RegGroup.ZONE.toString(), " ", " ", null, APIRegistry.zones.getGLOBAL().getZoneName(), 0);

    public static PermissionsHelper INSTANCE;

    private final UUID ENTRY_PLAYER_NAME = UUID.fromString("2ecbdcc0-0e4b-4a0d-92a1-221dda46b784");
    private String entryPlayerPrefix = "";
    private String entryPlayerSuffix = "";

    public PermissionsHelper()
    {
        INSTANCE = this;
    }

    @Override
    public boolean checkPermAllowed(PermQuery query)
    {
        if (query instanceof PermQueryPlayer)
        {
            PermissionsPlayerHandler.parseQuery((PermQueryPlayer) query);
        }
        else
        {
            PermissionsBlanketHandler.parseQuery(query);
        }

        return query.isAllowed();
    }

    @Override
    public PermResult checkPermResult(PermQuery query)
    {
        if (query instanceof PermQueryPlayer)
        {
            PermissionsPlayerHandler.parseQuery((PermQueryPlayer) query);
        }
        else
        {
            PermissionsBlanketHandler.parseQuery(query);
        }

        return query.getResult();
    }

    @Override
    public void getPermissionProp(PropQuery query)
    {
        PermissionsPropHandler.handleQuery(query);
    }

    @Override
    public Group createGroupInZone(String groupName, String zoneName, String prefix, String suffix, String parent, int priority)
    {
        Group g = new Group(groupName, prefix, suffix, parent, zoneName, priority);
        SqlHelper.createGroup(g);
        return g;
    }

    @Override
    public String setPlayerPermission(UUID username, String permission, boolean allow, String zoneID)
    {
        try
        {
            Zone zone = APIRegistry.zones.getZone(zoneID);
            if (zone == null)
            {
                return String.format("No zone by the name %s exists!", zoneID);
            }

            Permission perm = new Permission(permission, allow);

            // send out permissions string.
            PermissionSetEvent event = new PermissionSetEvent(perm, zone, "p:" + username);
            if (MinecraftForge.EVENT_BUS.post(event))
            {
                return event.getCancelReason();
            }

            SqlHelper.generatePlayer(username.toString());
            boolean worked = SqlHelper.setPermission(username.toString(), false, perm, zoneID);

            if (!worked)
            {
                return "An error has occurred processing your action. Please report this error to a server admin.";
            }
        }
        catch (Throwable t)
        {
            return t.getLocalizedMessage();
        }

        return null;
    }

    @Override
    public String setPlayerPermissionProp(UUID username, String permission, String value, String zoneID)
    {
        try
        {
            Zone zone = APIRegistry.zones.getZone(zoneID);
            if (zone == null)
            {
                return String.format("No zone by the name %s exists!", zoneID);
            }

            PermissionProp perm = new PermissionProp(permission, value);

            // send out permissions string.
            PermissionPropSetEvent event = new PermissionPropSetEvent(perm, zone, "p:" + username);
            if (MinecraftForge.EVENT_BUS.post(event))
            {
                return event.getCancelReason();
            }

            SqlHelper.generatePlayer(username.toString());
            boolean worked = SqlHelper.setPermProp(username.toString(), false, perm, zoneID);

            if (!worked)
            {
                return "An error has occurred processing your action. Please report this error to a server admin.";
            }
        }
        catch (Throwable t)
        {
            return t.getLocalizedMessage();
        }

        return null;
    }

    @Override
    public String setGroupPermission(String group, String permission, boolean allow, String zoneID)
    {
        try
        {
            Zone zone = APIRegistry.zones.getZone(zoneID);

            if (zone == null)
            {
                return String.format("No zone by the name %s exists!", zoneID);
            }

            Group g = SqlHelper.getGroupForName(group);
            if (g == null)
            {
                return String.format("No group of name %s exists!", group);
            }

            Permission perm = new Permission(permission, allow);

            // send out permissions string.
            PermissionSetEvent event = new PermissionSetEvent(perm, zone, "g:" + group);
            if (MinecraftForge.EVENT_BUS.post(event))
            {
                return event.getCancelReason();
            }

            boolean worked = SqlHelper.setPermission(group, true, perm, zoneID);

            if (!worked)
            {
                return "An error has occurred processing your action. Please report this error to a server admin.";
            }
        }
        catch (Throwable t)
        {
            return t.getMessage();
        }

        return null;
    }

    @Override
    public String setGroupPermissionProp(String group, String permission, String value, String zoneID)
    {
        try
        {
            Zone zone = APIRegistry.zones.getZone(zoneID);
            if (zone == null)
            {
                return String.format("No zone by the name %s exists!", zoneID);
            }

            Group g = SqlHelper.getGroupForName(group);
            if (g == null)
            {
                return String.format("No group of name %s exists!", group);
            }

            PermissionProp perm = new PermissionProp(permission, value);

            // send out permissions string.
            PermissionPropSetEvent event = new PermissionPropSetEvent(perm, zone, "g:" + group);
            if (MinecraftForge.EVENT_BUS.post(event))
            {
                return event.getCancelReason();
            }

            boolean worked = SqlHelper.setPermProp(group, true, perm, zoneID);

            if (!worked)
            {
                return "An error has occurred processing your action. Please report this error to a server admin.";
            }
        }
        catch (Throwable t)
        {
            return t.getLocalizedMessage();
        }

        return null;
    }

    @Override
    public ArrayList<Group> getApplicableGroups(EntityPlayer player, boolean includeDefaults)
    {
        Zone zone = APIRegistry.zones.getWhichZoneIn(new WorldPoint(player));

        return getApplicableGroups(player.getPersistentID(), includeDefaults, zone.getZoneName());
    }

    @Override
    public ArrayList<Group> getApplicableGroups(UUID player, boolean includeDefaults, String zoneID)
    {
        ArrayList<Group> list = new ArrayList<Group>();

        Zone zone = APIRegistry.zones.getZone(zoneID);

        while (zone != null)
        {
            list.addAll(SqlHelper.getGroupsForPlayer(player.toString(), zone.getZoneName()));

            if (zone == APIRegistry.zones.getGLOBAL())
            {
                zone = null;
            }
            else
            {
                zone = APIRegistry.zones.getZone(zone.parent);
            }
        }

        if (includeDefaults)
        {
            list.add(getDEFAULT());
        }

        return list;
    }

    @Override
    public ArrayList<String> getPlayersInGroup(String group, String zone)
    {
        return SqlHelper.getPlayersForGroup(group, zone);
    }

    @Override
    public Group getHighestGroup(EntityPlayer player)
    {
        Zone zone = APIRegistry.zones.getWhichZoneIn(new WorldPoint(player));
        TreeSet<Group> list = new TreeSet<Group>();

        ArrayList<Group> temp;
        while (zone != null && list.size() <= 0)
        {
            temp = SqlHelper.getGroupsForPlayer(player.getPersistentID().toString(), zone.getZoneName());

            if (!temp.isEmpty())
            {
                list.addAll(temp);
            }

            zone = APIRegistry.zones.getZone(zone.parent);
        }

        if (list.size() == 0)
        {
            return getDEFAULT();
        }
        else
        {
            return list.pollFirst();
        }
    }

    @Override
    public String setPlayerGroup(String group, UUID player, String zone)
    {
        SetPlayerGroupEvent event = new SetPlayerGroupEvent(group, player, zone);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            return event.getCancelReason();
        }

        SqlHelper.generatePlayer(player.toString());
        return SqlHelper.setPlayerGroup(group, player.toString(), zone);
    }

    @Override
    public String addPlayerToGroup(String group, UUID player, String zone)
    {
        SqlHelper.generatePlayer(player.toString());
        if (getApplicableGroups(player, false, zone).contains(APIRegistry.getAsFEGroup(group)))
        {
            return "Player already in group.";
        }
        else
        {
            AddPlayerGroupEvent event = new AddPlayerGroupEvent(group, player, zone);
            if (MinecraftForge.EVENT_BUS.post(event))
            {
                return event.getCancelReason();
            }

            return SqlHelper.addPlayerGroup(group, player.toString(), zone);
        }
    }

    @Override
    public String clearPlayerGroup(String group, UUID player, String zone)
    {
        RemovePlayerGroupEvent event = new RemovePlayerGroupEvent(group, player, zone);
        if (MinecraftForge.EVENT_BUS.post(event))
        {
            return event.getCancelReason();
        }

        SqlHelper.generatePlayer(player.toString());
        return SqlHelper.removePlayerGroup(group, player.toString(), zone);
    }

    @Override
    public String clearPlayerPermission(UUID player, String node, String zone)
    {
        SqlHelper.generatePlayer(player.toString());
        return SqlHelper.removePermission(player.toString(), false, node, zone);
    }

    @Override
    public String clearPlayerPermissionProp(UUID player, String node, String zone)
    {
        SqlHelper.generatePlayer(player.toString());
        return SqlHelper.removePermissionProp(player.toString(), false, node, zone);
    }

    @Override
    public void deleteGroupInZone(String group, String zone)
    {
        SqlHelper.deleteGroupInZone(group, zone);
    }

    @Override
    public boolean updateGroup(Group group)
    {
        return SqlHelper.updateGroup(group);
    }

    @Override
    public String clearGroupPermission(String name, String node, String zone)
    {
        return SqlHelper.removePermission(name, true, node, zone);
    }

    @Override
    public String clearGroupPermissionProp(String name, String node, String zone)
    {
        return SqlHelper.removePermissionProp(name, true, node, zone);
    }

    @Override
    public ArrayList<Group> getGroupsInZone(String zoneName)
    {
        return SqlHelper.getGroupsInZone(zoneName);
    }

    @Override
    public String getPermissionForPlayer(UUID target, String zone, String perm)
    {
        return SqlHelper.getPermission(target.toString(), false, perm, zone);
    }

    @Override
    public String getPermissionPropForPlayer(UUID target, String zone, String perm)
    {
        return SqlHelper.getPermissionProp(target.toString(), false, perm, zone);
    }

    @Override
    public String getPermissionForGroup(String target, String zone, String perm)
    {
        return SqlHelper.getPermission(target, true, perm, zone);
    }

    @Override
    public String getPermissionPropForGroup(String target, String zone, String perm)
    {
        return SqlHelper.getPermissionProp(target, true, perm, zone);
    }

    @Override
    public ArrayList getPlayerPermissions(UUID target, String zone)
    {
        ArrayList<String> output = new ArrayList<String>();

        if (zone == null)
        {
            output.add(String.format("No zone by the name %s exists!", zone));
        }
        else
        {
            for (Permission perm : SqlHelper.getAllPermissions(target.toString(), zone, false))
            {
                output.add(perm.toString());
            }
        }

        return output;
    }

    @Override
    public ArrayList getPlayerPermissionProps(UUID target, String zone)
    {
        ArrayList<String> output = new ArrayList<String>();

        if (zone == null)
        {
            output.add(String.format("No zone by the name %s exists!", zone));
        }
        else
        {
            for (Permission perm : SqlHelper.getAllPermissions(target.toString(), zone, true))
            {
                output.add(perm.toString());
            }
        }

        return output;
    }

    @Override
    public ArrayList<String> getGroupPermissions(String target, String zone)
    {
        ArrayList<String> output = new ArrayList<String>();
        Group g = SqlHelper.getGroupForName(target);

        if (zone == null)
        {
            output.add(String.format("No zone by the name %s exists!", zone));
        }
        else if (g == null)
        {
            output.add(String.format("No group of name %s exists!", target));
        }
        else
        {
            for (Permission perm : SqlHelper.getAllPermissions(target, zone, true))
            {
                output.add(perm.toString());
            }
        }

        return output;
    }

    @Override
    public ArrayList<String> getGroupPermissionProps(String target, String zone)
    {
        ArrayList<String> output = new ArrayList<String>();
        Group g = SqlHelper.getGroupForName(target);

        if (zone == null)
        {
            output.add(String.format("No zone by the name %s exists!", zone));
        }
        else if (g == null)
        {
            output.add(String.format("No group of name %s exists!", target));
        }
        else
        {
            for (Permission perm : SqlHelper.getAllPermissions(target, zone, true))
            {
                output.add(perm.toString());
            }
        }

        return output;
    }

    @Override
    public Group getDEFAULT()
    {
        return DEFAULT;
    }

    @Override public String getEPPrefix()
    {
        return entryPlayerPrefix;
    }

    @Override public void setEPPrefix(String ePPrefix)
    {
        entryPlayerPrefix = ePPrefix;
    }

    @Override public String getEPSuffix()
    {
        return entryPlayerSuffix;
    }

    @Override public void setEPSuffix(String ePSuffix)
    {
        entryPlayerSuffix = ePSuffix;
    }

    @Override public UUID getEntryPlayer()
    {
        return ENTRY_PLAYER_NAME;
    }
}
