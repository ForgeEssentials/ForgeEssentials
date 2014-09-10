package com.forgeessentials.permissions.persistance;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map.Entry;

import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.api.permissions.Zone.PermissionList;
import com.forgeessentials.permissions.core.ZonePersistanceProvider;
import com.forgeessentials.util.UserIdent;

public class FlatfileProvider extends ZonePersistanceProvider {

	private File path;

	public FlatfileProvider(File path)
	{
		this.path = path;
	}

	@Override
	public void save(ServerZone serverZone)
	{
		saveZonePermissions(path, serverZone);
		for (WorldZone worldZone : serverZone.getWorldZones().values())
		{
			File worldPath = new File(path, worldZone.getName());
			saveZonePermissions(worldPath, worldZone);
			for (AreaZone areaZone : worldZone.getAreaZones())
			{
				File areaPath = new File(worldPath, areaZone.getName());
				saveZonePermissions(worldPath, worldZone);
			}
		}
	}

	public void saveZonePermissions(File path, Zone zone)
	{
		for (Entry<UserIdent, PermissionList> entry : zone.getPlayerPermissions())
		{
			try
			{
				path.mkdirs();
				BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path, "USER_" + entry.getKey().toString() + ".txt")));
				for (Entry<String, String> permission : entry.getValue().entrySet())
				{
					writer.write(permission.getKey() + "=" + permission.getValue());
					writer.newLine();
				}
				writer.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}

		for (Entry<String, PermissionList> entry : zone.getGroupPermissions())
		{
			try
			{
				path.mkdirs();
				BufferedWriter writer = new BufferedWriter(new FileWriter(new File(path, "GROUP_" + entry.getKey().replaceAll("\\*", "_ALL_") + ".txt")));
				for (Entry<String, String> permission : entry.getValue().entrySet())
				{
					writer.write(permission.getKey() + "=" + permission.getValue());
					writer.newLine();
				}
				writer.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	@Override
	public ServerZone load()
	{
		return null;
	}

}
