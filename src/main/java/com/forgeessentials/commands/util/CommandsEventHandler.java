package com.forgeessentials.commands.util;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import com.forgeessentials.commands.player.CommandNoClip;
import com.forgeessentials.util.events.ServerEventHandler;
import com.google.common.collect.HashMultimap;

public class CommandsEventHandler extends ServerEventHandler
{

    public static HashMultimap<PlayerEntity, PlayerInvChest> map = HashMultimap.create();

    public static int getWorldHour(World world)
    {
        return (int) ((world.getDayTime() % 24000) / 1000);
    }

    public static int getWorldDays(World world)
    {
        return (int) (world.getDayTime() / 24000);
    }

    public static void makeWorldTimeHours(World world, int target)
    {
        world.getTimeOfDay((getWorldDays(world) + 1) * 24000 + (target * 1000));
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
