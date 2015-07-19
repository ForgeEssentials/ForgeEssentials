package com.forgeessentials.util.events;

import com.forgeessentials.api.UserIdent;
import com.forgeessentials.economy.plots.Plot;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

public abstract class PlotEvent extends Event
{
    public final Plot plot;

    public PlotEvent(Plot plot)
    {
        this.plot = plot;
    }

    /**
     * Posted, when a plot is defined
     */
    @Cancelable
    public static class Define extends PlotEvent
    {
        public Define(Plot plot)
        {
            super(plot);
        }
    }

    /**
     * Posted, when a plot is deleted
     */
    @Cancelable
    public static class Delete extends PlotEvent
    {
        public Delete(Plot plot)
        {
            super(plot);
        }
    }

    /**
     * Posted, when the owner of a plot changed
     */
    public static class OwnerChanged extends PlotEvent
    {
        public final UserIdent oldOwner;

        public OwnerChanged(Plot plot, UserIdent oldOwner)
        {
            super(plot);
            this.oldOwner = oldOwner;
        }
    }

}
