package com.forgeessentials.permissions.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.GroupEntry;
import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.api.permissions.Zone.PermissionList;
import com.forgeessentials.commons.selections.AreaBase;
import com.forgeessentials.commons.selections.AreaShape;
import com.forgeessentials.permissions.core.ZonePersistenceProvider;
import com.forgeessentials.util.ServerUtil;
import com.forgeessentials.util.output.LoggingHandler;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonProvider extends ZonePersistenceProvider
{
    private File path;
    private Gson gson;

    public JsonProvider()
    {
        path = new File(ServerUtil.getWorldPath(), "FEData/json/Permissions");
        gson = new GsonBuilder().disableHtmlEscaping().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).setPrettyPrinting().create();
    }

    @Override
    public ServerZone load()
    {
        ServerZone serverZone = new ServerZone();
        loadGroups(serverZone);
        loadUsers(serverZone);
        loadWorlds(serverZone);
        return serverZone;
    }

    private void loadGroups(ServerZone serverZone)
    {
        GroupsData groupsData = null;
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(path + "/groups.json"));
            groupsData = gson.fromJson(reader, GroupsData.class);
        }
        catch (IOException e)
        {
            LoggingHandler.felog.error("Could not load groups data: " + e.getMessage());
            return;
        }
        for (String group : groupsData.groups.keySet())
        {
            GroupData groupData = groupsData.groups.get(group);
            PermissionList list = PermissionList.fromList(groupData.permissions);
            list.put(FEPermissions.PREFIX, groupData.prefix);
            list.put(FEPermissions.SUFFIX, groupData.suffix);
            list.put(FEPermissions.GROUP_PRIORITY, Integer.toString(groupData.priority));
            list.put(FEPermissions.GROUP, Zone.PERMISSION_TRUE);
            if (groupData.Default)
            {
                list.put("fe.internal.group.default", Zone.PERMISSION_TRUE);
            }
            serverZone.getOrCreateGroupPermissions(group).putAll(list);
        }
    }

    private void loadUsers(ServerZone serverZone)
    {
        UsersData usersData = null;
        try
        {
            BufferedReader reader = new BufferedReader(new FileReader(path + "/users.json"));
            usersData = gson.fromJson(reader, UsersData.class);
        }
        catch (IOException e)
        {
            LoggingHandler.felog.error("Could not load groups data: " + e.getMessage());
            return;
        }
        for (String uuid : usersData.users.keySet())
        {
            UserData userData = usersData.users.get(uuid);
            UserIdent ident = UserIdent.get(uuid, userData.username);
            PermissionList list = PermissionList.fromList(userData.permissions);
            list.put(FEPermissions.PREFIX, userData.prefix);
            list.put(FEPermissions.SUFFIX, userData.suffix);
            serverZone.getOrCreatePlayerPermissions(ident).putAll(list);
            for (String group : userData.groups)
            {
                serverZone.addPlayerToGroup(ident, group);
            }
        }
    }

    private void loadWorlds(ServerZone serverZone)
    {
        List<File> files = new ArrayList<File>();
        Path p = FileSystems.getDefault().getPath(path.getAbsolutePath());
        try (DirectoryStream<Path> ds = Files.newDirectoryStream(p, "world_*.json"))
        {
            for (Path p2 : ds)
            {
                files.add(p2.toFile());
            }
        }
        catch (IOException e)
        {
            LoggingHandler.felog.error("Failed to get list of world files: " + e.getMessage());
        }
        for (File file : files)
        {
            WorldZoneData wzd = null;
            try
            {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                wzd = gson.fromJson(reader, WorldZoneData.class);
            }
            catch (IOException e)
            {
                LoggingHandler.felog.error(String.format("Failed to load world file &s: &s", file.getName(), e.getMessage()));
                return;
            }
            WorldZone wz = new WorldZone(serverZone, wzd.dimId, wzd.id);
            for (String group : wzd.groups.keySet())
            {
                wz.getGroupPermissions().put(group, PermissionList.fromList(wzd.groups.get(group).permissions));
            }
            for (String uuid : wzd.players.keySet())
            {
                wz.getPlayerPermissions().put(UserIdent.get(uuid, wzd.players.get(uuid).username), PermissionList.fromList(wzd.players.get(uuid).permissions));
            }
            for (AreaZoneData azd : wzd.zones)
            {
                AreaZone az = new AreaZone(wz, azd.name, azd.area, azd.id);
                az.setHidden(azd.hidden);
                az.setShape(azd.shape);
                az.setPriority(azd.priority);
                for (String group : azd.groups.keySet())
                {
                    az.getGroupPermissions().put(group, PermissionList.fromList(azd.groups.get(group).permissions));
                }
                for (String uuid : azd.players.keySet())
                {
                    az.getPlayerPermissions().put(UserIdent.get(uuid, azd.players.get(uuid).username),
                            PermissionList.fromList(azd.players.get(uuid).permissions));
                }
            }
        }
    }

    @Override
    public void save(ServerZone serverZone)
    {
        path.mkdirs();
        saveGroups(serverZone);
        saveUsers(serverZone);
        saveWorlds(serverZone);
    }

    public void saveGroups(ServerZone zone)
    {
        GroupsData groupsData = new GroupsData();
        groupsData.groups.putAll(getGroupDataMap(zone, zone.getGroups()));
        String json = gson.toJson(groupsData);

        try
        {
            FileWriter globalGroups = new FileWriter(new File(path + "/groups.json"));
            globalGroups.write(json);
            globalGroups.close();
        }
        catch (IOException e)
        {
            LoggingHandler.felog.error("Failed to save groups.json: " + e.getMessage());
        }
    }

    public void saveUsers(ServerZone serverZone)
    {
        File newPath = new File(path + "/users.json");
        UsersData usersData = new UsersData();
        usersData.users.putAll(getUserDataMap(serverZone, serverZone.getPlayerGroups().keySet()));
        String json = gson.toJson(usersData);

        try
        {
            FileWriter writer = new FileWriter(newPath);
            writer.write(json);
            writer.close();
        }
        catch (IOException e)
        {
            LoggingHandler.felog.error("Failed to save users.json: " + e.getMessage());
        }
    }

    public void saveWorlds(ServerZone serverZone)
    {
        for (Entry<Integer, WorldZone> wzEntry : serverZone.getWorldZones().entrySet())
        {
            Integer wzDimId = wzEntry.getKey();
            WorldZone wz = wzEntry.getValue();
            String pathName = String.format("/world_%d.json", wzDimId);
            File newPath = new File(path + pathName);
            WorldZoneData worldZoneData = new WorldZoneData(wz.getId(), wzDimId);
            worldZoneData.groups.putAll(getGroupDataMap(wz, wz.getGroupPermissions().keySet()));
            worldZoneData.players.putAll(getUserDataMap(wz, wz.getPlayerPermissions().keySet()));
            for (AreaZone az : wz.getAreaZones())
            {
                AreaZoneData areaZoneData = new AreaZoneData(az.getId(), az.getPriority(), az.isHidden(), az.getName(), az.getArea(), az.getShape());
                areaZoneData.groups.putAll(getGroupDataMap(az, az.getGroupPermissions().keySet()));
                areaZoneData.players.putAll(getUserDataMap(az, az.getPlayerPermissions().keySet()));
                worldZoneData.zones.add(areaZoneData);
            }
            String json = gson.toJson(worldZoneData);
            try
            {
                FileWriter writer = new FileWriter(newPath);
                writer.write(json);
                writer.close();
            }
            catch (IOException e)
            {
                LoggingHandler.felog.error(String.format("Failed to save world_%d.json: %s", wzDimId, e.getMessage()));
            }
        }
    }

    // public void removeInternals(List<String> list)
    // {
    // ArrayList<String> remove = new ArrayList<String>();
    // for(String perm : list)
    // {
    // if(perm.contains("fe.internal"))
    // remove.add(perm);
    // }
    // list.removeAll(remove);
    // }

    private Map<String, GroupData> getGroupDataMap(Zone zone, Set<String> groups)
    {
        boolean global = zone instanceof ServerZone;
        Map<String, GroupData> groupDataMap = new HashMap<String, GroupData>();
        for (String group : groups)
        {
            PermissionList groupList = new PermissionList(zone.getGroupPermissions(group));
            String prefix = groupList.remove(FEPermissions.PREFIX);
            if (prefix == null)
                prefix = "";
            String suffix = groupList.remove(FEPermissions.SUFFIX);
            if (suffix == null)
                suffix = "";

            Boolean Default = Boolean.getBoolean(groupList.remove("fe.internal.group.default"));
            Integer priority = null;
            String prioNode = groupList.remove(FEPermissions.GROUP_PRIORITY);
            if (prioNode != null)
            {
                priority = Integer.parseInt(groupList.remove(FEPermissions.GROUP_PRIORITY));
            }

            groupList.remove(FEPermissions.GROUP);
            if (!global)
            {
                prefix = null;
                suffix = null;
                priority = null;
                Default = null;
            }
            List<String> list = groupList.toList();
            GroupData groupData = new GroupData(prefix, suffix, Default, priority);
            groupData.permissions.addAll(list);
            groupDataMap.put(group, groupData);
        }
        return groupDataMap;
    }

    private Map<String, UserData> getUserDataMap(Zone zone, Set<UserIdent> users)
    {
        boolean global = zone instanceof ServerZone;
        Map<String, UserData> userDataMap = new HashMap<String, UserData>();
        for (UserIdent user : users)
        {
            String uuid = (user.getUuid() == null ? null : user.getUuid().toString());
            String username = user.getUsername();
            PermissionList permList = new PermissionList(zone.getPlayerPermissions(user));
            String prefix = permList.remove(FEPermissions.PREFIX);
            if (prefix == null)
                prefix = "";
            String suffix = permList.remove(FEPermissions.SUFFIX);
            if (suffix == null)
                suffix = "";

            if (uuid == null)
            {
                uuid = user.getUsername();
                username = null;
            }
            if (!global)
            {
                prefix = null;
                suffix = null;
            }
            List<String> list = permList.toList();
            UserData userData = new UserData(username, prefix, suffix);
            userData.permissions.addAll(list);
            if (global)
            {
                userData.groups.addAll(GroupEntry.toList(((ServerZone) zone).getPlayerGroups(user)));
            }
            else
            {
                userData.groups = null;
            }
            userDataMap.put(uuid, userData);
        }
        return userDataMap;
    }

    // helper classes
    public static class GroupsData
    {
        public Map<String, GroupData> groups;

        public GroupsData()
        {
            groups = new HashMap<String, GroupData>();
        }
    }

    public static class GroupData
    {
        public String prefix;
        public String suffix;
        public Boolean Default;
        public Integer priority;
        public List<String> permissions;

        public GroupData(String prefix, String suffix, Boolean Default, Integer priority)
        {
            this.prefix = prefix;
            this.suffix = suffix;
            this.Default = Default;
            this.priority = priority;
            permissions = new ArrayList<String>();
        }
    }

    public static class GroupMembers
    {
        public String name;
        public List<String> members;

        public GroupMembers(String name)
        {
            this.name = name;
            members = new ArrayList<String>();
        }
    }

    public static class WorldZoneData
    {
        public int id, dimId;
        public List<AreaZoneData> zones;
        public Map<String, GroupData> groups;
        public Map<String, UserData> players;

        public WorldZoneData(int id, int dimId)
        {
            this.id = id;
            groups = new HashMap<String, GroupData>();
            players = new HashMap<String, UserData>();
            zones = new ArrayList<AreaZoneData>();
        }
    }

    public static class AreaZoneData
    {
        public int id;
        public String name;
        public boolean hidden;
        public int priority;
        public Map<String, GroupData> groups;
        public Map<String, UserData> players;
        public AreaBase area;
        public AreaShape shape;

        public AreaZoneData(int id, int priority, boolean hidden, String name, AreaBase area, AreaShape shape)
        {
            this.id = id;
            this.priority = priority;
            this.hidden = hidden;
            this.name = name;
            this.area = area;
            this.shape = shape;
            groups = new HashMap<String, GroupData>();
            players = new HashMap<String, UserData>();
        }
    }

    public static class UsersData
    {
        public Map<String, UserData> users;

        public UsersData()
        {
            users = new HashMap<String, UserData>();
        }
    }

    public static class UserData
    {
        public String username;
        public String prefix;
        public String suffix;
        public List<String> groups;
        public List<String> permissions;

        public UserData(String username, String prefix, String suffix)
        {
            this.username = username;
            this.prefix = prefix;
            this.suffix = suffix;
            groups = new ArrayList<String>();
            permissions = new ArrayList<String>();
        }
    }
}
