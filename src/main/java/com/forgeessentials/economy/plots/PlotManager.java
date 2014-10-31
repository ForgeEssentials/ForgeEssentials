package com.forgeessentials.economy.plots;

import com.forgeessentials.data.AbstractDataDriver;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlotManager {

    private static AbstractDataDriver driver;
    private static ClassContainer plots = new ClassContainer(Plot.class);

    public static HashMap<String, Plot> plotList = new HashMap<>();

    public static Map<String, Offer> pendingOffers = new HashMap<>();

    public static List<String> forSale = new ArrayList<>();

    public static int timeout;

    public static void load()
    {
        driver = DataStorageManager.getReccomendedDriver();
        Object[] objs = driver.loadAllObjects(plots);
        for (Object obj : objs)
        {
            Plot plot = (Plot) obj;
            plotList.put(plot.getName(), plot);
        }
    }

    public static void save()
    {
        for (Plot plot : plotList.values())
        {
            driver.saveObject(plots, plot);
        }
    }

    public static void addPlot(Plot plot)
    {
        plotList.put(plot.getName(), plot);
        driver.saveObject(plots, plot);
    }

    public static void removePlot(String plotName)
    {
        plotList.remove(plotName);
        driver.deleteObject(plots, plotName);
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
