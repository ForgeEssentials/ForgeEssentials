package com.ForgeEssentials.permission;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;

import com.ForgeEssentials.util.FEChatFormatCodes;
import com.ForgeEssentials.util.OutputHandler;

public class ConfigPlayer
{
	public static File playersFile	= new File(ModulePermissions.permsFolder, "players.cfg");

	private static final String	PREFIX		= "chatPrefix";
	private static final String	SUFFIX		= "chatSuffix";
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
			tempPlayer.prefix = config.get(ZoneManager.GLOBAL.getZoneID() + "." + "AbrarSyed", PREFIX, FEChatFormatCodes.DARKRED + "[DevLead]" + FEChatFormatCodes.WHITE, "text to go before the username in chat. format char: \u00a7  Only works with the Chat module installed").value;
			tempPlayer.suffix = config.get(ZoneManager.GLOBAL.getZoneID() + "." + "AbrarSyed", SUFFIX, "", "text to go after the username in chat. format char: \u00a7  Only works with the Chat module installed").value;
			tempPlayer.addGroupAll(config.get(ZoneManager.GLOBAL.getZoneID() + "." + "AbrarSyed", GROUP, new String[] {PermissionsAPI.GROUP_OWNERS}, "The group this player will be in while in this Zone").valueList);
			PlayerManager.putPlayerData(tempPlayer);

			tempPlayer = new PlayerPermData("An_Sar");
			tempPlayer.prefix = config.get(ZoneManager.GLOBAL.getZoneID() + "." + "An_Sar", PREFIX, FEChatFormatCodes.DARKGREEN + "[AwesomeGuy]" + FEChatFormatCodes.WHITE).value;
			tempPlayer.suffix = config.get(ZoneManager.GLOBAL.getZoneID() + "." + "An_Sar", SUFFIX, "").value;
			tempPlayer.addGroupAll(config.get(ZoneManager.GLOBAL.getZoneID() + "." + "An_Sar", GROUP, new String[] {PermissionsAPI.GROUP_MEMBERS}).valueList);
			PlayerManager.putPlayerData(tempPlayer);

			tempPlayer = new PlayerPermData("Luacs1998");
			tempPlayer.prefix = config.get(ZoneManager.GLOBAL.getZoneID() + "." + "Luacs1998", PREFIX, FEChatFormatCodes.RED + "[Dev]" + FEChatFormatCodes.WHITE).value;
			tempPlayer.suffix = config.get(ZoneManager.GLOBAL.getZoneID() + "." + "Luacs1998", SUFFIX, "").value;
			tempPlayer.addGroupAll(config.get(ZoneManager.GLOBAL.getZoneID() + "." + "Luacs1998", GROUP, new String[] {PermissionsAPI.GROUP_ZONE_ADMINS}).valueList);
			PlayerManager.putPlayerData(tempPlayer);

			tempPlayer = new PlayerPermData("MysteriousAges");
			tempPlayer.prefix = config.get(ZoneManager.GLOBAL.getZoneID() + "." + "MysteriousAges", PREFIX, FEChatFormatCodes.RED + "[Dev]" + FEChatFormatCodes.WHITE).value;
			tempPlayer.suffix = config.get(ZoneManager.GLOBAL.getZoneID() + "." + "MysteriousAges", SUFFIX, "").value;
			tempPlayer.addGroupAll(config.get(ZoneManager.GLOBAL.getZoneID() + "." + "MysteriousAges", GROUP, new String[] {PermissionsAPI.GROUP_ZONE_ADMINS}).valueList);
			PlayerManager.putPlayerData(tempPlayer);

			tempPlayer = new PlayerPermData("Bob_A_Red_Dino");
			tempPlayer.prefix = config.get(ZoneManager.GLOBAL.getZoneID() + "." + "Bob_A_Red_Dino", PREFIX, FEChatFormatCodes.RED + "[Dev]" + FEChatFormatCodes.WHITE).value;
			tempPlayer.suffix = config.get(ZoneManager.GLOBAL.getZoneID() + "." + "Bob_A_Red_Dino", SUFFIX, "").value;
			tempPlayer.addGroupAll(config.get(ZoneManager.GLOBAL.getZoneID() + "." + "Bob_A_Red_Dino", GROUP, new String[] {PermissionsAPI.GROUP_ZONE_ADMINS}).valueList);
			PlayerManager.putPlayerData(tempPlayer);
			
			tempPlayer = new PlayerPermData("Dries007");
			tempPlayer.prefix = config.get(ZoneManager.GLOBAL.getZoneID() + "." + "Dries007", PREFIX, FEChatFormatCodes.RED + "[Dev]" + FEChatFormatCodes.WHITE).value;
			tempPlayer.suffix = config.get(ZoneManager.GLOBAL.getZoneID() + "." + "Dries007", SUFFIX, "").value;
			tempPlayer.addGroupAll(config.get(ZoneManager.GLOBAL.getZoneID() + "." + "Dries007", GROUP, new String[] {PermissionsAPI.GROUP_ZONE_ADMINS}).valueList);
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

				tempPlayer.prefix = config.get(group, PREFIX, "").value;
				tempPlayer.suffix = config.get(group, SUFFIX, "").value;
				tempPlayer.addGroupAll(config.get(group, GROUP, new String[] {}).valueList);

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
		for (ConcurrentHashMap<String, PlayerPermData> map : PlayerManager.playerDats.values())
			for (PlayerPermData data : map.values())
			{
				String category = new StringBuilder().append(data.zoneID).append('.').append(data.username).toString();
				config.get(category, PREFIX, "").value = data.prefix;
				config.get(category, SUFFIX, "").value = data.suffix;
				config.get(category, GROUP, new String[] {}).valueList = data.getGroupList().toArray(new String[] {});

				ConfigCategory cat = config.categories.get(category);
				cat.putAll(data.getData());
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
			if (!other.isChild())
				continue;

			if (other.getQualifiedName().startsWith(other.getQualifiedName()))
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
