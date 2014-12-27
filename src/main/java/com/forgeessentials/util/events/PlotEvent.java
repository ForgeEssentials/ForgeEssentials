package com.forgeessentials.util.events;

import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.economy.plots.PlotManager;
import cpw.mods.fml.common.eventhandler.Cancelable;
import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.entity.player.EntityPlayer;

public class PlotEvent extends Event
{
    public AreaZone plot;
    public EntityPlayer player;

    public PlotEvent(AreaZone plot, EntityPlayer player)
    {
        this.plot = plot;
        this.player = player;
    }

    @Cancelable
    public static class Define extends PlotEvent
    {
        public Define(AreaZone plot, EntityPlayer player)
        {
            super(plot, player);
        }
    }
}
