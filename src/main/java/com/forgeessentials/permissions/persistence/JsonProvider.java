package com.forgeessentials.permissions.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.RootZone;
import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.api.permissions.Zone.PermissionList;
import com.forgeessentials.permissions.core.ZonePersistenceProvider;
import com.forgeessentials.util.OutputHandler;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonProvider extends ZonePersistenceProvider {

    private File path;

    private Gson gson;

    public JsonProvider(File path)
    {
        this.path = path;
        gson = new GsonBuilder().disableHtmlEscaping().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).setPrettyPrinting().create();
    }

    @Override
    public ServerZone load()
    {
        OutputHandler.felog.info("Loading Json ServerZone");
        ServerZone serverZone = new ServerZone();
        loadGroups(serverZone);
        return serverZone;
    }

    private void loadGroups(ServerZone serverZone)
    {
        try (BufferedReader reader = new BufferedReader(new FileReader(path + "/groups.json")))
        {
            GroupsData groups = gson.fromJson(reader, GroupsData.class);
            for (Entry<String, GroupData> group : groups.groups.entrySet())
            {
                for (Entry<Integer, ZonePerms> zonePerms : group.getValue().permissions.entrySet())
                {
                    Zone zone = serverZone.getZoneMap().get(zonePerms.getKey());
                    if (zone == null)
                        continue;
                    zone.getOrCreateGroupPermissions(group.getKey()).putAll(PermissionList.fromList(zonePerms.getValue().permissions));
                }
                serverZone.setGroupPermissionProperty(group.getKey(), FEPermissions.PREFIX, group.getValue().prefix);
                serverZone.setGroupPermissionProperty(group.getKey(), FEPermissions.SUFFIX, group.getValue().suffix);
                serverZone.setGroupPermissionProperty(group.getKey(), FEPermissions.GROUP_PRIORITY, group.getValue().priority == null ? null
                        : group.getValue().priority.toString());
            }
        }
        catch (IOException e)
        {
            OutputHandler.felog.severe("Could not load groups data: " + e.getMessage());
            return;
        }
    }

    @Override
    public void save(ServerZone serverZone)
    {
        saveGroups(serverZone);
        saveWorlds(serverZone);
    }

    private static Integer tryParseInt(String value)
    {
        try
        {
            return Integer.parseInt(value);
        }
        catch (NumberFormatException e)
        {
            return null;
        }
    }

    public void saveGroups(ServerZone serverZone)
    {
        GroupsData groups = new GroupsData();
        for (String group : serverZone.getGroups())
        {
            GroupData groupData = new GroupData();
            for (Zone zone : serverZone.getZones())
            {
                if (zone instanceof RootZone)
                    continue;
                PermissionList groupPermissions = serverZone.getGroupPermissions(group);
                if (zone instanceof ServerZone)
                {
                    groupPermissions = new PermissionList(groupPermissions);
                    groupData.prefix = groupPermissions.remove(FEPermissions.PREFIX);
                    groupData.suffix = groupPermissions.remove(FEPermissions.SUFFIX);
                    groupData.priority = tryParseInt(groupPermissions.remove(FEPermissions.GROUP_PRIORITY));
                }
                groupData.permissions.put(zone.getId(), new ZonePerms(zone.getName(), groupPermissions.toList()));
            }
            groups.groups.put(group, groupData);
        }

        path.mkdirs();
        try (FileWriter globalGroups = new FileWriter(new File(path + "/groups.json")))
        {
            String json = gson.toJson(groups);
            globalGroups.write(json);
        }
        catch (IOException e)
        {
            OutputHandler.felog.severe("Failed to save groups.json: " + e.getMessage());
        }
    }

    public void saveWorlds(ServerZone serverZone)
    {
        // WorldData worldData = new WorldData();
    }

    // ------------------------------------------------------------
    // Helper classes

    public static class GroupsData {
        public Map<String, GroupData> groups = new TreeMap<>();
    }

    public static class GroupData {
        
        public String prefix;
        
        public String suffix;
        
        public Integer priority;
        
        public Map<Integer, ZonePerms> permissions = new TreeMap<>();
        
    }

    public static class ZonePerms {
        
        public String name_reference;
        
        List<String> permissions;

        public ZonePerms(String name, List<String> permissions)
        {
            this.name_reference = name;
            this.permissions = permissions;
        }
    }

    public static class WorldData {
    }

    public static class UsersData {
        
        public Map<String, UserData> users;

        public UsersData(Map<String, UserData> users)
        {
            users = new HashMap<String, UserData>();
        }
        
    }

    public static class UserData {

        public String username;
        
        public String prefix;
        
        public String suffix;
        
        public List<String> groups;
        
        public List<ZonePerms> zones;

        public UserData(String username, String prefix, String suffix, List<String> groups, List<ZonePerms> zones)
        {
            this.username = username;
            this.prefix = prefix;
            this.suffix = suffix;
            groups = new ArrayList<String>();
            zones = new ArrayList<ZonePerms>();
        }
    }
}
