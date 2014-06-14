package com.forgeessentials.commands.util;

import com.google.common.collect.HashMultimap;
import cpw.mods.fml.common.ITickHandler;
import cpw.mods.fml.common.TickType;
import net.minecraft.entity.player.EntityPlayer;

import java.util.EnumSet;

public class InvSeeMisk implements ITickHandler {
    public static HashMultimap<EntityPlayer, PlayerInvChest> map = HashMultimap.create();

    public static void register(PlayerInvChest inv)
    {
        map.put(inv.owner, inv);
    }

    public static void remove(PlayerInvChest inv)
    {
        map.remove(inv.owner, inv);
    }

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
        if (map.containsKey(tickData[0]))
        {
            for (PlayerInvChest inv : map.get((EntityPlayer) tickData[0]))
            {
                inv.update();
            }
        }
    }

    @Override
    public void tickEnd(EnumSet<TickType> type, Object... tickData)
    {
    }

    @Override
    public EnumSet<TickType> ticks()
    {
        return EnumSet.of(TickType.PLAYER);
    }

    @Override
    public String getLabel()
    {
        return "invSee ticker";
    }
}
