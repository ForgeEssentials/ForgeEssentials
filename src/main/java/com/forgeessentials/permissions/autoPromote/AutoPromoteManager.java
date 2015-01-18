package com.forgeessentials.permissions.autoPromote;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimerTask;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.data.v2.DataManager;
import com.forgeessentials.util.tasks.TaskRegistry;

import cpw.mods.fml.common.FMLCommonHandler;

public class AutoPromoteManager extends TimerTask {

    private static AutoPromoteManager instance;
	
    public Map<Integer, AutoPromote> promoteMap = new HashMap<Integer, AutoPromote>();

	public AutoPromoteManager()
	{
		if (!FMLCommonHandler.instance().getEffectiveSide().isServer())
			return;

		Map<String, AutoPromote> loaded = DataManager.getInstance().loadAll(AutoPromote.class);
		if (loaded != null)
			for (AutoPromote ap : loaded.values())
				if (APIRegistry.perms.getZoneById(ap.getZone()) != null)
					promoteMap.put(ap.getZone(), ap);
		
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
		    DataManager.getInstance().save(ap, Integer.toString(ap.getZone()));
		}
	}

	@Override
	public void run()
	{
		for (String username : MinecraftServer.getServer().getAllUsernames())
		{
			EntityPlayerMP player = MinecraftServer.getServer().getConfigurationManager().func_152612_a(username);
			List<AreaZone> zones = APIRegistry.perms.getServerZone().getAreaZonesAt(new WorldPoint(player));
			Zone zone = zones.isEmpty() ? null : zones.get(0);
			while (zone != null)
			{
				if (promoteMap.containsKey(zone.toString()))
				{
					promoteMap.get(zone.toString()).tick(player);
				}
				zone = zone.getParent();
			}
		}
	}

	public void stop()
	{
		TaskRegistry.removeTask(this);
		for (AutoPromote ap : promoteMap.values())
		{
            DataManager.getInstance().save(ap, Integer.toString(ap.getZone()));
		}
	}
}
