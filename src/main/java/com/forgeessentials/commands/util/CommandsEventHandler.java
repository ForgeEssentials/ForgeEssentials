package com.forgeessentials.commands.util;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import com.forgeessentials.commands.player.CommandNoClip;
import com.forgeessentials.util.events.ServerEventHandler;
import com.google.common.collect.HashMultimap;

public class CommandsEventHandler extends ServerEventHandler
{

    public static HashMultimap<EntityPlayer, PlayerInvChest> map = HashMultimap.create();

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

    public static void register(PlayerInvChest inv)
    {
        map.put(inv.owner, inv);
    }

    public static void remove(PlayerInvChest inv)
    {
        map.remove(inv.owner, inv);
    }

    public CommandsEventHandler()
    {
        super();
    }

    @SubscribeEvent
    public void doWorldTick(TickEvent.WorldTickEvent e)
    {
        /*
         * Time settings
         */
        if (!CommandDataManager.WTmap.containsKey(e.world.provider.getDimensionId()))
        {
            WeatherTimeData wt = new WeatherTimeData(e.world.provider.getDimensionId());
            wt.freezeTime = e.world.getWorldTime();
            CommandDataManager.WTmap.put(e.world.provider.getDimensionId(), wt);
        }
        else
        {
            WeatherTimeData wt = CommandDataManager.WTmap.get(e.world.provider.getDimensionId());
            /*
             * Weather part
             */
            if (wt.weatherSpecified)
            {
                WorldInfo winfo = e.world.getWorldInfo();
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
                e.world.setWorldTime(wt.freezeTime);
            }
            else if (wt.timeSpecified)
            {
                int h = getWorldHour(e.world);

                if (wt.day)
                {
                    if (h >= WeatherTimeData.dayTimeEnd)
                    {
                        makeWorldTimeHours(e.world, WeatherTimeData.dayTimeStart);
                    }
                }
                else
                {
                    if (h >= WeatherTimeData.nightTimeEnd)
                    {
                        makeWorldTimeHours(e.world, WeatherTimeData.nightTimeStart);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void tickStart(TickEvent.PlayerTickEvent event)
    {
        if (map.containsKey(event.player))
        {
            for (PlayerInvChest inv : map.get(event.player))
            {
                inv.update();
            }
        }
        if (event.phase == TickEvent.Phase.END)
            CommandNoClip.checkClip(event.player);
    }

}
