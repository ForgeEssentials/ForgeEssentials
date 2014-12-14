package com.forgeessentials.economy.plots;


import com.forgeessentials.api.permissions.Zone;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.Map;

public class PlotManager {

    public static Map<String, Offer> pendingOffers = new HashMap<>();

    public static int timeout;

    public static final String PLOT_NAME_ID = "[PLOT]";
    public static final String PLOT_PERM = "fe.plot";
    public static final String PLOT_OWNER = PLOT_PERM + ".owner";
    public static final String PLOT_NAME_PERM = PLOT_PERM + ".name";

    // Represents an offer to transact a plot. Do not persist.
    public static class Offer
    {
        public Zone plot;
        public EntityPlayer buyer;
        public EntityPlayer seller;
        public int amount;

        public Offer(Zone plot, EntityPlayer buyer, EntityPlayer seller, int amount)
        {
            this.plot = plot;
            this.buyer = buyer;
            this.seller = seller;
            this.amount = amount;
        }
    }
}
