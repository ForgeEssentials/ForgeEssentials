package com.forgeessentials.economy.plots;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.command.ICommandSender;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commons.selections.AreaBase;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;

public class Plot
{

    private static final String GROUP = Zone.GROUP_DEFAULT;

    private static Map<Integer, Plot> plots = new HashMap<>();

    private AreaZone zone;

    private UserIdent owner;

    public Plot(AreaZone zone, UserIdent owner)
    {
        this.zone = zone;
        this.owner = owner;
    }

    public Zone getZone()
    {
        return zone;
    }

    public UserIdent getOwner()
    {
        return owner;
    }

    public String getName()
    {
        return zone.getGroupPermission(GROUP, PlotManager.PERM_NAME).replaceAll("@p", owner.getUsernameOrUUID());
    }

    public WorldPoint getPlotCenter()
    {
        return new WorldPoint(zone.getWorldZone().getDimensionID(), zone.getArea().getCenter());
    }

    public long getPrice()
    {
        try
        {
            String price = zone.getGroupPermission(GROUP, PlotManager.PERM_SELL_PRICE);
            if (price == null)
                return -1;
            return Long.parseLong(price);
        }
        catch (NumberFormatException e)
        {
            return -1;
        }
    }

    public long getCalculatedPrice()
    {
        AreaBase area = zone.getArea();
        String priceStr = APIRegistry.perms.getGroupPermissionProperty(GROUP, getPlotCenter(), PlotManager.PERM_PRICE);
        if (priceStr == null)
            return 0;
        double pricePerUnit = FunctionHelper.parseDoubleDefault(priceStr, 0);
        if (pricePerUnit == 0)
            return 0;
        boolean columnMode = APIRegistry.perms.checkGroupPermission(GROUP, zone, PlotManager.PERM_PRICE);
        return (long) (columnMode ? area.getXLength() * area.getZLength() * pricePerUnit : //
                area.getXLength() * area.getYLength() * area.getZLength() * pricePerUnit);
    }

    public void setOwner(UserIdent newOwner)
    {
        owner = newOwner;
        zone.setGroupPermissionProperty(GROUP, PlotManager.PERM_OWNER, owner.getOrGenerateUuid().toString());
    }

    public void printDetails(ICommandSender sender)
    {
        OutputHandler.chatNotification(sender, String.format("Plot #%d: %s", zone.getId(), getName()));
        OutputHandler.chatNotification(sender, String.format("  Owner: %s", owner.getUsernameOrUUID()));
        OutputHandler.chatNotification(sender,
                String.format("  Location between %s and %s", zone.getArea().getHighPoint().toString(), zone.getArea().getLowPoint().toString()));
        long price = getPrice();
        if (price >= 0)
            OutputHandler.chatNotification(sender, String.format("  Price: %d", price));
        else
            OutputHandler.chatNotification(sender, "  Not open for sale");
    }

    public static void registerPlot(Plot plot)
    {
        if (plots.containsKey(plot.zone.getId()))
            throw new RuntimeException("Registered plot twice");
        plots.put(plot.getZone().getId(), plot);
    }

    public static void registerPlots()
    {
        plots.clear();
        for (Zone zone : APIRegistry.perms.getZones())
            if (zone instanceof AreaZone)
            {
                String ownerId = zone.getGroupPermission(GROUP, PlotManager.PERM_OWNER);
                if (ownerId != null)
                    registerPlot(new Plot((AreaZone) zone, new UserIdent(ownerId)));
            }
    }

    public static Plot getPlot(int zoneId)
    {
        return plots.get(zoneId);
    }

    public static Collection<Plot> getPlots()
    {
        return plots.values();
    }

    public static Plot getPlot(WorldPoint point)
    {
        List<Zone> zones = APIRegistry.perms.getServerZone().getZonesAt(point);
        for (Zone zone : zones)
        {
            Plot plot = plots.get(zone.getId());
            if (plot != null)
                return plot;
        }
        return null;
    }

}
