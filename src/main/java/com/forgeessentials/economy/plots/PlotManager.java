package com.forgeessentials.economy.plots;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.entity.player.EntityPlayer;

import com.forgeessentials.data.v2.DataManager;

public class PlotManager {

    public static HashMap<String, Plot> plotList = new HashMap<>();

    public static Map<String, Offer> pendingOffers = new HashMap<>();

    public static List<String> forSale = new ArrayList<>();

    public static int timeout;

    public static void load()
    {
        Map<String, Plot> plots = DataManager.getInstance().loadAll(Plot.class);
        for (Plot plot : plots.values())
            plotList.put(plot.getName(), plot);
    }

    public static void save()
    {
        for (Plot plot : plotList.values())
            DataManager.getInstance().save(plot, plot.getName());
    }

    public static void addPlot(Plot plot)
    {
        plotList.put(plot.getName(), plot);
        DataManager.getInstance().save(plot, plot.getName());
    }

    public static void removePlot(String plotName)
    {
        plotList.remove(plotName);
        DataManager.getInstance().delete(Plot.class, plotName);
    }

    // Represents an offer to transact a plot. Do not persist.
    public static class Offer
    {
        public Plot plot;
        public EntityPlayer buyer;
        public EntityPlayer seller;
        public int amount;

        public Offer(Plot plot, EntityPlayer buyer, EntityPlayer seller, int amount)
        {
            this.plot = plot;
            this.buyer = buyer;
            this.seller = seller;
            this.amount = amount;
        }
    }
}
