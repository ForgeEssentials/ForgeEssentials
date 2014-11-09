package com.forgeessentials.permissions.persistence;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.h2.store.fs.FileUtils;

import com.forgeessentials.api.permissions.FEPermissions;
import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.api.permissions.Zone.PermissionList;
import com.forgeessentials.permissions.core.ZonePersistenceProvider;
import com.forgeessentials.permissions.persistence.JsonSerializerHelpers.GroupData;
import com.forgeessentials.permissions.persistence.JsonSerializerHelpers.GroupsData;
import com.forgeessentials.permissions.persistence.JsonSerializerHelpers.ZonePerms;
import com.forgeessentials.util.OutputHandler;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class JsonProvider extends ZonePersistenceProvider {
	
	private File path;
	
	public JsonProvider(File path)
	{
		this.path = path;
		if(!path.exists())
			FileUtils.createDirectory(path.getPath());			
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
}
