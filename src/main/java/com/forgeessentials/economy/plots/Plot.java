package com.forgeessentials.economy.plots;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.command.ICommandSender;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.UserIdent;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.WorldZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.commons.selections.AreaBase;
import com.forgeessentials.commons.selections.WorldArea;
import com.forgeessentials.commons.selections.WorldPoint;
import com.forgeessentials.util.FunctionHelper;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.events.EventCancelledException;
import com.forgeessentials.util.events.PlotEvent;
import com.forgeessentials.util.events.PlotEvent.OwnerChanged;

public class Plot
{

    private static final String GROUP = Zone.GROUP_DEFAULT;

    private static final String SERVER_OWNER = "SERVER";

    private static Map<Integer, Plot> plots = new HashMap<>();

    private AreaZone zone;

    private UserIdent owner;

    private Plot(AreaZone zone, UserIdent owner)
    {
        this.zone = zone;
        this.owner = owner;
    }

    public Zone getZone()
    {
        return zone;
    }

    public boolean hasOwner()
    {
        return owner != null;
    }

    public UserIdent getOwner()
    {
        return owner;
    }

    public void setOwner(UserIdent newOwner)
    {
        if (newOwner == owner || (newOwner != null && newOwner.equals(owner)))
            return;
        OwnerChanged event = new PlotEvent.OwnerChanged(this, owner);
        if (owner != null)
            zone.removePlayerFromGroup(owner, PlotManager.GROUP_PLOT_OWNER);
        
        // Set new owner
        owner = newOwner;
        if (owner == null)
            zone.clearGroupPermission(GROUP, PlotManager.PERM_OWNER);
        else
            zone.setGroupPermissionProperty(GROUP, PlotManager.PERM_OWNER, owner.getOrGenerateUuid().toString());
        if (owner != null)
            zone.addPlayerToGroup(newOwner, PlotManager.GROUP_PLOT_OWNER);
        APIRegistry.getFEEventBus().post(event);
    }

    public String getName()
    {
        return zone.getGroupPermission(GROUP, PlotManager.PERM_NAME).replaceAll("@p", owner.getUsernameOrUUID());
    }

    public WorldPoint getPlotCenter()
    {
        return new WorldPoint(zone.getWorldZone().getDimensionID(), zone.getArea().getCenter());
    }

    /**
     * Gets the size that counts for price / limit calculation. Depending on whether the column flag is set or not, this
     * is the area or volume of the plot.
     * 
     * @return accounted size
     */
    public long getAccountedSize()
    {
        AreaBase area = zone.getArea();
        boolean columnMode = APIRegistry.perms.checkGroupPermission(GROUP, zone, PlotManager.PERM_PRICE);
        return columnMode ? area.getXLength() * area.getZLength() : area.getXLength() * area.getYLength() * area.getZLength();
    }

    public long getCalculatedPrice()
    {
        String priceStr = APIRegistry.perms.getGroupPermissionProperty(GROUP, getPlotCenter(), PlotManager.PERM_PRICE);
        if (priceStr == null)
            return 0;
        double pricePerUnit = FunctionHelper.parseDoubleDefault(priceStr, 0);
        if (pricePerUnit == 0)
            return 0;
        return (long) (getAccountedSize() * pricePerUnit);
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

    /* ------------------------------------------------------------ */

    private static void registerPlot(Plot plot)
    {
        plots.put(plot.getZone().getId(), plot);
    }

    public static Plot define(WorldArea area, UserIdent owner) throws EventCancelledException
    {
        WorldZone worldZone = APIRegistry.perms.getServerZone().getWorldZone(area.getDimension());

        // TODO: Check for exisiting plot

        AreaZone zone = new AreaZone(worldZone, "_PLOT_" + (APIRegistry.perms.getServerZone().getMaxZoneID() + 1), area);
        Plot plot = new Plot(zone, owner);
        registerPlot(plot);
        zone.setHidden(true);
        zone.setGroupPermissionProperty(GROUP, PlotManager.PERM_NAME, owner == null ? SERVER_OWNER : owner.getOrGenerateUuid().toString());
        if (owner != null)
            zone.addPlayerToGroup(owner, PlotManager.GROUP_PLOT_OWNER);
        return plot;
    }

    public static void loadPlots()
    {
        plots.clear();
        for (Zone zone : APIRegistry.perms.getZones())
            if (zone instanceof AreaZone)
            {
                String ownerId = zone.getGroupPermission(GROUP, PlotManager.PERM_OWNER);
                if (ownerId != null)
                    registerPlot(new Plot((AreaZone) zone, ownerId.equals(SERVER_OWNER) ? null : new UserIdent(ownerId)));
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

    /**
     * Gets the size that counts for price / limit calculation. Depending on whether the column flag is set or not, this
     * is the area or volume of the plot.
     * 
     * @return accounted size
     */
    public static long getAccountedSize(WorldArea area)
    {
        String permValue = APIRegistry.perms.getPermission(null, null, area, Arrays.asList(GROUP), PlotManager.PERM_PRICE, true);
        boolean columnMode = APIRegistry.perms.checkBooleanPermission(permValue);
        return columnMode ? area.getXLength() * area.getZLength() : area.getXLength() * area.getYLength() * area.getZLength();
    }

    public static long getCalculatedPrice(WorldArea area)
    {
        String priceStr = APIRegistry.perms.getGroupPermissionProperty(GROUP, area.getCenter(), PlotManager.PERM_PRICE);
        if (priceStr == null)
            return 0;
        double pricePerUnit = FunctionHelper.parseDoubleDefault(priceStr, 0);
        if (pricePerUnit == 0)
            return 0;
        return (long) (getAccountedSize(area) * pricePerUnit);
    }

}
