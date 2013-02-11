package com.ForgeEssentials.permission;

import java.util.Arrays;
import java.util.HashMap;

import net.minecraft.server.MinecraftServer;

import com.ForgeEssentials.api.data.DataStorageManager;
import com.ForgeEssentials.api.data.IReconstructData;
import com.ForgeEssentials.api.data.SaveableObject;
import com.ForgeEssentials.api.data.SaveableObject.Reconstructor;
import com.ForgeEssentials.api.data.SaveableObject.SaveableField;
import com.ForgeEssentials.api.data.SaveableObject.UniqueLoadingKey;
import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.ZoneManager;
import com.ForgeEssentials.commands.CommandAFK;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.OutputHandler;
import com.ForgeEssentials.util.PWarp;
import com.ForgeEssentials.util.AreaSelector.WarpPoint;

@SaveableObject
public class AutoPromote implements Runnable
{
	
	/*
	 * This part is used once per zone.
	 * Configurable via commands in game.
	 */
	
	@UniqueLoadingKey
	@SaveableField
	public String 								zone;
	
	@SaveableField
	public boolean								enable;
	
	
	public HashMap<Integer, String>				promotemap	= new HashMap();
	
	public AutoPromote(String zone, boolean enable)
	{
		this.zone = zone;
		this.enable = enable;
	}
	
	@Reconstructor
	private static AutoPromote reconstruct(IReconstructData tag)
	{
		AutoPromote data = new AutoPromote((String) tag.getFieldValue("zone"), (Boolean) tag.getFieldValue("enable"));
		//data.promotemap = (HashMap<Integer, String>) tag.getFieldValue("promotemap");
		return data;
	}
	
	public void save()
	{
		DataStorageManager.getReccomendedDriver().saveObject(this);
	}
	
	public void count(String player, int time)
	{
		//System.out.println(player + " counted in " + zone + " time: " + time);
		
		/*
		// if(map.containsKey(PlayerInfo.getPlayerInfo(player).timePlayed))
		{
			String currentzone = PermissionsAPI.getHighestGroup(FunctionHelper.getPlayerFromPartialName(player)).name;
			PromotionLadder ladder = SqlHelper.getLadderForGroup(currentzone, zone);
			if (ladder == null)
			{
				OutputHandler.severe("WTF ARE YOU DOING? YOU WANT ME TO CRASH???");
			}
			else
			{
				System.out.println(ladder.getListGroup());
				int currentID = Arrays.asList(ladder.getListGroup()).indexOf(currentzone);
				int newID = Arrays.asList(ladder.getListGroup()).indexOf(map.get(PlayerInfo.getPlayerInfo(player).timePlayed));
				
				if(newID > currentID)
				{
					PermissionsAPI.setPlayerGroup(map.get(PlayerInfo.getPlayerInfo(player).timePlayed), player, currentzone);
				}
			}
		}
		*/
	}

	
	/*
	 * This part is run only once. This part makes the "ticker" run. (Static values)
	 */
	
	private static Thread						thread;
	public static MinecraftServer				server;
	public static boolean						countAFK;
	public static HashMap<String, AutoPromote> 	map 		= new HashMap();
	
	
	public AutoPromote(MinecraftServer server)
	{
		if(thread != null)
		{
			OutputHandler.severe("##################################");
			OutputHandler.severe("### AutoPromote thread != null ###");
			OutputHandler.severe("###           dafuq?           ###");
			OutputHandler.severe("##################################");
			return;
		}
		this.server = server;

		thread = new Thread(this, "ForgeEssentials - Permssions - autoPromote");
		thread.start();
		
		if(!map.containsKey(ZoneManager.getGLOBAL().getZoneName()))
		{
			map.put(ZoneManager.getGLOBAL().getZoneName(), new AutoPromote(ZoneManager.getGLOBAL().getZoneName(), false));
		}
	}
	
	@Override
	public void run()
	{
		while (server.isServerRunning())
		{
			try
			{
				thread.sleep(1000 * 10);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}

			for (String player : server.getConfigurationManager().getAllUsernames())
			{
				try
				{
					if(CommandAFK.afkList.contains(player))
					{
						if(countAFK) PlayerInfo.getPlayerInfo(player).timePlayed++;
					}	
					else
					{
						PlayerInfo.getPlayerInfo(player).timePlayed++;
					}
				}
				catch (Exception e) 
				{
					PlayerInfo.getPlayerInfo(player).timePlayed++;
				}
				
				for(AutoPromote obj : map.values())
				{
					obj.count(player, PlayerInfo.getPlayerInfo(player).timePlayed);
				}
			}
		}
	}

	public static void saveAll()
	{
		for(AutoPromote obj : map.values())
		{
			obj.save();
		}
	}
}
