package com.forgeessentials.permissions.autoPromote;

import java.util.HashMap;
import java.util.List;
import java.util.TimerTask;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.util.selections.WorldPoint;
import com.forgeessentials.util.tasks.TaskRegistry;

import cpw.mods.fml.common.FMLCommonHandler;

public class AutoPromoteManager extends TimerTask {
	static ClassContainer con = new ClassContainer(AutoPromote.class);
	private static AutoPromoteManager instance;
	public HashMap<Integer, AutoPromote> map = new HashMap<Integer, AutoPromote>();

	public AutoPromoteManager()
	{
		if (!FMLCommonHandler.instance().getEffectiveSide().isServer())
		{
			return;
		}
		Object[] loaded = DataStorageManager.getReccomendedDriver().loadAllObjects(con);
		if (loaded != null)
		{
			for (Object obj : loaded)
			{
				AutoPromote ap = (AutoPromote) obj;
				if (APIRegistry.perms.getZoneById(ap.getZone()) != null)
				{
					map.put(ap.getZone(), ap);
				}
			}
		}
		TaskRegistry.registerRecurringTask(this, 0, 0, 1, 0, 0, 1, 0, 0);
		instance = this;
	}

	public static AutoPromoteManager instance()
	{
		return instance;
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

	@Override
	public void run()
	{
		for (String username : MinecraftServer.getServer().getAllUsernames())
		{
			EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().func_152612_a(username);
			List<AreaZone> zones = APIRegistry.perms.getAreaZonesAt(new WorldPoint(player));
			Zone zone = zones.isEmpty() ? null : zones.get(0);
			while (zone != null)
			{
				if (map.containsKey(zone.getName()))
				{
					map.get(zone.getName()).tick(player);
				}
				zone = zone.getParent();
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
}
