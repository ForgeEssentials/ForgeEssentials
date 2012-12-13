package com.ForgeEssentials.permission;

import java.io.File;
import java.util.ArrayList;

import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.OutputHandler;

public class ConfigPlayer
{
	public static File			playersFile	= new File(ModulePermissions.permsFolder, "players.cfg");

	private static final String	PREFIX		= "chatPrefix";
	private static final String	SUFFIX		= "chatSuffix";
	private static final String	PARENT		= "parent";
	private static final String	GROUP		= "group";

	public Configuration		config;

	public ConfigPlayer()
	{
		OutputHandler.debug("ConfigGroups initlializing...");

		config = new Configuration(playersFile, true);

		// to be used lots of places...
		PlayerPermData tempPlayer; // temporary group.

		// check for other groups. or generate
		if (config.categories.get(ZoneManager.GLOBAL.getZoneID()) == null || !doesCategoryHaveChilldren(config.categories.get(ZoneManager.GLOBAL.getZoneID())))
		{
			tempPlayer = new PlayerPermData("AbrarSyed");
			tempPlayer.setParent(config.get(ZoneManager.GLOBAL.getZoneID() + "." + "AbrarSyed", PARENT, "", "the group from which this group will inherit permissions").value);
			tempPlayer.prefix = config.get(ZoneManager.GLOBAL.getZoneID() + "." + "AbrarSyed", PREFIX, FEChatFormatCodes.DARKRED + "[DevLead]" + FEChatFormatCodes.WHITE, "text to go before the username in chat. format char: \u00a7  Only works with the Chat module installed").value;
			tempPlayer.suffix = config.get(ZoneManager.GLOBAL.getZoneID() + "." + "AbrarSyed", SUFFIX, "", "text to go after the username in chat. format char: \u00a7  Only works with the Chat module installed").value;
			tempPlayer.group = config.get(ZoneManager.GLOBAL.getZoneID() + "." + "AbrarSyed", GROUP, PermissionsAPI.GROUP_OWNERS, "The group this player will be in while in this Zone").value;
			PlayerManager.putPlayerData(tempPlayer);

			tempPlayer = new PlayerPermData("An_Sar");
			tempPlayer.setParent(config.get(ZoneManager.GLOBAL.getZoneID() + "." + "An_Sar", PARENT, "").value);
			tempPlayer.prefix = config.get(ZoneManager.GLOBAL.getZoneID() + "." + "An_Sar", PREFIX, FEChatFormatCodes.DARKGREEN + "[AwesomeGuy]" + FEChatFormatCodes.WHITE).value;
			tempPlayer.suffix = config.get(ZoneManager.GLOBAL.getZoneID() + "." + "An_Sar", SUFFIX, "").value;
			tempPlayer.group = config.get(ZoneManager.GLOBAL.getZoneID() + "." + "An_Sar", GROUP, PermissionsAPI.GROUP_MEMBERS).value;
			PlayerManager.putPlayerData(tempPlayer);

			tempPlayer = new PlayerPermData("Luacs1998");
			tempPlayer.setParent(config.get(ZoneManager.GLOBAL.getZoneID() + "." + "Luacs1998", PARENT, "").value);
			tempPlayer.prefix = config.get(ZoneManager.GLOBAL.getZoneID() + "." + "Luacs1998", PREFIX, FEChatFormatCodes.RED + "[Dev]" + FEChatFormatCodes.WHITE).value;
			tempPlayer.suffix = config.get(ZoneManager.GLOBAL.getZoneID() + "." + "Luacs1998", SUFFIX, "").value;
			tempPlayer.group = config.get(ZoneManager.GLOBAL.getZoneID() + "." + "Luacs1998", GROUP, PermissionsAPI.GROUP_ZONE_ADMINS).value;
			PlayerManager.putPlayerData(tempPlayer);

			tempPlayer = new PlayerPermData("MysteriousAges");
			tempPlayer.setParent(config.get(ZoneManager.GLOBAL.getZoneID() + "." + "MysteriousAges", PARENT, "").value);
			tempPlayer.prefix = config.get(ZoneManager.GLOBAL.getZoneID() + "." + "MysteriousAges", PREFIX, FEChatFormatCodes.RED + "[Dev]" + FEChatFormatCodes.WHITE).value;
			tempPlayer.suffix = config.get(ZoneManager.GLOBAL.getZoneID() + "." + "MysteriousAges", SUFFIX, "").value;
			tempPlayer.group = config.get(ZoneManager.GLOBAL.getZoneID() + "." + "MysteriousAges", GROUP, PermissionsAPI.GROUP_ZONE_ADMINS).value;
			PlayerManager.putPlayerData(tempPlayer);

			tempPlayer = new PlayerPermData("Bob_A_Red_Dino");
			tempPlayer.setParent(config.get(ZoneManager.GLOBAL.getZoneID() + "." + "Bob_A_Red_Dino", PARENT, "").value);
			tempPlayer.prefix = config.get(ZoneManager.GLOBAL.getZoneID() + "." + "Bob_A_Red_Dino", PREFIX, FEChatFormatCodes.RED + "[Dev]" + FEChatFormatCodes.WHITE).value;
			tempPlayer.suffix = config.get(ZoneManager.GLOBAL.getZoneID() + "." + "Bob_A_Red_Dino", SUFFIX, "").value;
			tempPlayer.group = config.get(ZoneManager.GLOBAL.getZoneID() + "." + "Bob_A_Red_Dino", GROUP, PermissionsAPI.GROUP_ZONE_ADMINS).value;
			PlayerManager.putPlayerData(tempPlayer);
		}

		forceLoadConfig();

		// category comments since it doesn't set these if its null...
		config.addCustomCategoryComment(ZoneManager.GLOBAL.getZoneID(), "This is interpretted as the SUPER. Meaning it is layered on TOP of everything specified in other places (groups, zones etc.) This applies only to the Suffix and Prefix properties.");

		config.save();

		OutputHandler.debug("ConfigGroups initlialization complete");
	}

	public void forceLoadConfig()
	{
		PlayerPermData tempPlayer;
		for (ConfigCategory cat : config.categories.values())
		{
			if (cat.isChild())
				continue;

			// all the world/Global categories now...

			new ArrayList<String>();

			// Iterate through children.
			for (String group : getCategoryChildren(cat))
			{
				// read Player
				tempPlayer = new PlayerPermData(getPlayerNameFromCategory(group), cat.getQualifiedName());

				tempPlayer.setParent(config.get(group, PARENT, "").value);
				tempPlayer.prefix = config.get(group, PREFIX, "").value;
				tempPlayer.suffix = config.get(group, SUFFIX, "").value;
				tempPlayer.group = config.get(group, GROUP, "").value;

				for (Property prop : config.categories.get(group).getValues().values())
					if (prop.getName().equals(PREFIX) || prop.getName().equals(SUFFIX))
						continue;
					else
						tempPlayer.addData(prop);

				PlayerManager.putPlayerData(tempPlayer);
			}
		}
	}

	public void forceSaveConfigs()
	{
		for (Group group : GroupManager.groups.values())
		{
			String category = new StringBuilder().append(group.zoneID).append('.').append(group.name).toString();
			config.get(category, PARENT, "").value = group.getParent();
			config.get(category, PREFIX, "").value = group.prefix;
			config.get(category, SUFFIX, "").value = group.suffix;

			ConfigCategory cat = config.categories.get(category);
			cat.putAll(group.getData());
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

	private boolean doesCategoryHaveChilldren(ConfigCategory cat)
	{
		for (ConfigCategory other : config.categories.values())
		{
			if (!cat.isChild())
				continue;

			if (cat.getQualifiedName().startsWith(cat.getQualifiedName()))
				return true;
		}
		return false;
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
