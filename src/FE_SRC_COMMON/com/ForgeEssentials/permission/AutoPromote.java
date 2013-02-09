package com.ForgeEssentials.permission;

import java.util.HashMap;

import net.minecraft.server.MinecraftServer;

import com.ForgeEssentials.api.permissions.PermissionsAPI;
import com.ForgeEssentials.api.permissions.ZoneManager;
import com.ForgeEssentials.core.PlayerInfo;
import com.ForgeEssentials.util.FunctionHelper;
import com.ForgeEssentials.util.OutputHandler;

public class AutoPromote implements Runnable
{
	private static Thread					thread;
	public static int						waittime	= 1;
	public static boolean					enable		= false;
	public static HashMap<Integer, String>	map			= new HashMap();

	MinecraftServer							server;

	public AutoPromote(MinecraftServer server)
	{
		this.server = server;

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
					PlayerInfo.getPlayerInfo(player).timePlayed++;
					// if(map.containsKey(PlayerInfo.getPlayerInfo(player).timePlayed))
					{
						PromotionLadder ladder = SqlHelper.getLadderForGroup(PermissionsAPI.getHighestGroup(FunctionHelper.getPlayerFromPartialName(player)).name, ZoneManager.getGLOBAL().getZoneName());
						if (ladder == null)
						{
							OutputHandler.severe("WTF ARE YOU DOING? YOU WANT ME TO CRASH???");
						}
						else
						{
							System.out.println(ladder.getListGroup());
						}
					}
				}
			}
		}
	}
}
