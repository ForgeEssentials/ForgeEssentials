package com.forgeessentials.commands.util;

import com.forgeessentials.core.PlayerInfo;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Use for all commands that need a cooldown, except for warp systems, see
 * TeleportCenter.
 *
 * @author Dries007
 */

public class TickHandlerCommands implements IScheduledTickHandler {
    /*
     * For AFK system
	 */

    /*
     * For kit command
     */
    public static final String BYPASS_KIT_COOLDOWN = "fe.TickHandlerCommands.BypassKitCooldown";
    public static List<AFKdata> afkList = new ArrayList<AFKdata>();
    public static List<AFKdata> afkListToAdd = new ArrayList<AFKdata>();
    public static List<AFKdata> afkListToRemove = new ArrayList<AFKdata>();

    public static int getWorldHour(World world)
    {
        return (int) ((world.getWorldTime() % 24000) / 1000);
    }

    public static int getWorldDays(World world)
    {
        return (int) (world.getWorldTime() / 24000);
    }

    public static void makeWorldTimeHours(World world, int target)
    {
        world.setWorldTime((getWorldDays(world) + 1) * 24000 + (target * 1000));
    }

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
        if (type.contains(TickType.SERVER))
        {
            doServerTick();
        }

        if (type.contains(TickType.WORLD))
        {
            doWorldTick((World) tickData[0]);
        }
    }

    private void doWorldTick(World world)
    {
        /*
	     * Time settings
	     */
        if (!CommandDataManager.WTmap.containsKey(world.provider.dimensionId))
        {
            WeatherTimeData wt = new WeatherTimeData(world.provider.dimensionId);
            wt.freezeTime = world.getWorldTime();
            CommandDataManager.WTmap.put(world.provider.dimensionId, wt);
        }
        else
        {
            WeatherTimeData wt = CommandDataManager.WTmap.get(world.provider.dimensionId);
	        /*
	         * Weather part
	         */
            if (wt.weatherSpecified)
            {
                WorldInfo winfo = world.getWorldInfo();
                if (!wt.rain)
                {
                    winfo.setRainTime(20 * 300);
                    winfo.setRaining(false);
                    winfo.setThunderTime(20 * 300);
                    winfo.setThundering(false);
                }
                else if (!wt.storm)
                {
                    winfo.setThunderTime(20 * 300);
                    winfo.setThundering(false);
                }
            }
	        
	        /*
	         * Time part
	         */
            if (wt.timeFreeze)
            {
                world.setWorldTime(wt.freezeTime);
            }
            else if (wt.timeSpecified)
            {
                int h = getWorldHour(world);

                if (wt.day)
                {
                    if (h >= WeatherTimeData.dayTimeEnd)
                    {
                        makeWorldTimeHours(world, WeatherTimeData.dayTimeStart);
                    }
                }
                else
                {
                    if (h >= WeatherTimeData.nightTimeEnd)
                    {
                        makeWorldTimeHours(world, WeatherTimeData.nightTimeStart);
                    }
                }
            }
        }
    }

    private void doServerTick()
    {
	    /*
         * Kit system
         */
        for (Object player : FMLCommonHandler.instance().getMinecraftServerInstance().getConfigurationManager().playerEntityList)
        {
            PlayerInfo.getPlayerInfo(((EntityPlayer) player).username).KitCooldownTick();
        }

        /*
         * AFK system
         */
        try
        {
            afkList.addAll(afkListToAdd);
            afkListToAdd.clear();
            for (AFKdata data : afkList)
            {
                data.count();
            }
            afkList.removeAll(afkListToRemove);
            afkListToRemove.clear();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
        // Not needed here
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.SERVER, TickType.WORLD);
    }

    @Override
    public String getLabel()
    {
        return "FE_TickHandlerCommands";
    }

    @Override
    public int nextTickSpacing()
    {
        return 20;
    }
}
