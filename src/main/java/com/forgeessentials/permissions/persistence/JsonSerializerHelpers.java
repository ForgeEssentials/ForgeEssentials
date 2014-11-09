package com.forgeessentials.permissions.persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonSerializerHelpers
{
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
