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

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.IPermissionsHelper;
import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.api.permissions.Zone.PermissionList;
import com.forgeessentials.permissions.core.ZonePersistenceProvider;
import com.forgeessentials.permissions.core.ZonedPermissionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonProvider extends ZonePersistenceProvider
{
    private File path;
    private Gson gson;
    
    public JsonProvider(File path)
    {
        this.path = path;
        gson = new GsonBuilder()
                .disableHtmlEscaping()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .setPrettyPrinting()
                .create();
    }

    @Override
    public ServerZone load()
    {
        ServerZone serverZone = new ServerZone();
        loadGroups(serverZone);
        loadUsers(serverZone);
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
            OutputHandler.felog.severe("Could not load groups data: " + e.getMessage());
            return;
        }
        for(String group : groupsData.groups.keySet())
        {
            GroupData groupData = groupsData.groups.get(group);
            PermissionList list = PermissionList.fromList(groupData.permissions);
            list.put(FEPermissions.PREFIX, groupData.prefix);
            list.put(FEPermissions.SUFFIX, groupData.suffix);
            list.put(FEPermissions.GROUP_PRIORITY, Integer.toString(groupData.priority));
        	list.put(FEPermissions.GROUP, ZonedPermissionHelper.PERMISSION_TRUE);
            if(groupData.Default)
            {
                list.put("fe.internal.group.default", IPermissionsHelper.PERMISSION_TRUE);
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
            OutputHandler.felog.severe("Could not load groups data: " + e.getMessage());
            return;
        }
        for(String uuid : usersData.users.keySet())
        {
            UserData userData = usersData.users.get(uuid);
            UserIdent ident = new UserIdent(uuid, userData.username);
            PermissionList list = PermissionList.fromList(userData.permissions);
            list.put(FEPermissions.PREFIX, userData.prefix);
            list.put(FEPermissions.SUFFIX, userData.suffix);
            serverZone.getOrCreatePlayerPermissions(ident).putAll(list);
            for(String group : userData.groups)
            {
                serverZone.addPlayerToGroup(ident, group);
            }
        }
    }

    @Override
    public void save(ServerZone serverZone)
    {
        path.mkdirs();
        saveGroups(serverZone);
        saveUsers(serverZone);
    }
    
    public void saveGroups(ServerZone zone)
    {
        GroupsData groupsData = new GroupsData();
        for(String group : zone.getGroups())
        {
            PermissionList groupList = new PermissionList(zone.getGroupPermissions(group));
            String prefix = groupList.remove(FEPermissions.PREFIX);
            if(prefix == null)
                prefix = "";
            String suffix = groupList.remove(FEPermissions.SUFFIX);
            if(suffix == null)
                suffix = "";
            
            boolean Default = Boolean.getBoolean(groupList.remove("fe.internal.group.default"));
            int priority = Integer.parseInt(groupList.remove(FEPermissions.GROUP_PRIORITY));
            groupList.remove(FEPermissions.GROUP);
            List<String> list = groupList.toList();
            GroupData groupData = new GroupData(prefix, suffix, Default, priority);
            groupData.permissions.addAll(list);
            groupsData.groups.put(group, groupData);
        }
        String json = gson.toJson(groupsData);
        
        try
        {
            FileWriter globalGroups = new FileWriter(new File(path + "/groups.json"));
            globalGroups.write(json);
            globalGroups.close();
        }
        catch(IOException e)
        {
            OutputHandler.felog.severe("Failed to save groups.json: " + e.getMessage());
        }
    }
    
    public void saveUsers(ServerZone serverZone)
    {
        File newPath = new File(path + "/users.json");
        UsersData usersData = new UsersData();
        for(UserIdent user : serverZone.getPlayerPermissions().keySet())
        {
            String uuid = user.getUuid().toString();
            String username = user.getUsername();
            PermissionList permList = new PermissionList(serverZone.getPlayerPermissions(user));
            String prefix = permList.remove(FEPermissions.PREFIX);
            if(prefix == null)
                prefix = "";
            String suffix = permList.remove(FEPermissions.SUFFIX);
            if(suffix == null)
                suffix = "";
            
            if(uuid == null)
            {
                uuid = user.getUsername();
                username = null;
            }
            List<String> list = permList.toList();
            UserData userData = new UserData(username, prefix, suffix);
            userData.permissions.addAll(list);
            userData.groups.addAll(serverZone.getPlayerGroups(user));
            usersData.users.put(uuid, userData);
        }
        String json = gson.toJson(usersData);
        
        try
        {
            FileWriter writer = new FileWriter(newPath);
            writer.write(json);
            writer.close();
        }
        catch(IOException e)
        {
            OutputHandler.felog.severe("Failed to save users.json: " + e.getMessage());
        }
    }
    
    public void removeInternals(List<String> list)
    {
    	ArrayList<String> remove = new ArrayList<String>();
        for(String perm : list)
        {
            if(perm.contains("fe.internal"))
                remove.add(perm);
        }
        list.removeAll(remove);
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
        public boolean Default;
        public int priority;
        public List<String> permissions;
        
        public GroupData(String prefix, String suffix, boolean Default, int priority)
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
    
    public static class ZonePerms
    {
        public int id;
        public String name;
        public String parent;
        Map<String, List<String>> groupPerms;
        Map<String, List<String>> userPerms;
        
        public ZonePerms(int id, String name, String parent)
        {
            this.id = id;
            this.name = name;
            this.parent = parent;
            groupPerms = new HashMap<String, List<String>>();
            userPerms = new HashMap<String, List<String>>();
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
