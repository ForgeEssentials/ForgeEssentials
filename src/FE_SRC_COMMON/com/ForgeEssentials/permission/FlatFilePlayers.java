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
		
		PlayerInfo info;
		for (String cat : config.categories.keySet())
		{
			if (cat.contains("."))
				continue;
			else if (cat.equalsIgnoreCase(APIHelper.EntryPlayer))
			{
				APIHelper.EPPrefix = config.get(cat, "prefix", " ").value;
				APIHelper.EPSuffix = config.get(cat, "suffix", " ").value;
				continue;
			}
			
			info = PlayerInfo.getPlayerInfo(cat);
			
			if (info != null)
			{
				info.prefix = config.get(cat, "prefix", " ").value;
				info.suffix = config.get(cat, "suffix", " ").value;
			}
			
			players.add(cat);
			discardInfo(info, new String[] {});
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
			if (name.equalsIgnoreCase(APIHelper.EntryPlayer))
			{
				config.get(name, "prefix", APIHelper.EPPrefix);
				config.get(name, "suffix", APIHelper.EPSuffix);
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

}
