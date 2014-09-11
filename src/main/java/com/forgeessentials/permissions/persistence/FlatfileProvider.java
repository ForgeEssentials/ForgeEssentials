package com.forgeessentials.permissions.persistence;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.IPermissionsHelper;
import com.forgeessentials.api.permissions.ServerZone;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.api.permissions.Zone.PermissionList;
import com.forgeessentials.permissions.core.IZonePersistenceProvider;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;
import com.forgeessentials.util.selections.AreaBase;
import com.forgeessentials.util.selections.Point;

public class FlatfileProvider implements IZonePersistenceProvider {

	public static final String PERMISSION_FILE_EXT = ".txt";

	private File basePath;

	public static final FileFilter permissionFilter = new FileFilters.Extension(PERMISSION_FILE_EXT);

	public static final FileFilter directoryFilter = new FileFilters.Directory();

	// ------------------------------------------------------------

	public FlatfileProvider(File basePath)
	{
		this.basePath = basePath;
	}

	// ------------------------------------------------------------
	// -- Saving
	// ------------------------------------------------------------

	@Override
	public void save(ServerZone serverZone)
	{
		File path = basePath;
		try
		{
			FileUtils.cleanDirectory(path);
		}
		catch (IOException e)
		{
		}

		saveServerZone(path, serverZone);
		saveZonePermissions(path, serverZone);
		for (WorldZone worldZone : serverZone.getWorldZones().values())
		{
			File worldPath = new File(path, worldZone.getName());
			saveWorldZone(worldPath, worldZone);
			saveZonePermissions(worldPath, worldZone);
			for (AreaZone areaZone : worldZone.getAreaZones())
			{
				File areaPath = new File(worldPath, areaZone.getName());
				saveAreaZone(areaPath, areaZone);
				saveZonePermissions(worldPath, worldZone);
			}
		}
	}

	public static void saveServerZone(File path, ServerZone serverZone)
	{
		try
		{
			path.mkdirs();

			Properties p = new Properties();

			p.setProperty("id", Integer.toString(serverZone.getId()));
			p.setProperty("maxZoneId", Integer.toString(serverZone.getNextZoneID()));

			p.storeToXML(new BufferedOutputStream(new FileOutputStream(new File(path, "server.xml"))), "Data of server");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void saveWorldZone(File path, WorldZone worldZone)
	{
		try
		{
			path.mkdirs();

			Properties p = new Properties();

			p.setProperty("id", Integer.toString(worldZone.getId()));
			p.setProperty("dimId", Integer.toString(worldZone.getDimensionID()));

			p.storeToXML(new BufferedOutputStream(new FileOutputStream(new File(path, "world.xml"))), "Data of world " + worldZone.getName());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void saveAreaZone(File path, AreaZone areaZone)
	{
		try
		{
			path.mkdirs();

			Properties p = new Properties();

			p.setProperty("id", Integer.toString(areaZone.getId()));
			p.setProperty("name", areaZone.getName());
			p.setProperty("x1", Integer.toString(areaZone.getArea().getLowPoint().getX()));
			p.setProperty("y1", Integer.toString(areaZone.getArea().getLowPoint().getY()));
			p.setProperty("z1", Integer.toString(areaZone.getArea().getLowPoint().getZ()));
			p.setProperty("x2", Integer.toString(areaZone.getArea().getHighPoint().getX()));
			p.setProperty("y2", Integer.toString(areaZone.getArea().getHighPoint().getY()));
			p.setProperty("z2", Integer.toString(areaZone.getArea().getHighPoint().getZ()));

			p.storeToXML(new BufferedOutputStream(new FileOutputStream(new File(path, "area.xml"))), "Data of area " + areaZone.getName());
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void saveZonePermissions(File path, Zone zone)
	{
		File playersPath = new File(path, "players");
		File groupsPath = new File(path, "groups");
		for (Entry<UserIdent, PermissionList> entry : zone.getPlayers())
		{
			// Get filename and info
			String username = entry.getKey().getUsername() == null ? entry.getKey().getUuid().toString() : entry.getKey().getUsername();
			UUID uuid = entry.getKey().getUuid();
			String userIdentification = username == null ? uuid.toString() : username;
			String comment = "Permissions for user " + (username != null ? username : "<unknown-username>") + " with UUID "
					+ (uuid != null ? uuid.toString() : "<unknown-uuid>");
			userIdentification = userIdentification.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");

			// Save permissions
			Properties p = permissionListToProperties(entry.getValue());
			p.setProperty("fe.internal.player.username", entry.getKey().getUsername() == null ? null : entry.getKey().getUsername());
			p.setProperty("fe.internal.player.uuid", entry.getKey().getUuid() == null ? null : entry.getKey().getUuid().toString());
			saveProperties(p, playersPath, userIdentification + PERMISSION_FILE_EXT, comment);
		}
		for (Entry<String, PermissionList> entry : zone.getGroups())
		{
			// Get filename and info
			String groupName = entry.getKey();
			if (groupName.equals(IPermissionsHelper.PERMISSION_ASTERIX))
				groupName = IPermissionsHelper.PERMISSION_ALL;
			String comment = "Permissions for group " + entry.getKey();

			// Save permissions
			Properties p = permissionListToProperties(entry.getValue());
			// p.setProperty("fe.internal.group.id", 0);
			saveProperties(p, groupsPath, groupName + PERMISSION_FILE_EXT, comment);
		}
	}

	public static Properties permissionListToProperties(PermissionList list)
	{
		Properties p = new Properties();
		for (Entry<String, String> permission : list.entrySet())
			p.setProperty(permission.getKey(), permission.getValue());
		return p;
	}

	public static void saveProperties(Properties properties, File path, String filename, String comment)
	{
		try
		{
			path.mkdirs();
			properties.store(new BufferedOutputStream(new FileOutputStream(new File(path, filename))), comment);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	// ------------------------------------------------------------
	// -- Loading
	// ------------------------------------------------------------

	@Override
	public ServerZone load()
	{
		File path = basePath;
		try
		{
			// Create ServerZone and load permissions
			ServerZone serverZone = new ServerZone();
			loadZonePermissions(path, serverZone);

			int maxId = 2;

			for (File worldPath : path.listFiles(directoryFilter))
			{
				try
				{
					File worldFile = new File(worldPath, "world.xml");
					if (!worldFile.exists())
						continue;
					Properties worldProperties = new Properties();
					worldProperties.loadFromXML(new BufferedInputStream(new FileInputStream(worldFile)));

					// Read world data
					int worldId = Integer.parseInt(worldProperties.getProperty("id"));
					maxId = Math.max(maxId, worldId);

					int dimensionID = Integer.parseInt(worldProperties.getProperty("dimId"));

					// Create WorldZone and load permissions
					WorldZone worldZone = new WorldZone(serverZone, dimensionID, worldId);
					loadZonePermissions(worldPath, worldZone);

					for (File areaPath : worldPath.listFiles(directoryFilter))
					{
						try
						{
							File areaFile = new File(areaPath, "area.xml");
							if (!areaFile.exists())
								continue;
							Properties areaProperties = new Properties();
							areaProperties.loadFromXML(new BufferedInputStream(new FileInputStream(areaFile)));

							// Read area data
							int areaId = Integer.parseInt(areaProperties.getProperty("id"));
							maxId = Math.max(maxId, areaId);

							String name = areaProperties.getProperty("name");
							int x1 = Integer.parseInt(areaProperties.getProperty("x1"));
							int y1 = Integer.parseInt(areaProperties.getProperty("y1"));
							int z1 = Integer.parseInt(areaProperties.getProperty("z1"));
							int x2 = Integer.parseInt(areaProperties.getProperty("x2"));
							int y2 = Integer.parseInt(areaProperties.getProperty("y2"));
							int z2 = Integer.parseInt(areaProperties.getProperty("z2"));
							if (name == null)
								throw new IllegalArgumentException();

							// Create AreaZone and load permissions
							AreaZone areaZone = new AreaZone(worldZone, name, new AreaBase(new Point(x1, y1, z1), new Point(x2, y2, z2)), areaId);
							loadZonePermissions(areaPath, areaZone);
						}
						catch (IllegalArgumentException | IOException e)
						{
							OutputHandler.felog.severe("Error reading area " + worldPath.getName() + "/" + areaPath.getName());
						}
					}
				}
				catch (NumberFormatException | IOException e)
				{
					OutputHandler.felog.severe("Error reading world " + worldPath.getName());
				}
			}

			File serverFile = new File(path, "server.xml");
			if (serverFile.exists())
			{
				try
				{
					Properties serverProperties = new Properties();
					serverProperties.loadFromXML(new BufferedInputStream(new FileInputStream(serverFile)));
					serverZone.setMaxZoneId(Integer.parseInt(serverProperties.getProperty("maxZoneId")));
				}
				catch (IllegalArgumentException | IOException e)
				{
					OutputHandler.felog.severe("Error reading server data " + serverFile.getName());
					serverZone.setMaxZoneId(maxId);
				}
			}
			else
			{
				serverZone.setMaxZoneId(maxId);
			}

			return serverZone;
		}
		catch (Exception e)
		{
			OutputHandler.felog.severe("Error loading permissions");
			e.printStackTrace();
			return null;
		}
	}

	public static void loadZonePermissions(File path, Zone zone)
	{
		File playersPath = new File(path, "players");
		File groupsPath = new File(path, "groups");

		if (playersPath.exists())
		{
			for (File file : playersPath.listFiles(permissionFilter))
			{
				try
				{
					Properties p = new Properties();
					p.load(new BufferedInputStream(new FileInputStream(file)));

					// Get player
					String username = p.getProperty("fe.internal.player.username");
					String uuid = p.getProperty("fe.internal.player.uuid");
					p.remove("fe.internal.player.username");
					p.remove("fe.internal.player.uuid");
					if (username == null && uuid == null)
					{
						OutputHandler.felog.severe("User identification missing in " + path.getAbsolutePath());
						continue;
					}
					UserIdent ident = new UserIdent(uuid, username);

					// Load permissions
					PermissionList permissions = zone.getOrCreatePlayerPermissions(ident);
					for (Entry permission : p.entrySet())
					{
						permissions.put((String) permission.getKey(), (String) permission.getValue());
					}
				}
				catch (IllegalArgumentException | IOException e)
				{
					OutputHandler.felog.severe("Error reading permissions from " + path.getAbsolutePath());
				}
			}

		}

		if (groupsPath.exists())
		{
			for (File file : groupsPath.listFiles(permissionFilter))
			{
				try
				{
					Properties p = new Properties();
					p.load(new BufferedInputStream(new FileInputStream(file)));

					// Get group
					String groupName = file.getName().substring(0, file.getName().length() - PERMISSION_FILE_EXT.length());
					if (groupName.equals(IPermissionsHelper.PERMISSION_ALL))
						groupName = IPermissionsHelper.PERMISSION_ASTERIX;

					// Load permissions
					PermissionList permissions = zone.getOrCreateGroupPermissions(groupName);
					for (Entry permission : p.entrySet())
					{
						permissions.put((String) permission.getKey(), (String) permission.getValue());
					}
				}
				catch (IllegalArgumentException | IOException e)
				{
					OutputHandler.felog.severe("Error reading permissions from " + path.getAbsolutePath());
				}
			}

		}
	}

}
