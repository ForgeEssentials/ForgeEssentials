package com.forgeessentials.economy;

import com.forgeessentials.api.APIRegistry;
import com.forgeessentials.api.permissions.AreaZone;
import com.forgeessentials.api.permissions.IPermissionsHelper;
import com.forgeessentials.api.permissions.Zone;
import com.forgeessentials.util.OutputHandler;
import com.forgeessentials.util.UserIdent;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlotManager {

    public static Map<String, Offer<Zone>> pendingOffers = new HashMap<>();

    public static int timeout;

    public static final String PLOT_NAME_ID = "[PLOT]";
    public static final String PLOT_PERM = "fe.plot";
    public static final String PLOT_OWNER = PLOT_PERM + ".owner";
    public static final String PLOT_NAME_PERM = PLOT_PERM + ".name";
    public static final String PLOT_VALUE = PLOT_PERM + ".value";

    public static void printPlotDetails(ICommandSender sender, AreaZone plot)
    {
        if (!plot.checkGroupPermission(IPermissionsHelper.GROUP_DEFAULT, PlotManager.PLOT_PERM)) return;
        OutputHandler.chatNotification(sender,
                "Name: " + plot.getGroupPermission(IPermissionsHelper.GROUP_DEFAULT, PlotManager.PLOT_NAME_PERM) + " Owner: " + UserIdent
                        .getUsernameByUuid(plot.getGroupPermission(IPermissionsHelper.GROUP_DEFAULT, PlotManager.PLOT_OWNER)) + "Location: between " + plot
                        .getArea().getHighPoint().toString() + " and " + plot.getArea().getLowPoint().toString() + " Value: " + plot.getGroupPermission(IPermissionsHelper.GROUP_DEFAULT, PLOT_VALUE));
    }

    public static AreaZone[] getPlotList()
    {
        List<AreaZone> zones = new ArrayList<AreaZone>();
        for (Zone zone : APIRegistry.perms.getZones())
        {
            if (zone.checkGroupPermission(IPermissionsHelper.GROUP_DEFAULT, PlotManager.PLOT_PERM) && zone instanceof AreaZone)
                zones.add((AreaZone)zone);
        }
        return zones.toArray(new AreaZone[]{});
    }
}
