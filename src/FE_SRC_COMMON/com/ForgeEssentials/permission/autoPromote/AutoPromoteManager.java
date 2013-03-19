package com.ForgeEssentials.permission.autoPromote;

import java.util.HashMap;
import java.util.TimerTask;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import com.ForgeEssentials.api.data.ClassContainer;
import com.ForgeEssentials.api.data.DataStorageManager;
import com.ForgeEssentials.api.permissions.Zone;
import com.ForgeEssentials.api.permissions.ZoneManager;
import com.ForgeEssentials.util.AreaSelector.WorldPoint;
import com.ForgeEssentials.util.tasks.TaskRegistry;

import cpw.mods.fml.common.FMLCommonHandler;

public class AutoPromoteManager extends TimerTask
{
	public HashMap<String, AutoPromote>	map	= new HashMap<String, AutoPromote>();
	static ClassContainer				con	= new ClassContainer(AutoPromote.class);
	private static AutoPromoteManager	instance;

	public static AutoPromoteManager instance()
	{
		return instance;
	}

	public AutoPromoteManager()
	{
		if (!FMLCommonHandler.instance().getEffectiveSide().isServer())
			return;
		Object[] loaded = DataStorageManager.getReccomendedDriver().loadAllObjects(con);
		if (loaded != null)
		{
			for (Object obj : loaded)
			{
				AutoPromote ap = (AutoPromote) obj;
				if (ZoneManager.getZone(ap.zone) != null)
				{
					map.put(ap.zone, ap);
				}
			}
		}
		TaskRegistry.registerRecurringTask(this, 0, 0, 1, 0, 0, 1, 0, 0);
		instance = this;
	}

	@Override
	public void run()
	{
		for (String username : MinecraftServer.getServer().getAllUsernames())
		{
			EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().getPlayerForUsername(username);
			Zone zone = ZoneManager.getWhichZoneIn(new WorldPoint(player));
			while (zone != null)
			{
				if (map.containsKey(zone.getZoneName()))
				{
					map.get(zone.getZoneName()).tick(player);
				}
				zone = ZoneManager.getZone(zone.parent);
			}
		}
	}

	public void stop()
	{
		TaskRegistry.removeTask(this);
		for (AutoPromote ap : map.values())
		{
			DataStorageManager.getReccomendedDriver().saveObject(con, ap);
		}
	}

	public static void save(AutoPromote ap)
	{
		if (ap != null)
		{
			try
			{
				DataStorageManager.getReccomendedDriver().saveObject(con, ap);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
