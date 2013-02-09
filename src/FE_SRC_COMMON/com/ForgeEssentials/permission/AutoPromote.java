package com.ForgeEssentials.permission;

import java.util.Arrays;
import java.util.HashMap;

import net.minecraft.server.MinecraftServer;

import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.ZoneManager;
import com.ForgeEssentials.commands.CommandAFK;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.OutputHandler;

public class AutoPromote implements Runnable
{
	private Thread							thread;
	public int								waittime	= 1;
	public boolean							enable		= true;
	public HashMap<Integer, String>			map			= new HashMap();
	public boolean							counteAFK;
	public String 							zone;
	
	MinecraftServer							server;
	

	public AutoPromote(MinecraftServer server, String zone)
	{
		this.server = server;
		this.zone = zone;

		thread = new Thread(this, "ForgeEssentials - Permssions - autoPromote");
		thread.start();
	}

	@Override
	public void run()
	{
		while (server.isServerRunning())
		{
			try
			{
				thread.sleep(1000 * waittime);
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
			}

			if (enable)
			{
				for (String player : server.getConfigurationManager().getAllUsernames())
				{
					try
					{
						if(CommandAFK.afkList.contains(player))
						{
							if(counteAFK) PlayerInfo.getPlayerInfo(player).timePlayed++;
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
				}
			}
		}
	}
}
