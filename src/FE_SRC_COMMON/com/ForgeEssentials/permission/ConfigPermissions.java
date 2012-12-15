package com.ForgeEssentials.permission;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import net.minecraftforge.event.Event.Result;

import com.ForgeEssentials.util.OutputHandler;

public class ConfigPermissions
{
	public static File			permissionsFile	= new File(ModulePermissions.permsFolder, "permissions.cfg");

	private static final String	SUPERS			= "_PLAYER_SUPERS_";

	public Configuration		config;

	private String				global;

	public ConfigPermissions()
	{
		OutputHandler.debug("ConfigPermissions initlializing...");
		config = new Configuration(permissionsFile, true);

		global = ZoneManager.GLOBAL.getZoneID();

		// Supers properties
		if (config.categories.containsKey(SUPERS))
		{
			config.addCustomCategoryComment(SUPERS, "the last stop where permissions get checked before their defaults");
			readPlayerSupers(config.categories.get(SUPERS));
		}
		writePlayerSupers(SUPERS);

		// GLOBAL properties
		if (config.categories.containsKey(global))
		{
			config.addCustomCategoryComment(global, "the last stop where permissions get checked before their defaults");

			if (config.categories.containsKey(global + ".groups"))
				readGroupPerms(ZoneManager.GLOBAL, config.categories.get(global + ".groups"));

			if (config.categories.containsKey(global + ".players"))
				readPlayerPerms(ZoneManager.GLOBAL, config.categories.get(global + ".players"));
		}
		writeGroupPerms(ZoneManager.GLOBAL, global);
		writePlayerPerms(ZoneManager.GLOBAL, global);

		// WorldZones
		for (Zone worldZone : ZoneManager.worldZoneMap.values())
		{
			if (config.categories.containsKey(worldZone.getZoneID()))
			{
				if (config.categories.containsKey(worldZone.getZoneID() + ".groups"))
					readGroupPerms(worldZone, config.categories.get(worldZone.getZoneID() + ".groups"));

				if (config.categories.containsKey(worldZone.getZoneID() + ".players"))
					readPlayerPerms(ZoneManager.GLOBAL, config.categories.get(worldZone.getZoneID() + ".players"));
			}
			writeGroupPerms(worldZone, worldZone.getZoneID());
			writePlayerPerms(worldZone, worldZone.getZoneID());
		}

		// all the other zones.
		for (Zone zone : ZoneManager.zoneMap.values())
		{
			if (config.categories.containsKey(zone.getZoneID()))
			{
				if (config.categories.containsKey(zone.getZoneID() + ".groups"))
					readGroupPerms(zone, config.categories.get(zone.getZoneID() + ".groups"));

				if (config.categories.containsKey(zone.getZoneID() + ".players"))
					readPlayerPerms(ZoneManager.GLOBAL, config.categories.get(zone.getZoneID() + ".players"));
			}
			writeGroupPerms(zone, zone.getZoneID());
			writePlayerPerms(zone, zone.getZoneID());
		}

		config.save();
		OutputHandler.debug("ConfigPermissions initlialization complete");
	}

	/**
	 * 
	 * @param zone who's permissions to load from the file.
	 */
	public void forceLoadZone(Zone zone)
	{
		config.load();
		if (config.categories.containsKey(zone.getZoneID()))
		{
			if (config.categories.containsKey(zone.getZoneID() + ".groups"))
				readGroupPerms(zone, config.categories.get(zone.getZoneID() + ".groups"));

			if (config.categories.containsKey(zone.getZoneID() + ".players"))
				readPlayerPerms(zone, config.categories.get(zone.getZoneID() + ".players"));
		}
	}

	/**
	 * 
	 * @param zone who's permissions to save to the file.
	 */
	public void forceSaveZone(Zone zone)
	{
		writeGroupPerms(zone, zone.getZoneID());
		writePlayerPerms(zone, zone.getZoneID());
		config.save();
	}

	public void forceLoadSupers()
	{
		config.load();
		// Supers properties
		if (config.categories.containsKey(SUPERS))
		{
			config.addCustomCategoryComment(SUPERS, "the last stop where permissions get checked before their defaults");
			readPlayerSupers(config.categories.get(SUPERS));
		}
	}

	public void forceSaveSupers()
	{
		writePlayerSupers(SUPERS);
		config.save();
	}

	public void loadAll()
	{
		config.load();

		// GLOBAL properties
		if (config.categories.containsKey(global))
		{
			config.addCustomCategoryComment(global, "the last stop where permissions get checked before their defaults");

			if (config.categories.containsKey(global + ".groups"))
				readGroupPerms(ZoneManager.GLOBAL, config.categories.get(global + ".groups"));

			if (config.categories.containsKey(global + ".players"))
				readPlayerPerms(ZoneManager.GLOBAL, config.categories.get(global + ".players"));
		}

		// WorldZones
		for (Zone worldZone : ZoneManager.worldZoneMap.values())
			if (config.categories.containsKey(worldZone.getZoneID()))
			{
				if (config.categories.containsKey(worldZone.getZoneID() + ".groups"))
					readGroupPerms(worldZone, config.categories.get(worldZone.getZoneID() + ".groups"));

				if (config.categories.containsKey(worldZone.getZoneID() + ".players"))
					readPlayerPerms(ZoneManager.GLOBAL, config.categories.get(worldZone.getZoneID() + ".players"));
			}

		// all the other zones.
		for (Zone zone : ZoneManager.zoneMap.values())
			if (config.categories.containsKey(zone.getZoneID()))
			{
				if (config.categories.containsKey(zone.getZoneID() + ".groups"))
					readGroupPerms(zone, config.categories.get(zone.getZoneID() + ".groups"));

				if (config.categories.containsKey(zone.getZoneID() + ".players"))
					readPlayerPerms(ZoneManager.GLOBAL, config.categories.get(zone.getZoneID() + ".players"));
			}
	}

	public void saveAll()
	{
		// GLOBAL properties
		writeGroupPerms(ZoneManager.GLOBAL, global);
		writePlayerPerms(ZoneManager.GLOBAL, global);

		// WorldZones
		for (Zone worldZone : ZoneManager.worldZoneMap.values())
		{
			writeGroupPerms(worldZone, worldZone.getZoneID());
			writePlayerPerms(worldZone, worldZone.getZoneID());
		}

		// all the other zones.
		for (Zone zone : ZoneManager.zoneMap.values())
		{
			writeGroupPerms(zone, zone.getZoneID());
			writePlayerPerms(zone, zone.getZoneID());
		}

		config.save();
	}

	private void readGroupPerms(Zone zone, ConfigCategory cat)
	{
		ArrayList<String> children = getCategoryChildren(cat);

		for (String child : children)
		{
			// read permissions
			ConfigCategory groupCat = config.categories.get(child);

			HashSet<Permission> perms = zone.groupOverrides.get(getPlayerNameFromCategory(child));
			if (perms == null)
				perms = new HashSet<Permission>();

			for (Property prop : groupCat.values())
			{
				Permission newPerm = new Permission(prop.getName(), prop.getBoolean(true));
				PermissionChecker check = new Permission(prop.getName(), prop.getBoolean(true));

				if (perms.contains(check) && !perms.contains(newPerm))
					perms.remove(check);

				perms.add(newPerm);
			}
		}
	}

	private void readPlayerSupers(ConfigCategory cat)
	{
		ArrayList<String> children = getCategoryChildren(cat);

		for (String child : children)
		{
			// read permissions
			ConfigCategory groupCat = config.categories.get(child);

			HashSet<Permission> perms = PlayerManager.playerSupers.get(getPlayerNameFromCategory(child));
			if (perms == null)
				perms = new HashSet<Permission>();

			for (Property prop : groupCat.values())
			{
				Permission newPerm = new Permission(prop.getName(), prop.getBoolean(true));
				PermissionChecker check = new Permission(prop.getName(), prop.getBoolean(true));

				if (perms.contains(check) && !perms.contains(newPerm))
					perms.remove(check);

				perms.add(newPerm);
			}
		}
	}

	private void readPlayerPerms(Zone zone, ConfigCategory cat)
	{
		ArrayList<String> children = getCategoryChildren(cat);

		for (String child : children)
		{
			// read permissions
			ConfigCategory groupCat = config.categories.get(child);

			HashSet<Permission> perms = zone.playerOverrides.get(getPlayerNameFromCategory(child));

			if (perms == null)
				perms = new HashSet<Permission>();

			for (Property prop : groupCat.values())
			{
				Permission newPerm = new Permission(prop.getName(), prop.getBoolean(true));
				PermissionChecker check = new Permission(prop.getName(), prop.getBoolean(true));

				if (perms.contains(check) && !perms.contains(newPerm))
					perms.remove(check);

				perms.add(newPerm);
			}
		}
	}

	private void writePlayerSupers(String parentCat)
	{
		for (String player : PlayerManager.playerSupers.keySet())
		{
			HashSet<Permission> list = PlayerManager.playerSupers.get(player);
			for (Permission perm : list)
				config.get(parentCat + "." + player, perm.name, perm.allowed.equals(Result.ALLOW));
		}
	}

	private void writePlayerPerms(Zone zone, String parentCat)
	{
		for (String player : zone.getPlayersOverriden())
		{
			HashSet<Permission> list = zone.playerOverrides.get(player);
			for (Permission perm : list)
				config.get(parentCat + ".players." + player, perm.name, perm.allowed.equals(Result.ALLOW));
		}
	}

	private void writeGroupPerms(Zone zone, String parentCat)
	{
		for (String group : zone.getGroupsOverriden())
		{
			HashSet<Permission> list = zone.groupOverrides.get(group);
			for (Permission perm : list)
				config.get(parentCat + ".groups." + group, perm.name, perm.allowed.equals(Result.ALLOW));
		}
	}

	private ArrayList<String> getCategoryChildren(ConfigCategory category)
	{
		ArrayList<String> categories = new ArrayList<String>();

		for (ConfigCategory cat : config.categories.values())
		{
			if (!cat.isChild())
				continue;

			if (cat.getQualifiedName().startsWith(category.getQualifiedName()))
				categories.add(cat.getQualifiedName());
		}

		return categories;
	}

	private String getPlayerNameFromCategory(String qualifiedName)
	{
		String[] names = qualifiedName.split("\\" + Configuration.CATEGORY_SPLITTER);

		if (names.length == 0)
			return qualifiedName;
		else
			return names[names.length - 1];
	}

}
