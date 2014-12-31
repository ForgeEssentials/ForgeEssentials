package com.forgeessentials.economy.plots;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.economy.Offer;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;
import net.minecraft.command.ICommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlotManager {

    public static Map<String, Offer<AreaZone>> pendingOffers = new HashMap<>();

    public static int timeout;

    public static final String PLOT_PERM = "fe.economy.plots";

    public static final String PLOT_NAME_ID = "[PLOT]";
    public static final String DATA_PERM = PLOT_PERM + ".data";
    public static final String PLOT_OWNER = DATA_PERM + ".owner";
    public static final String PLOT_NAME_PERM = DATA_PERM + ".name";
    public static final String PLOT_VALUE = DATA_PERM + ".value";

    public static final String PLOT_PERMPROP_CLAIMCAP = PLOT_PERM + ".claimcap";
    public static final String PLOT_PERMPROP_CLAIMED = PLOT_PERM + ".claimed";

    public static void printPlotDetails(ICommandSender sender, AreaZone plot)
    {
        if (!plot.checkGroupPermission(Zone.GROUP_DEFAULT, PlotManager.PLOT_PERM)) return;
        OutputHandler.chatNotification(sender,
                "Name: " + plot.getGroupPermission(Zone.GROUP_DEFAULT, PlotManager.PLOT_NAME_PERM) + " Owner: " + UserIdent
                        .getUsernameByUuid(plot.getGroupPermission(Zone.GROUP_DEFAULT, PlotManager.PLOT_OWNER)) + "Location: between " + plot
                        .getArea().getHighPoint().toString() + " and " + plot.getArea().getLowPoint().toString() + " Value: " + plot.getGroupPermission(Zone.GROUP_DEFAULT, PLOT_VALUE));
    }

    public static AreaZone[] getPlotList()
    {
        List<AreaZone> zones = new ArrayList<AreaZone>();
        for (Zone zone : APIRegistry.perms.getZones())
        {
            if (zone.checkGroupPermission(Zone.GROUP_DEFAULT, PlotManager.PLOT_PERM) && zone instanceof AreaZone)
                zones.add((AreaZone)zone);
        }
        return zones.toArray(new AreaZone[]{});
    }
}
