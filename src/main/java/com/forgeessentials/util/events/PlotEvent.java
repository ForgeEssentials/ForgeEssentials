package com.forgeessentials.util.events;

import com.forgeessentials.api.permissions.AreaZone;
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

    /**
     * Thrown when a plot is defined.
     * player is set to the new owner.
     */
    @Cancelable
    public static class Define extends PlotEvent
    {
        public Define(AreaZone plot, EntityPlayer player)
        {
            super(plot, player);
        }
    }

    /**
     * Thrown when the owner of a plot is set.
     * player is set to the new owner.
     */
    @Cancelable
    public static class OwnerSet extends PlotEvent
    {
        public OwnerSet(AreaZone plot, EntityPlayer player)
        {
            super(plot, player);
        }
    }

    /**
     * Thrown when someone is removed from ownership of a plot. (plot trading, forced repossession by admin, etc)
     * player is set to the player who was removed.
     */
    public static class OwnerUnset extends PlotEvent
    {
        public OwnerUnset(AreaZone plot, EntityPlayer player)
        {
            super(plot, player);
        }
    }

    /**
     * Thrown when the rent manager receives a rent payment.
     * player is set to the player paying the rent
     * amount is set to the amount of rent paid
     */
    public static class RentPaid extends PlotEvent
    {
        
        public int rentAmount;

        private RentPaid(AreaZone plot, EntityPlayer player, int rentAmount)
        {
            super(plot, player);
            this.rentAmount = rentAmount;
        }

    }

    /**
     * Thrown when the rent manager finds that a player has defaulted on his rent payments.\
     * player is set to the defaulting player.
     */
    public static class RentDefaulted extends PlotEvent
    {
        public RentDefaulted(AreaZone plot, EntityPlayer player)
        {
            super(plot, player);
        }
    }
}
