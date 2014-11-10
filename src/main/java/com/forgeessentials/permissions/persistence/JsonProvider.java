package com.forgeessentials.permissions.persistence;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.api.permissions.Zone.PermissionList;
import com.forgeessentials.permissions.core.ZonePersistenceProvider;
import com.forgeessentials.util.OutputHandler;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonProvider extends ZonePersistenceProvider {
	
	private File path;
	
	public JsonProvider(File path)
	{
		this.path = path;		
	}

	@Override
	public ServerZone load()
	{
		return null;
	}

	@Override
	public void save(ServerZone serverZone)
	{
		Gson gson = new GsonBuilder()
				.disableHtmlEscaping()
				.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE)
				.setPrettyPrinting()
				.create();
		saveGroups(gson, serverZone);
	}
	
	public void saveGroups(Gson gson, ServerZone zone)
	{
		GroupsData groupsData = new GroupsData();
		for(String group : zone.getGroups())
		{
			PermissionList groupList = zone.getGroupPermissions(group);
			String prefix = groupList.get(FEPermissions.PREFIX);
			String suffix = groupList.get(FEPermissions.SUFFIX);
			boolean Default = Boolean.getBoolean(groupList.get("fe.internal.group.default"));
			int priority = Integer.parseInt(groupList.get(FEPermissions.GROUP_PRIORITY));
			List<String> list = groupList.getPermissionsListFromMap();
			GroupData groupData = new GroupData(prefix, suffix, Default, priority);
			ZonePerms zonePerms = new ZonePerms(zone.getId(), zone.getName(), zone.getParent().getName());
			zonePerms.permissions.addAll(list);
			groupData.zones.add(zonePerms);
			groupsData.groups.put(group, groupData);
		}
		String json = gson.toJson(groupsData);
		
		path.mkdirs();
		
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
		public List<ZonePerms> zones;
		
		public GroupData(String prefix, String suffix, boolean Default, int priority)
		{
			this.prefix = prefix;
			this.suffix = suffix;
			this.Default = Default;
			this.priority = priority;
			zones = new ArrayList<ZonePerms>();
		}
	}
	
	public static class ZonePerms
	{
		public int id;
		public String name;
		public String parent;
		List<String> permissions;
		
		public ZonePerms(int id, String name, String parent)
		{
			this.id = id;
			this.name = name;
			this.parent = parent;
			permissions = new ArrayList<String>();
		}
	}
	
	public static class UsersData
	{
		public Map<String, UserData> users;

		public UsersData(Map<String, UserData> users)
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
