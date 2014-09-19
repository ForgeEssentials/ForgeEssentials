package com.forgeessentials.economy.plots;

import com.forgeessentials.data.AbstractDataDriver;
import com.forgeessentials.data.api.ClassContainer;
import com.forgeessentials.data.api.DataStorageManager;

import java.util.HashMap;

public class PlotManager {

    private static AbstractDataDriver driver;
    private static ClassContainer plots = new ClassContainer(Plot.class);

    public static HashMap<String, Plot> plotList = new HashMap<>();

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
}
