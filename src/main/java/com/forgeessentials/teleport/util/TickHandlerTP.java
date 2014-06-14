package com.forgeessentials.teleport.util;

import cpw.mods.fml.common.IScheduledTickHandler;
import cpw.mods.fml.common.TickType;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Use for all commands that need a cooldown, except for warp systems, see
 * TeleportCenter.
 *
 * @author Dries007
 */

public class TickHandlerTP implements IScheduledTickHandler {

	/*
     * For TPA system
	 */

    public static List<TPAdata> tpaList = new ArrayList<TPAdata>();
    public static List<TPAdata> tpaListToAdd = new ArrayList<TPAdata>();
    public static List<TPAdata> tpaListToRemove = new ArrayList<TPAdata>();

    @Override
    public void tickStart(EnumSet<TickType> type, Object... tickData)
    {
        try
        {
            tpaList.addAll(tpaListToAdd);
            tpaListToAdd.clear();
            for (TPAdata data : tpaList)
            {
                data.count();
            }
            tpaList.removeAll(tpaListToRemove);
            tpaListToRemove.clear();
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
        return "FE_TickHandlerTP";
    }

    @Override
    public int nextTickSpacing()
    {
        return 20;
    }
}
