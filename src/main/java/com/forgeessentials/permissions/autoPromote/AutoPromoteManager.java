package com.forgeessentials.permissions.autoPromote;

import java.util.HashMap;
import java.util.TimerTask;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;
import com.forgeessentials.util.selections.WorldPoint;
import com.forgeessentials.util.tasks.TaskRegistry;

import cpw.mods.fml.common.FMLCommonHandler;

public class AutoPromoteManager extends TimerTask {
    static ClassContainer con = new ClassContainer(AutoPromote.class);
    private static AutoPromoteManager instance;
    public HashMap<String, AutoPromote> map = new HashMap<String, AutoPromote>();

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
                if (APIRegistry.perms.getZone(ap.zone) != null)
                {
                    map.put(ap.zone, ap);
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
            Zone zone = APIRegistry.perms.getWhichZoneIn(new WorldPoint(player));
            while (zone != null)
            {
                if (map.containsKey(zone.getName()))
                {
                    map.get(zone.getName()).tick(player);
                }
                zone = APIRegistry.perms.getZone(zone.parent);
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
