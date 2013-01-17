package com.ForgeEssentials.permission;

import java.io.File;
import java.util.ArrayList;

import net.minecraftforge.common.ConfigCategory;
import net.minecraftforge.common.Configuration;

import com.ForgeEssentials.core.PlayerInfo;

import cpw.mods.fml.common.FMLCommonHandler;

public class FlatFilePlayers
{
	File file;

	public FlatFilePlayers(File file)
	{
		this.file = new File(file, "players.txt");
	}
	public ArrayList<String> load()
	{
		ArrayList<String> players = new ArrayList<String>(); 

		Configuration config = new Configuration(file);
		
		String[] allPlayers = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getAllUsernames();
		
		PlayerInfo info;
		for (String cat : config.categories.keySet())
		{
			if (cat.contains("."))
				continue;
			else if (cat.equalsIgnoreCase(PermissionsAPI.EntryPlayer))
			{
				PermissionsAPI.EPPrefix = config.get(cat, "prefix", " ").value;
				PermissionsAPI.EPSuffix = config.get(cat, "suffix", " ").value;
				continue;
			}
			
			info = PlayerInfo.getPlayerInfo(cat);
			info.prefix = config.get(cat, "prefix", " ").value;
			info.suffix = config.get(cat, "suffix", " ").value;
			players.add(cat);
			discardInfo(info, allPlayers);
		}

		return players;
	}

	public void save(ArrayList<String> players)
	{
		// clear it.
		if (file.exists())
			file.delete();
		
		String[] allPlayers = FMLCommonHandler.instance().getSidedDelegate().getServer().getConfigurationManager().getAllUsernames();
		
		Configuration config = new Configuration(file);
		
		PlayerInfo info;
		for (String name : players)
		{
			if (name.equalsIgnoreCase(PermissionsAPI.EntryPlayer))
			{
				config.get(name, "prefix", PermissionsAPI.EPPrefix);
				config.get(name, "suffix", PermissionsAPI.EPSuffix);
				continue;
			}
			
			info = PlayerInfo.getPlayerInfo(name);
			config.get(name, "prefix", info.prefix == null ? "" : info.prefix);
			config.get(name, "suffix", info.suffix == null ? "" : info.suffix);
			discardInfo(info, allPlayers);
		}

		config.save();
	}
	
	private void discardInfo(PlayerInfo info, String[] allPlayers)
	{
		for (String name : allPlayers)
			if (info.username.equalsIgnoreCase(name))
				return;
		
		// not logged in?? kill it.
		PlayerInfo.discardInfo(info.username);
	}
	

	private ArrayList<String> getCategoryChildren(Configuration config, ConfigCategory category)
	{
		ArrayList<String> categories = new ArrayList<String>();

		for (ConfigCategory cat : config.categories.values())
		{
			if (!cat.isChild())
			{
				continue;
			}

			if (cat.getQualifiedName().startsWith(category.getQualifiedName()))
			{
				categories.add(cat.getQualifiedName());
			}
		}

		return categories;
	}

	private String getPlayerNameFromCategory(String qualifiedName)
	{
		String[] names = qualifiedName.split("\\" + Configuration.CATEGORY_SPLITTER);

		if (names.length == 0)
		{
			return qualifiedName;
		}
		else
		{
			return names[names.length - 1];
		}
	}

}
